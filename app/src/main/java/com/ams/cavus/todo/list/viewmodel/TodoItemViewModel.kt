package com.ams.cavus.todo.list.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.ams.cavus.todo.BR
import com.ams.cavus.todo.list.model.ToDoItem

class TodoItemViewModel(private val toDoItem: ToDoItem) : BaseObservable(){

    var id: String = toDoItem.id
        @Bindable get
        set(value) {
            field = value
            toDoItem.id = value
            notifyPropertyChanged(BR.id)
        }

    var text: String = toDoItem.text
        @Bindable get
        set(value) {
            field = value
            toDoItem.text = value
            notifyPropertyChanged(BR.text)
        }

    var completed: Boolean = toDoItem.isComplete
        @Bindable get
        set(value) {
            field = value
            toDoItem.isComplete = value
            notifyPropertyChanged(BR.completed)
        }

}