package com.ams.cavus.todo.base.di

import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.login.di.LoginModule
import com.ams.cavus.todo.login.login.LoginActivity
import com.ams.cavus.todo.login.viewmodel.LoginViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        LoginModule::class))

interface AppComponent {
    fun inject(app: App)
    fun inject(activity: LoginActivity)
    fun inject(viewModel: LoginViewModel)
}