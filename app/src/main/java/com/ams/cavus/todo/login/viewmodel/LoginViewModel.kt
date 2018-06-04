package com.ams.cavus.todo.login.viewmodel

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.widget.Toast
import com.ams.cavus.todo.login.model.LoginDataModel
import com.example.amstodo.util.SingleLiveEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.authentication.LoginManager
import javax.inject.Inject

class LoginViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver {

    @Inject
    lateinit var azureClient: MobileServiceClient

    @Inject
    lateinit var googleClient: GoogleSignInClient

    @Inject
    lateinit var model: LoginDataModel

    val openTaskEvent = SingleLiveEvent<Intent>()

    val loginManager: LoginManager by lazy {
        LoginManager(azureClient)
    }

    init {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }

    fun onLogin() {

        val account = GoogleSignIn.getLastSignedInAccount(getApplication())
        if(account != null) {
            googleClient
                .signOut()
                .addOnCompleteListener(azureClient.context as Activity, OnCompleteListener<Void> {
                    model.isInProgress = true
                    val googleClient = googleClient.signInIntent
                    openTaskEvent.value = googleClient
                })
        } else {
            model.isInProgress = true
            val googleClient = googleClient.signInIntent
            openTaskEvent.value = googleClient
        }

//        loginManager.authenticate(MobileServiceAuthenticationProvider.Google.name, client.context,
//                UserAuthenticationCallback { user, exception, response ->
//                    print(user.userId)
//                })
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    // [START handleSignInResult]
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Toast.makeText(getApplication(), account.email, Toast.LENGTH_SHORT).show()
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

}