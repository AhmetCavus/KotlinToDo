package com.ams.cavus.todo.login.model

import android.databinding.BaseObservable
import android.databinding.Bindable

data class LoginDataModel(private val id: Int = 0) : BaseObservable() {

    var username: String = ""
        @Bindable get

    var password: String = ""
        @Bindable get

    var isInProgress: Boolean = false
        @Bindable get

}