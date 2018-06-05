package com.ams.cavus.todo.util

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter("onClick")
fun setOnClick(view: com.google.android.gms.common.SignInButton, clickListener: View.OnClickListener) {
    view.setOnClickListener(clickListener)
}

@BindingAdapter("visibleIf")
fun changeVisibility(view: View, visible: Boolean) {
    view.visibility = if(visible) View.VISIBLE else View.GONE
}