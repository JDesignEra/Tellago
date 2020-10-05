package com.tellago.activities

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.tellago.MainActivity
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.utilities.CustomToast

class AuthActivity : AppCompatActivity() {
    private lateinit var toast: CustomToast

    override fun onStart() {
        super.onStart()

        toast = CustomToast(this)
    }

    override fun onResume() {
        super.onResume()
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)

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

        var intentBuilder = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAuthMethodPickerLayout(customLayout)
            .setTheme(R.style.AuthTheme)
            .setAvailableProviders(authProviders)
            .setIsSmartLockEnabled(false)

        if (user == null && Auth().getUser() == null) {
            startActivityForResult(intentBuilder.build(), rcSignIn)
        }
        else if (user != null && user!!.isAnonymous) {
            intentBuilder = intentBuilder.enableAnonymousUsersAutoUpgrade()

            startActivityForResult(intentBuilder.build(), rcSignIn)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                Auth()

                if (user?.isAnonymous!!) {
                    startGuestActivity()
                }
                else {
                    startMainActivity()
                }
            }
            else {
                when {
                    response == null -> {
                        Log.w("AuthActivity", "User cancelled the sign-in flow.")
                    }
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                        //toast.error("Requires an Internet Connection")
                    }
                    response.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT -> {
                        Auth().signOut(this) {
                            FirebaseAuth.getInstance()
                                .signInWithCredential(response.credentialForLinking!!)
                                .addOnSuccessListener {
                                    Auth()
                                }
                        }

                        startMainActivity()
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun startGuestActivity() {
        startActivity(Intent(this, GuestScrollingActivity::class.java))
        finish()
    }

    companion object {
        const val rcSignIn: Int = 1478
    }
}