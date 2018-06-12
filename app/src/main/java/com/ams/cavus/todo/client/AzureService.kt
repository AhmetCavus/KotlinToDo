package com.ams.cavus.todo.client

import android.content.Intent
import com.ams.cavus.client.AzureApi
import com.ams.cavus.todo.helper.Settings
import com.ams.cavus.todo.list.model.ToDoItem
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.MobileServiceList
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread

typealias AuthSuccess = (AzureCredentials) -> Unit
typealias AuthFail = (Exception) -> Unit
typealias LogoutSuccess = (MobileServiceUser) -> Unit
typealias LogoutFail = (Exception) -> Unit
typealias FetchListComplete = (MobileServiceList<ToDoItem>) -> Unit
typealias AddItemComplete = (ToDoItem) -> Unit

class AzureService(private val client: MobileServiceClient, private val gson: Gson, private var settings: Settings) {

    var currentCredentials = emptyCredentials()

    val isLoggedIn: Boolean
        get() = client?.currentUser?.authenticationToken?.isNotEmpty() ?: false

    private lateinit var todoTable: MobileServiceTable<ToDoItem>

    init {
        if (client.currentUser != null) {
            initCredentials()
        }
    }

    fun login(credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {
            if (credentials == null) {
                uiThread { fail.invoke(Exception("Invalid state")) }
            } else {
                val oauthToken = gson.toJson(credentials)
                val authTask = client.login(credentials.provider, oauthToken)
                // Signed in successfully, show authenticated UI.
                authTask.doAsyncResult {
                    if (!authTask.isDone) return@doAsyncResult
                    authTask.get().apply {
                        credentials.authToken = authenticationToken
                        credentials.userId = userId
                        currentCredentials = credentials
                        settings.saveCredentials(credentials.authToken, credentials.userId)
                    }
                    uiThread { success.invoke(credentials) }
                }
            }
        }
    }

    fun login(provider: String, credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {

            val authTask = client.login(provider, gson.toJson(credentials))
            // Signed in successfully, show authenticated UI.
            authTask.doAsyncResult {
//                if (!authTask.isDone) return@doAsyncResult
                authTask.get().apply {
                    credentials.authToken = authenticationToken
                    credentials.userId = userId
                    currentCredentials = credentials
                    settings.saveCredentials(credentials.authToken, credentials.userId)
                }
                uiThread { success.invoke(credentials) }
            }
        }
    }

    fun login(provider: String, requestCode: Int) {
        client.login(provider, AzureApi.URI_SCHEME, requestCode)
    }

    fun logout(success: LogoutSuccess, fail: LogoutFail) {
        val logoutTask = client.logout()
        logoutTask.doAsyncResult {
            if (!logoutTask.isDone) return@doAsyncResult
            uiThread { success.invoke(client.currentUser) }
        }
    }

    fun FetchTodos(syncCompleteListener: FetchListComplete)
    {
        //Initialize & Sync
        doAsync {
            initialize()
            val todoItems = todoTable.select().execute().get()
            uiThread {
                syncCompleteListener.invoke(todoItems)
            }
        }
    }

    fun syncTodos()
    {
        doAsync {
            client.syncContext.push().get()
            print("Done!!!")
        }
    }

    fun addTodo(text: String, listener: AddItemComplete)
    {
        doAsync {
            initialize()
            var todoItem = ToDoItem(text, currentCredentials.email)

            val res = todoTable.insert(todoItem).get()
            syncTodos()
            uiThread { listener.invoke(res) }
        }
    }

    fun onActivityResult(data: Intent): MobileServiceActivityResult {
        return client.onActivityResult(data).apply {
            if(isLoggedIn) {
                settings.saveCredentials(
                        client.currentUser?.authenticationToken ?: "",
                        client.currentUser.userId ?: "")
                initCredentials()
                initialize()
            }
        }
    }

    fun initialize() {
        if(client?.syncContext?.isInitialized == true) return
        if(!settings?.isCredentials) return

        client.apply {
            currentUser = MobileServiceUser(settings.credentials.userId)
            currentUser.authenticationToken = settings.credentials.authToken
        }

        //InitialzeDatabase for path
        var path = "syncstore.db"

        //setup our local sqlite store and intialize our table
        var store = SQLiteLocalStore(client.context, "TodoStore", null, 1)

        val definition =
        mapOf(
            "id" to ColumnDataType.String,
            "text" to ColumnDataType.String,
            "complete" to ColumnDataType.Boolean
        )

        //Define table
        store.defineTable("TodoTable", definition)

        //Initialize SyncContext
        client.syncContext.initialize(store, SimpleSyncHandler()).get()

        //Get our sync table that will call out to azure
        todoTable = client.getTable(ToDoItem::class.java)
    }

    private fun initCredentials() {
        currentCredentials = emptyCredentials().apply {
            authToken = client.currentUser.authenticationToken
            userId = client.currentUser.userId
        }
    }

}