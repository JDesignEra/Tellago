package com.tellago.model

import android.app.Activity
import android.content.Context
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tellago.R
import java.util.*

class Auth : Activity() {
    init {
        user = FirebaseAuth.getInstance().currentUser
    }

    fun initSignInInstance(activity: Activity) {
        if (user == null) {
            val authProviders = Arrays.asList(
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

            activity.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(authProviders)
                    .setTheme(R.style.AuthTheme)
                    .build(),
                rcSignIn
            )
        }
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        if (user != null && user!!.isAnonymous) {
            user!!.delete()
        }

        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                user = FirebaseAuth.getInstance().currentUser
                onComplete()
            }
    }

    companion object {
        val rcSignIn = 1478
        var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    }
}