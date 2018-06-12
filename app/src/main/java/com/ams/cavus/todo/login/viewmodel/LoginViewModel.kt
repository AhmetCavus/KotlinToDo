package com.ams.cavus.todo.login.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import com.ams.cavus.todo.client.AzureCredentials
import com.ams.cavus.todo.client.AzureService
import com.ams.cavus.todo.client.GoogleService
import com.ams.cavus.todo.list.TodoActivity
import com.ams.cavus.todo.login.model.LoginDataModel
import com.example.amstodo.util.SingleLiveEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider
import javax.inject.Inject

class LoginViewModel (private val app: Application) : AndroidViewModel(app), LifecycleObserver{

    companion object {
        const val GOOGLE_CALLBACK = 101
        const val MICROSOFT_CALLBACK = 102
        const val AD_CALLBACK = 103
        const val TWITTER_CALLBACK = 104
    }

    @Inject
    lateinit var azureService: AzureService

    @Inject
    lateinit var googleService: GoogleService

    @Inject
    lateinit var model: LoginDataModel

    var currentCallbackId = 0

    val startActivityForResultEvent = SingleLiveEvent<Intent>()
    val startActivityEvent = SingleLiveEvent<Intent>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        relogin()
    }

    private fun relogin() {
        azureService.initialize()
        if(!azureService.isLoggedIn) return
        showTodoList()
    }

    fun handleGoogleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = completedTask.getResult(ApiException::class.java)
            googleService.loadCredentials(app, account) {
                credentials ->
                    azureService.login(
                            "Google",
                            credentials,
                            { user -> showTodoList()},
                            { exception -> updateUI(exception)}
                    )
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(e)
        }
    }

    fun handleTwitterResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TODO("Handle Twitter result")
    }

    fun handleMicrosoftResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TODO("Handle Microsoft result")
    }

    fun handleAdResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TODO("Handle Active Directory result")
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent) {
        val result = azureService.onActivityResult(data)
        if(result.isLoggedIn) {
            showTodoList()
        } else {
            updateUI(Exception(result.errorMessage))
        }
    }

    fun signIn(provider: MobileServiceAuthenticationProvider) {
        azureService.login(provider.name, currentCallbackId)
    }

    fun signOut() {
        azureService.logout(
            { loggedOutUser -> showTodoList()},
            { exception -> updateUI(exception) }
        )
    }

    private fun showTodoList() {
        val todoListIntent = Intent(app, TodoActivity::class.java)
        startActivityEvent.value = todoListIntent
    }

    private fun updateUI(error: Exception?) {
        model.statusText = error?.message ?: ""
    }

    private fun onLoginSuccess(credentials: AzureCredentials?) = showTodoList()
    private fun onLoginFail(exception: Exception?) = updateUI(exception)

}