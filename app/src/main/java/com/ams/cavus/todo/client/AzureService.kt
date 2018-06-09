package com.ams.cavus.todo.client

import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread

typealias AuthSuccess = (AzureCredentials) -> Unit
typealias AuthFail = (Exception) -> Unit
typealias LogoutSuccess = (MobileServiceUser) -> Unit
typealias LogoutFail = (Exception) -> Unit

class AzureService(private val client: MobileServiceClient, private val gson: Gson) {

    lateinit var currentCredentials: AzureCredentials

    fun login(credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {
            if (credentials == null) {
                uiThread { fail.invoke(Exception("Invalid state")) }
            } else {
                val oauthToken = gson.toJson(credentials)
                val authTask = client.login(credentials.provider, oauthToken)
                // Signed in successfully, show authenticated UI.
                authTask.doAsyncResult {
                    currentCredentials = credentials
                    if (!authTask.isDone) return@doAsyncResult
                    authTask.get().apply {
                        credentials.authToken = this.authenticationToken
                        credentials.userId = this.userId
                    }
                    uiThread { success.invoke(credentials) }
                }
            }
        }
    }

    fun login(provider: String, credentials: AzureCredentials, success: AuthSuccess, fail: AuthFail) {
        doAsync {

            val authTask = client.login(provider, gson.toJson(credentials))
            // Signed in successfully, show authenticated UI.
            authTask.doAsyncResult {
//                if (!authTask.isDone) return@doAsyncResult
                currentCredentials = credentials
                authTask.get().apply {
                    credentials.authToken = this.authenticationToken
                    credentials.userId = this.userId
                }
                uiThread { success.invoke(credentials) }
            }
        }
    }

    fun logout(success: LogoutSuccess, fail: LogoutFail) {
        val logoutTask = client.logout()
        logoutTask.doAsyncResult {
            if (!logoutTask.isDone) return@doAsyncResult
            uiThread { success.invoke(client.currentUser) }
        }
    }

}