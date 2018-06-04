package com.ams.cavus.todo.base

import android.app.Application
import com.ams.cavus.todo.base.di.AppComponent
import com.ams.cavus.todo.base.di.AppModule
import com.ams.cavus.todo.base.di.DaggerAppComponent

class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }

}

// Quantum Ödi