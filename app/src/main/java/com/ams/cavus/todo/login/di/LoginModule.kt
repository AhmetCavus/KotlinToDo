package com.ams.cavus.todo.login.di

import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.login.model.LoginDataModel
import com.ams.cavus.todo.login.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
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
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        return GoogleSignIn.getClient(app.applicationContext, gso)
    }

}