package com.ams.cavus.todo.list.di

import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.helper.Settings
import com.ams.cavus.todo.list.service.TodoService
import com.ams.cavus.todo.list.viewmodel.TodoViewModel
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TodoModule {

    @Singleton
    @Provides
    fun provideTodoViewModel(app: App) = TodoViewModel(app)

    @Singleton
    @Provides
    fun provideTodoService(client: MobileServiceClient, gson: Gson, settings: Settings) = TodoService(client, gson, settings)
}