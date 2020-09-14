package com.tellago

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tellago.fragments.*
import com.tellago.model.Auth
import com.tellago.services.AuthExitService
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_header.*
import kotlinx.android.synthetic.main.toolbar_drawer.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1478
    private val authProviders = Arrays.asList(
        AuthUI.IdpConfig.EmailBuilder().build(),
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

        // To hide bottom navigation between sign out & login
//        bottom_navigation.visibility = View.INVISIBLE
//        guest_bot_banner.visibility = View.INVISIBLE

        Auth().initSignInInstance(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Auth.rcSignIn) {
            if (resultCode == Activity.RESULT_OK) {
                Auth.user = FirebaseAuth.getInstance().currentUser

                if (Auth.user != null && Auth.user!!.isAnonymous) {
                    bottom_navigation.visibility = View.INVISIBLE
                    guest_bot_banner.visibility = View.VISIBLE
                }
                else {
                    bottom_navigation.visibility = View.VISIBLE
                    guest_bot_banner.visibility = View.INVISIBLE
                }

                addFragment(homeFragment)

                if (Auth.user != null && Auth.user!!.isAnonymous) {
                    CustomToast(this, "Welcome to Tellago, Guest").success()
                } else {
                    CustomToast(this, "Welcome to Tellago, %s".format(Auth.user?.displayName)).success()
                    user_displayname.text = "Greetings, %s".format(Auth.user?.displayName)
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

        configureNavigationDrawer()
        configureToolbar()
        navigation.visibility = View.INVISIBLE

        StartTimer()

        if (Auth.user != null) {
            replaceFragment(homeFragment)

            if (Auth.user!!.isAnonymous) {
                bottom_navigation.visibility = View.INVISIBLE
            }
        }

        // the following code will replace the current fragment based on the selected navigation
        // item from the bottom navigation bar
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_people -> replaceFragment(communityFragment)
                R.id.ic_flag -> replaceFragment(lifeAspirationFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }

            true
        }
    }


    private fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null);
            transaction.commit()
        } else {
            addFragment(fragment)
        }
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar as Toolbar?)
        val actionbar: ActionBar? = supportActionBar
        // To 'hide' Title display in actionbar
        actionbar?.setTitle("")

        // Open up navigation in the event that 'Menu icon' is clicked (it is actually Navigation button)
        (toolbar as Toolbar?)?.setNavigationOnClickListener {
            navigation.visibility = View.VISIBLE
        }

        // Set 'Menu icon' as the icon for Navigation button
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionbar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureNavigationDrawer() {
        val navigationView: NavigationView = navigation
        navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            true
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val viewRect = Rect()
        navigation.getGlobalVisibleRect(viewRect)
        // if user taps outside of toolbar navigation drawer, then change its visibility to INVISIBLE
        if (!viewRect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
            navigation.visibility = View.INVISIBLE
        }
        return super.dispatchTouchEvent(ev)
    }

    fun onNavigationItemSelected(menuItem: MenuItem) {
        val f: Fragment? = null
        val menu_itemID = menuItem.itemId

        when (menu_itemID) {
            R.id.logout_from_drawer -> Auth().signOut(this) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        if (f != null) {
            val drawerLayout: DrawerLayout = drawer_layout
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.toolbar_frame, f)
            transaction.commit()
            drawerLayout.closeDrawers()
            true
        } else
            false
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        navigation.visibility = View.VISIBLE
        return super.onMenuOpened(featureId, menu)
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
}