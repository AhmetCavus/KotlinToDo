package com.ams.cavus.todo.login.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.ams.cavus.todo.BR

data class LoginDataModel(private val id: Int = 0) : BaseObservable() {

    var username = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.username)
        }

    var usernameValid = false
        @Bindable get() = username.isNotEmpty() && username.length > 2


    var password: String = ""
        @Bindable get

    var isInProgress: Boolean = false
        @Bindable get

    var isSignInVisible: Boolean = true
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.signInVisible)
        }

    var isSignOutVisible: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.signOutVisible)
        }

    var statusText: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.statusText)
        }
}