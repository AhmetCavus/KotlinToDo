package com.ams.cavus.todo.db.service

import com.ams.cavus.todo.helper.Settings
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser
import com.microsoft.windowsazure.mobileservices.table.query.Query
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations



typealias SyncCompleteListener<TEntity> = (MutableList<TEntity>) -> Unit

abstract class AzureEntityService<TEntity>(protected val client: MobileServiceClient, protected val gson: Gson, protected var settings: Settings) {

    var itemsCache = mutableListOf<TEntity>()

    protected lateinit var table: MobileServiceSyncTable<TEntity>

    fun fetch(query: Query?, syncCompleteListener: SyncCompleteListener<TEntity>)
    {
        //Initialize & Sync
        sync {
            doAsync {
                itemsCache = table.read(query).get()
                uiThread { syncCompleteListener.invoke(itemsCache) }
            }
        }
    }

    fun sync(callback: () -> Unit)
    {
        doAsync {
            initialize()
            //pull down all latest changes and then push current coffees up
            table.pull(null, onQueryId()).get()
            client.syncContext.push().get()
            uiThread { callback() }
        }
    }

    fun initialize() {
        if(client.syncContext?.isInitialized == true) return
        if(!settings.isCredentials) return

        client.apply {
            currentUser = MobileServiceUser(settings.credentials.userId)
            currentUser.authenticationToken = settings.credentials.authToken
        }

        //setup our local sqlite store and intialize our table
        var store = SQLiteLocalStore(client.context, onGetLocalDbName(), null, 1)

        val definition = onCreateDefinition()


        //Define table
        store.defineTable(onGetTableName(), definition)

        //Initialize SyncContext
        client.syncContext.initialize(store, SimpleSyncHandler()).get()

        //Get our sync table that will call out to azure
        table = onCreateTable()
    }

    abstract fun onCreateTable(): MobileServiceSyncTable<TEntity>
    abstract fun onCreateDefinition(): Map<String, ColumnDataType>
    abstract fun onGetLocalDbName(): String
    abstract fun onGetTableName(): String
    abstract fun onQueryId(): String
}