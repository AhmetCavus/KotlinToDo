package com.ams.cavus.todo.login

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ams.cavus.todo.R
import com.ams.cavus.todo.databinding.ActivityLoginBinding
import com.ams.cavus.todo.login.viewmodel.LoginViewModel
import com.ams.cavus.todo.util.app
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import javax.inject.Inject

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    private lateinit var viewDataBinding: ActivityLoginBinding

    @Inject
    lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var client: MobileServiceClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The layout for this activity is a Data Binding layout so it needs to be inflated using
        viewDataBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_login)

        app.component.inject(this)
        app.component.inject(viewModel)

        client.context = this

        viewDataBinding.vm = viewModel.apply {
            startActivityForResultEvent.observe(this@LoginActivity, Observer(::onStartActivityForResult))
            startActivityEvent.observe(this@LoginActivity, Observer(::onStartActivity))
            lifecycleRegistry.addObserver(this)
        }
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoginViewModel.TWITTER_CALLBACK -> viewModel.handleTwitterResult(requestCode, resultCode, data)
            LoginViewModel.GOOGLE_CALLBACK -> viewModel.handleGoogleResult(requestCode, resultCode, data)
            LoginViewModel.MICROSOFT_CALLBACK -> viewModel.handleMicrosoftResult(requestCode, resultCode, data)
            LoginViewModel.AD_CALLBACK -> viewModel.handleAdResult(requestCode, resultCode, data)
            else -> viewModel.handleResult(requestCode, resultCode, data)
        }
    }

    private fun onStartActivityForResult(intent: Intent?) {
        startActivityForResult(intent, viewModel.currentCallbackId)
    }

    private fun onStartActivity(intent: Intent?) {
        startActivity(intent)
    }

}