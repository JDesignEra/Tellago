package com.tellago

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.tellago.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1478
    private val signInProviders = Arrays.asList(
        AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
//        AuthUI.IdpConfig.AnonymousBuilder().build()
    )

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val lifeaspirationFragment = LifeAspirationFragment()
    private val profileFragment = ProfileFragment()


    override fun onStart() {
        super.onStart()

        // Running is custom layout for landing page (functionlity handled by FirebaseUI)
        val customLayout : AuthMethodPickerLayout = AuthMethodPickerLayout
            .Builder(R.layout.activity_custom_logo)
            .setEmailButtonId(R.id.btnEmail)
            .setFacebookButtonId(R.id.btnFacebook)
            .setGoogleButtonId(R.id.btnGoogle)
            .setAnonymousButtonId(R.id.btnGuest)
            .build()


        user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(signInProviders)
                    .setTheme(R.style.AuthTheme)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus)
            hideSystemUI()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                user = FirebaseAuth.getInstance().currentUser
                addFragment(homeFragment)
            }
            else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    private fun StartTimer() {
        handler = Handler()
        handlerTask = Runnable { // do something
            hideSystemUI()
            handler!!.postDelayed(handlerTask, 3000)
        }
        handlerTask!!.run()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StartTimer()

        if (FirebaseAuth.getInstance().currentUser != null) {
            replaceFragment(homeFragment)
        }

        // the following code will replace the current fragment based on the selected navigation
        // item from the bottom navigation bar
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_people -> replaceFragment(communityFragment)
                R.id.ic_flag -> replaceFragment(lifeaspirationFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }

            true
        }


    }

    private fun addFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null);
            transaction.commit()
        }
        else {
            addFragment(fragment)
        }
    }


    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    companion object {

        var user = FirebaseAuth.getInstance().currentUser
    }
}