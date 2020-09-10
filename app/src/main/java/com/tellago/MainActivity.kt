package com.tellago

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tellago.fragments.*
import com.tellago.services.AuthExitService
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1478
    private val authProviders = Arrays.asList(
        AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.AnonymousBuilder().build()
    )

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val lifeAspirationFragment = LifeAspirationFragment()
    private val profileFragment = ProfileFragment()


    override fun onStart() {
        super.onStart()

        startService(Intent(this, AuthExitService::class.java))

        user = FirebaseAuth.getInstance().currentUser

        // Running is custom layout for landing page (functionality handled by FirebaseUI)
        val customLayout : AuthMethodPickerLayout = AuthMethodPickerLayout
            .Builder(R.layout.activity_auth)
            .setEmailButtonId(R.id.btnEmail)
            .setFacebookButtonId(R.id.btnFacebook)
            .setGoogleButtonId(R.id.btnGoogle)
            .setAnonymousButtonId(R.id.btnGuest)
            .build()

        if (user == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(authProviders)
                    .setTheme(R.style.AuthTheme)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) hideSystemUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                user = FirebaseAuth.getInstance().currentUser

                addFragment(homeFragment)

                if (user != null && user!!.isAnonymous) {
                    CustomToast(this, "Welcome to Tellago, Guest").success()
                }
                else {
                    CustomToast(this, "Welcome to Tellago, %s".format(user?.displayName)).success()
                }
            }
        }
    }

    private fun StartTimer() {
        handler = Handler()
        handlerTask = Runnable { // do something
            hideSystemUI()
            handler!!.postDelayed(handlerTask, 5000)
        }

        handlerTask!!.run()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Indicate that toolbar will replace Actionbar
        setSupportActionBar(toolbar as Toolbar?)

        StartTimer()

        if (user != null) {
            replaceFragment(homeFragment)

            if (user!!.isAnonymous) {
                bottom_navigation.visibility = View.INVISIBLE
            }
        }

        // the following code will replace the current fragment based on the selected navigation
        // item from the bottom navigation bar
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_people -> replaceFragment(communityFragment)
                R.id.ic_flag -> replaceFragment(lifeAspirationFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }

            true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_logout -> signOut()
        }
        return true
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

    private fun signOut() {
        if (user != null && user!!.isAnonymous) {
            user!!.delete()
        }

        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)
            }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    companion object {
        var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    }
}