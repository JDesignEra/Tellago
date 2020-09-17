package com.tellago.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.tellago.MainActivity
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.services.ExitService
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_guest_to_sign_in_up.*


class GuestScrollingActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_guest_to_sign_in_up)

        Log.d("onCreate", "Fired onCreate!")
        bottomMenuFirebaseAuth()

    }

    private fun bottomMenuFirebaseAuth() {
        val chipNavigationBar: ChipNavigationBar = bottom_nav_menu_bar

        //chipNavigationBar.expand()

        val authProviders = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val customLayout: AuthMethodPickerLayout = AuthMethodPickerLayout
            .Builder(R.layout.bottom_guest_to_sign_in_up)
            .setEmailButtonId(R.id.bottom_nav_email_icon)
            .setFacebookButtonId(R.id.bottom_nav_facebook_icon)
            .setGoogleButtonId(R.id.bottom_nav_google_icon)
            .build()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(authProviders)
                .setTheme(R.style.AuthTheme)
                .setIsSmartLockEnabled(false)
                .enableAnonymousUsersAutoUpgrade()
                .build(),
            AuthActivity.rcSignIn
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val response = IdpResponse.fromResultIntent(data)
        Log.e("IdpResponse", response.toString())

        if (requestCode == AuthActivity.rcSignIn) {
            Log.e("requestCode", AuthActivity.rcSignIn.toString())

            if (resultCode == Activity.RESULT_OK) {
                Auth()

            } else {
                // Sign in failed
                if (response == null) {
                    Log.w("AuthActivity", "User cancelled the sign-in flow.")
                }
                else if (response.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                    // Get the non-anoymous credential from the response
                    val nonAnonymousCredential : AuthCredential? = response.credentialForLinking
                    // Sign in with credential
                    if (nonAnonymousCredential != null) {
                        FirebaseAuth.getInstance().signInWithCredential(nonAnonymousCredential)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                     }
                }

                else if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    CustomToast(this, "Requires an Internet Connection").primary()
                }
            }
        }

    }
}