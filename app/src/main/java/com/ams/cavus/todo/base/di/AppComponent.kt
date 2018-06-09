package com.ams.cavus.todo.base.di

import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.list.TodoActivity
import com.ams.cavus.todo.list.di.TodoModule
import com.ams.cavus.todo.list.di.TodoModule_ProvideTodoViewModelFactory
import com.ams.cavus.todo.list.viewmodel.TodoViewModel
import com.ams.cavus.todo.login.LoginActivity
import com.ams.cavus.todo.login.di.LoginModule
import com.ams.cavus.todo.login.viewmodel.LoginViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        LoginModule::class,
        TodoModule::class))

interface AppComponent {
    fun inject(app: App)
    fun inject(activity: LoginActivity)
    fun inject(viewModel: LoginViewModel)
    fun inject(activity: TodoActivity)
    fun inject(viewModel: TodoViewModel)
}