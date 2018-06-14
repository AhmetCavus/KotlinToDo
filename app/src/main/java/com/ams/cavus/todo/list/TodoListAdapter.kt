package com.ams.cavus.todo.list

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ams.cavus.todo.R
import com.ams.cavus.todo.databinding.ListitemTodoBinding
import com.ams.cavus.todo.list.model.Todo
import com.ams.cavus.todo.list.viewmodel.TodoItemViewModel


class TodoListAdapter: RecyclerView.Adapter<TodoListAdapter.TodoItemHolder>() {

    lateinit var viewDataBinding: ListitemTodoBinding

    var todoItems = mutableListOf<Todo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemHolder {
        viewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.listitem_todo, parent, false)
        return TodoItemHolder(viewDataBinding)
    }

    override fun getItemCount() = todoItems.size

    override fun onBindViewHolder(holder: TodoItemHolder, position: Int) {
        holder.listItemBinding.vm = TodoItemViewModel(todoItems[position])
    }

    class TodoItemHolder(val listItemBinding: ListitemTodoBinding) : RecyclerView.ViewHolder(listItemBinding.root)

}