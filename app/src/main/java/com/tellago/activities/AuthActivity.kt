package com.tellago.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.tellago.MainActivity
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.utils.CustomToast

class AuthActivity: AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        if (Auth.user == null && Auth().getUser() == null) {
            val authProviders = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.AnonymousBuilder().build()
            )

            val customLayout: AuthMethodPickerLayout = AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setEmailButtonId(R.id.btnEmail)
                .setFacebookButtonId(R.id.btnFacebook)
                .setGoogleButtonId(R.id.btnGoogle)
                .setAnonymousButtonId(R.id.btnGuest)
                .build()

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(authProviders)
                    .setTheme(R.style.AuthTheme)
                    .setIsSmartLockEnabled(false)
                    .build(),
                rcSignIn
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val response = IdpResponse.fromResultIntent(data)

        if (requestCode == rcSignIn && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                Auth()

                if (Auth().getUser()?.isAnonymous!!) {
                    startActivity(Intent(this, GuestScrollingActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                if (response == null) {
                    Log.w("AuthActivity", "User cancelled the sign-in flow.")
                } else if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    CustomToast(this, "Requires an Internet Connection").primary()
                }
            }
        }
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        if (Auth.user != null && Auth.user!!.isAnonymous) {
            Auth.user!!.delete()
        }

        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                Auth.user = null
                onComplete()
            }
    }

    companion object {
        const val rcSignIn = 1478
    }
}