package com.ams.cavus.todo.list.di

import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.list.viewmodel.TodoViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TodoModule {

    @Singleton
    @Provides
    fun provideTodoViewModel(app: App) = TodoViewModel(app)

}