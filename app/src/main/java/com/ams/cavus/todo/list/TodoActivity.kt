package com.ams.cavus.todo.list

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ams.cavus.todo.R
import com.ams.cavus.todo.databinding.ActivityTodoBinding
import com.ams.cavus.todo.list.viewmodel.TodoViewModel
import com.ams.cavus.todo.util.app
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import javax.inject.Inject

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, userName address, and basic
 * profile.
 */
class TodoActivity : AppCompatActivity() {

    private val lifecycleRegistry = LifecycleRegistry(this)

    private lateinit var viewDataBinding: ActivityTodoBinding

    private lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var viewModel: TodoViewModel

    @Inject
    lateinit var client: MobileServiceClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The layout for this activity is a Data Binding layout so it needs to be inflated using
        viewDataBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_todo)

        app.component.inject(this)
        app.component.inject(viewModel)

        client.context = this

        viewDataBinding.vm = viewModel.apply {
            lifecycleRegistry.addObserver(this)
        }

        lifecycleRegistry.markState(Lifecycle.State.CREATED)

        linearLayoutManager = LinearLayoutManager(this)
        viewDataBinding.recyclerView.apply {
            layoutManager = linearLayoutManager
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

}