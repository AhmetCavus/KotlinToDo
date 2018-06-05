package com.ams.cavus.todo.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ActionMode
import android.view.View
import android.widget.TextView
import com.ams.cavus.todo.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import java.security.KeyStore

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mStatusTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        mStatusTextView = findViewById(R.id.status)

        // Button listeners
        sign_in_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
        disconnect_button.setOnClickListener(this)

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val clientId = getString(R.string.google_server_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .requestIdToken(clientId)
                .requestServerAuthCode(clientId)
                .build()
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setColorScheme(SignInButton.COLOR_LIGHT)
        // [END customize_button]

        val defaultKey = KeyStore.getDefaultType()
        val keystore = KeyStore.getInstance(defaultKey)
        print(keystore.provider)
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        super.onActionModeStarted(mode)
    }

    public override fun onStart() {
        super.onStart()

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
//            val account = GoogleSignIn.getLastSignedInAccount(this)

            print(account.idToken)
            print(account.serverAuthCode)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }

    }
    // [END handleSignInResult]

    // [START signIn]
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        mGoogleSignInClient!!.revokeAccess()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signIn]

    // [START signOut]
    private fun signOut() {
        mGoogleSignInClient!!.signOut()
                .addOnCompleteListener(this) {
                    // [START_EXCLUDE]
                    updateUI(null)
                    // [END_EXCLUDE]
                }
    }
    // [END signOut]

    // [START revokeAccess]
    private fun revokeAccess() {
        mGoogleSignInClient!!.revokeAccess()
                .addOnCompleteListener(this) {
                    // [START_EXCLUDE]
                    updateUI(null)
                    // [END_EXCLUDE]
                }
    }
    // [END revokeAccess]

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.displayName)

            sign_in_button.setVisibility(View.GONE)
            sign_out_and_disconnect.setVisibility(View.VISIBLE)
        } else {
            mStatusTextView!!.setText(R.string.signed_out)
            sign_in_button.setVisibility(View.VISIBLE)
            sign_out_and_disconnect.setVisibility(View.GONE)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
            R.id.disconnect_button -> revokeAccess()
        }
    }

    companion object {

        private val TAG = "SignInActivity"
        private val RC_SIGN_IN = 9001
    }
}