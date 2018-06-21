package com.ams.cavus.todo.list.service

import com.ams.cavus.todo.db.service.AzureEntityService
import com.ams.cavus.todo.helper.Settings
import com.ams.cavus.todo.list.model.Todo
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType
import org.jetbrains.anko.doAsync

typealias AddTodoComplete = (Todo) -> Unit
typealias PushComplete = () -> Unit

class TodoService(client: MobileServiceClient, gson: Gson, settings: Settings) : AzureEntityService<Todo>(client, gson, settings) {

    override fun onGetTableName() = "todo"

    override fun onQueryId() = "todoQuery"

    override fun onGetLocalDbName() = "todoLocalDb"

    override fun onCreateDefinition(): Map<String, ColumnDataType> =
        mapOf(
            "id" to ColumnDataType.String,
            "userId" to ColumnDataType.String,
            "text" to ColumnDataType.String,
            "deleted" to ColumnDataType.Boolean,
            "completed" to ColumnDataType.Boolean,
            "createdAt" to ColumnDataType.DateTimeOffset
        )

    override fun onCreateTable(): MobileServiceSyncTable<Todo> = client.getSyncTable(Todo::class.java)

    fun add(userId: String, text: String, listener: AddTodoComplete)
    {
        doAsync {
            initialize()
            var entity = Todo(userId, text)
            val res = table.insert(entity).get()
            sync { listener.invoke(res) }
        }
    }

    fun pushCache(listener: PushComplete)
    {
        doAsync {
            initialize()
            itemsCache.forEach { entity -> table.insert(entity).get() }
            sync { listener.invoke() }
        }
    }
}