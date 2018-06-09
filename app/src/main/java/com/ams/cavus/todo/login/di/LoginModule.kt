package com.ams.cavus.todo.login.di

import android.arch.persistence.room.Room
import com.ams.cavus.client.AzureApi
import com.ams.cavus.todo.R
import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.client.AzureService
import com.ams.cavus.todo.client.GoogleService
import com.ams.cavus.todo.db.AppDb
import com.ams.cavus.todo.db.DbService
import com.ams.cavus.todo.login.model.LoginDataModel
import com.ams.cavus.todo.login.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class LoginModule {

    @Singleton
    @Provides
    // TODO("Use the ViewModelProvider instead of the constructor")
    fun provideLoginViewModel(app: App) = LoginViewModel(app)

    @Singleton
    @Provides
    fun provideLoginDataModel() = LoginDataModel()

    @Singleton
    @Provides
    fun provideGoogleClient(app: App): GoogleSignInClient{
        val clientId = app.getString(R.string.google_server_client_id)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .requestIdToken(clientId)
                .requestServerAuthCode(clientId)
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        return GoogleSignIn.getClient(app.applicationContext, gso)
    }

    @Singleton
    @Provides
    fun provideGoogleService(client: GoogleSignInClient, gson: Gson) = GoogleService(client, gson)

}