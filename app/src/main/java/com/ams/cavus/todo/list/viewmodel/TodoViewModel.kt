package com.ams.cavus.todo.list.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.ams.cavus.todo.client.AzureService
import com.ams.cavus.todo.list.TodoListAdapter
import javax.inject.Inject

class TodoViewModel (private val app: Application) : AndroidViewModel(app), LifecycleObserver{

    val adapter = TodoListAdapter()

    @Inject
    lateinit var azureService: AzureService

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        azureService.fetchTodos {
            adapter.todoItems = it
            adapter.notifyDataSetChanged()
        }
    }


    var count = 0
    fun onAddItem() {
        count+= 1
        azureService.addTodo("Text $count") { todoItem ->
            adapter.notifyDataSetChanged()
        }
    }

}