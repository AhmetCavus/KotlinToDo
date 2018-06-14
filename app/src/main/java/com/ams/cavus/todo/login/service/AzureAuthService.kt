package com.ams.cavus.todo.login.service

import android.content.Intent
import com.ams.cavus.client.AzureApi
import com.ams.cavus.todo.client.AzureCredentials
import com.ams.cavus.todo.client.emptyCredentials
import com.ams.cavus.todo.helper.Settings
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread

typealias AuthSuccess = (AzureCredentials) -> Unit
typealias AuthFail = (Exception) -> Unit
typealias LogoutSuccess = (MobileServiceUser) -> Unit
typealias LogoutFail = (Exception) -> Unit

class AzureAuthService(private val client: MobileServiceClient, private val gson: Gson, private val settings: Settings) {

    var currentCredentials = emptyCredentials()

    val isLoggedIn: Boolean
        get() = client.currentUser?.authenticationToken?.isNotEmpty() ?: false

    val hasIdentifier: Boolean
        get() = settings.credentials.userName != null && settings.credentials.userName.isNotEmpty()

    init {
        if (client.currentUser != null) {
            initCredentials()
        }
    }

    fun login(credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {
            val oauthToken = gson.toJson(credentials)
            val authTask = client.login(credentials.provider, oauthToken)
            // Signed in successfully, show authenticated UI.
            authTask.doAsyncResult {
                if (!authTask.isDone) return@doAsyncResult
                authTask.get().apply {
                    credentials.authToken = authenticationToken
                    credentials.userId = userId
                    currentCredentials = credentials
                    settings.saveCredentials(credentials.authToken, credentials.userId, "")
                }
                uiThread { success.invoke(credentials) }
            }
        }
    }

    fun login(provider: String, credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {

            val authTask = client.login(provider, gson.toJson(credentials))
            // Signed in successfully, show authenticated UI.
            authTask.doAsyncResult {
                //                if (!authTask.isDone) return@doAsyncResult
                authTask.get().apply {
                    credentials.authToken = authenticationToken
                    credentials.userId = userId
                    currentCredentials = credentials
                    settings.saveCredentials(credentials.authToken, credentials.userId, "")
                }
                uiThread { success.invoke(credentials) }
            }
        }
    }

    fun login(provider: String, requestCode: Int) {
        client.login(provider, AzureApi.URI_SCHEME, requestCode)
    }

    fun logout(success: LogoutSuccess, fail: LogoutFail) {
        val logoutTask = client.logout()
        logoutTask.doAsyncResult {
            if (!logoutTask.isDone) return@doAsyncResult
            uiThread { success.invoke(client.currentUser) }
        }
    }

    private fun initCredentials() {
        currentCredentials = emptyCredentials().apply {
            authToken = client.currentUser.authenticationToken
            userId = client.currentUser.userId
            userName = settings.credentials.userName
        }
    }

    fun onActivityResult(data: Intent): MobileServiceActivityResult {
        return client.onActivityResult(data).apply {
            if(isLoggedIn) {
                settings.saveCredentials(
                        client.currentUser?.authenticationToken ?: "",
                        client.currentUser.userId ?: "",
                        "")
                initCredentials()
                initialize()
            }
        }
    }

    fun initialize() {
        if(client?.syncContext?.isInitialized == true) return
        if(!settings?.isCredentials) return

        client.apply {
            currentUser = MobileServiceUser(settings.credentials.userId)
            currentUser.authenticationToken = settings.credentials.authToken
            initCredentials()
        }
    }

}