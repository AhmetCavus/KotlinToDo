package com.ams.cavus.todo.list.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import com.ams.cavus.todo.list.TodoActivity
import com.ams.cavus.todo.client.AzureCredentials
import com.ams.cavus.todo.client.AzureService
import com.ams.cavus.todo.client.GoogleService
import com.ams.cavus.todo.db.DbService
import com.ams.cavus.todo.login.model.LoginDataModel
import com.example.amstodo.util.SingleLiveEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider
import javax.inject.Inject

class TodoViewModel (private val app: Application) : AndroidViewModel(app), LifecycleObserver{

    companion object {
        const val GOOGLE_CALLBACK = 101
        const val MICROSOFT_CALLBACK = 102
        const val AD_CALLBACK = 103
    }

    @Inject
    lateinit var azureService: AzureService

    @Inject
    lateinit var dbService: DbService

    var currentCallbackId = 0

    val startActivityForResultEvent = SingleLiveEvent<Intent>()
    val startActivityEvent = SingleLiveEvent<Intent>()

    fun signOut() {
        azureService.logout(
            { loggedOutUser -> },
            { exception ->  }
        )
    }

}