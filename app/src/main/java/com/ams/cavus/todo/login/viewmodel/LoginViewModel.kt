package com.ams.cavus.todo.login.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import com.ams.cavus.todo.R
import com.ams.cavus.todo.client.AzureCredentials
import com.ams.cavus.todo.db.AppDb
import com.ams.cavus.todo.db.entity.CredentialsData
import com.ams.cavus.todo.login.model.LoginDataModel
import com.example.amstodo.util.SingleLiveEvent
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class LoginViewModel (private val app: Application) : AndroidViewModel(app), LifecycleObserver {

    @Inject
    lateinit var azureClient: MobileServiceClient

    @Inject
    lateinit var googleClient: GoogleSignInClient

    @Inject
    lateinit var model: LoginDataModel

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var appDb: AppDb

    val startActivityEvent = SingleLiveEvent<Intent>()

    init {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        doAsync {
            val credentials = appDb.credentialsDataDao().selectLastAccount()
            if(azureClient.currentUser != null) {
                uiThread { updateUI(credentials) }
            } else {
                if(credentials == null) {
                    uiThread { updateUI(null) }
                } else {
                    val oauthToken = gson.toJson(credentials)
                    val authTask = azureClient.login(MobileServiceAuthenticationProvider.Google.name, oauthToken)
                    // Signed in successfully, show authenticated UI.
                    authTask.doAsyncResult {
                        if(!authTask.isDone) return@doAsyncResult
                        val user = authTask.get()
                        print(user)
                        uiThread { updateUI(credentials) }
                    }
                }
            }
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
//            val account = completedTask.getResult(ApiException::class.java)
            val signInAccount = GoogleSignIn.getLastSignedInAccount(app)
            doAsync {
                val accessToken = GoogleAuthUtil.getToken(app.applicationContext, signInAccount?.account, app.getString(R.string.azure_scope))
                val oauthToken = gson.toJson(AzureCredentials(accessToken, signInAccount?.idToken ?: ""))
                val authTask = azureClient.login(MobileServiceAuthenticationProvider.Google.name, oauthToken)
                // Signed in successfully, show authenticated UI.
                authTask.doAsyncResult {
//                    if(!authTask.isDone) return@doAsyncResult
                    val user = authTask.get()
                    val credentials =
                            CredentialsData(
                                    0,
                                    user.authenticationToken,
                                    user.userId,
                                    signInAccount?.email ?: "",
                                    accessToken,
                                    signInAccount?.idToken ?: ""
                            )
                    appDb.credentialsDataDao().insert(credentials)
                    uiThread { updateUI(credentials) }
                }
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null)
        }
    }

    fun signIn() {
        val signInIntent = googleClient.signInIntent
        googleClient.revokeAccess()
        startActivityEvent.value = signInIntent
    }

    fun signInToAzure(credentials: CredentialsData) {

    }

    fun signOut() {
        googleClient.signOut()
            .addOnCompleteListener {
                updateUI(null)
            }
    }

    fun revokeAccess() {
        googleClient.revokeAccess()
            .addOnCompleteListener {
                updateUI(null)
            }
    }

    private fun updateUI(credentials: CredentialsData?) {
        val context = app.baseContext
        if (credentials != null) {
            model.apply {
                statusText = context.getString(R.string.signed_in_fmt, credentials.email)
                isSignInVisible = false
                isSignOutVisible = true
            }
        } else {
            model.apply {
                statusText = context.getString(R.string.signed_out)
                isSignInVisible = true
                isSignOutVisible = false
            }
        }
    }

}