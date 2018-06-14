package com.ams.cavus.todo.client

import android.app.Application
import android.content.Intent
import com.ams.cavus.todo.R
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.gson.Gson
import org.jetbrains.anko.doAsync

typealias GetCredentialsCompleted = (AzureCredentials) -> Unit

class GoogleService(private val client: GoogleSignInClient, private val gson: Gson) {

    fun loadCredentials(app: Application, account: GoogleSignInAccount, completed: GetCredentialsCompleted) {
        doAsync {
            val accessToken = GoogleAuthUtil.getToken(app.applicationContext, account.account, app.getString(R.string.azure_scope))
            val credentials = AzureCredentials(accessToken, account.idToken ?: "", provider = "Google")
            completed.invoke(credentials)
        }
    }

    fun createSignInIntent(): Intent {
        client.revokeAccess()
        return client.signInIntent
    }

}