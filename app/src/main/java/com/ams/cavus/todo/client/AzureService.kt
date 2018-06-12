package com.ams.cavus.todo.client

import android.content.Intent
import com.ams.cavus.client.AzureApi
import com.ams.cavus.todo.list.model.ToDoItem
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable
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

class AzureService(private val client: MobileServiceClient, private val gson: Gson) {

    lateinit var currentCredentials: AzureCredentials
    lateinit var todoTable: MobileServiceSyncTable<ToDoItem>

    fun login(credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {
            if (credentials == null) {
                uiThread { fail.invoke(Exception("Invalid state")) }
            } else {
                val oauthToken = gson.toJson(credentials)
                val authTask = client.login(credentials.provider, oauthToken)
                // Signed in successfully, show authenticated UI.
                authTask.doAsyncResult {
                    currentCredentials = credentials
                    if (!authTask.isDone) return@doAsyncResult
                    authTask.get().apply {
                        credentials.authToken = this.authenticationToken
                        credentials.userId = this.userId
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
                currentCredentials = credentials
                authTask.get().apply {
                    credentials.authToken = this.authenticationToken
                    credentials.userId = this.userId
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

    fun onActivityResult(data: Intent) = client.onActivityResult(data)

    fun initialize() {
        //InitialzeDatabase for path
        var path = "syncstore.db";
//        path = Path.Combine(MobileServiceClient.DefaultDatabasePath, path);

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
        todoTable = client.getSyncTable(ToDoItem::class.java)
    }

}