package com.tellago.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.tellago.*
import com.tellago.models.Auth
import com.tellago.services.ExitService
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.bottom_guest_to_sign_in_up.*


class GuestScrollingActivity : AppCompatActivity() {


    private lateinit var userPostAdapter: UserPostRecyclerAdapter

    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_guest_to_sign_in_up)

        initRecyclerView()
        addDataSet()

        // bottomMenuFirebaseAuth()


        Log.d("onCreate", "Fired onCreate!")

        fab.setOnClickListener {
            Log.d("Floating Action Button", "Fired Floating Action Button!")
            //Toast.makeText(this, "clicked on Floating Action Button", Toast.LENGTH_SHORT).show()
            // close GuestScrollingActivity.
            //startActivity(Intent(this, SplashActivity::class.java))
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

    }

    private fun bottomMenuFirebaseAuth() {

        Log.d("bottomMenuFirebaseAuth", "FIRED")

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

        Log.d("customLayout", "COMPLETED customLayout")


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

    private fun addDataSet() {
        // data created in DataSource data class should be retrieved from Firebase Storage & Cloud Firestore
        val data = DataSource.createDataSet()
        userPostAdapter.submitList(data)
        Log.d("addDataSet()", "FIRED")
    }

    private fun initRecyclerView() {
        Log.d("initRecyclerView()", "FIRED")
        // recyclerview from bottom_guest_to_sign_in_up.xml
        recycler_view.apply {

            // Step 1: set layoutManager for recyclerview
            layoutManager = LinearLayoutManager(this@GuestScrollingActivity)

            // Step 2: Adding padding decoration for spacing between viewholders (defined in TopSpacingItemDecoration.kt)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)

            // Step 3: Initialise the lateinit variable userPostAdapter
            userPostAdapter = UserPostRecyclerAdapter()
            adapter = userPostAdapter
        }
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
                } else if (response.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                    // Get the non-anoymous credential from the response
                    val nonAnonymousCredential: AuthCredential? = response.credentialForLinking
                    // Sign in with credential
                    if (nonAnonymousCredential != null) {
                        FirebaseAuth.getInstance().signInWithCredential(nonAnonymousCredential)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    CustomToast(this, "Requires an Internet Connection").primary()
                }
            }
        }
    }
}