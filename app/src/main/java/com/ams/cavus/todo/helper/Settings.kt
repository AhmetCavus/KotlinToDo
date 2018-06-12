package com.ams.cavus.todo.helper

import android.content.Context
import android.content.SharedPreferences
import com.ams.cavus.todo.client.AzureCredentials
import com.ams.cavus.todo.client.emptyCredentials

class Settings(context: Context) {

    companion object {
        const val PREFERENCES_KEY = "credentials.key"
        const val ACCESS_TOKEN_KEY = "authToken"
        const val USER_ID_KEY = "userId"
    }

    var preferences: SharedPreferences = context?.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)

    val isCredentials: Boolean
        get() {
            return preferences.contains(ACCESS_TOKEN_KEY) && preferences.contains(USER_ID_KEY)
        }

    private val _credentials = emptyCredentials()
    val credentials: AzureCredentials
        get() {
            val authToken = preferences.getString(ACCESS_TOKEN_KEY, "")
            val userId = preferences.getString(USER_ID_KEY, "")
            _credentials.authToken = authToken
            _credentials.userId = userId
            return  _credentials
        }

    fun saveCredentials(accessToken: String, userId: String) {
        with(preferences.edit()) {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(USER_ID_KEY, userId)
            commit()
        }
    }

}