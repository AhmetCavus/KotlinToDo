package com.ams.cavus.todo.login.login

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ams.cavus.todo.R
import com.ams.cavus.todo.login.viewmodel.LoginViewModel
import com.ams.cavus.util.app
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import javax.inject.Inject

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        const val GOOGLE_CALLBACK = 101
    }

//    lateinit var viewDataBinding: ActivityLoginBinding

    @Inject
    lateinit var viewModel: LoginViewModel

    /**
     * Mobile Service Client reference
     */
    @Inject
    lateinit var client: MobileServiceClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The layout for this activity is a Data Binding layout so it needs to be inflated using
//        viewDataBinding = DataBindingUtil.setContentView(
//                this, R.layout.activity_login)
//
//        app.component.inject(this)
//        app.component.inject(viewModel)
//
//        client.context = this
//
//        viewDataBinding.vm = viewModel.apply {
//            openTaskEvent.observe(this@LoginActivity, android.arch.lifecycle.Observer {
//                startActivityForResult(it ?: null, LoginActivity.GOOGLE_CALLBACK)
//            })
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == LoginActivity.GOOGLE_CALLBACK) {
            viewModel.handleActivityResult(requestCode, resultCode, intent)
        }
    }

}
