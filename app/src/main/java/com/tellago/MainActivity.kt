package com.tellago

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tellago.fragments.*
import com.tellago.model.Auth
import com.tellago.model.Auth.Companion.user
import com.tellago.services.AuthExitService
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.menu_header.*
import kotlinx.android.synthetic.main.menu_header.view.*

class MainActivity : AppCompatActivity() {
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val feedFragment = FeedFragment()
    private val profileFragment = ProfileFragment()

    override fun onStart() {
        super.onStart()
        startService(Intent(this, AuthExitService::class.java))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
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

        configureNavigationDrawer()

        configureToolbar()

        StartTimer()


        if (user != null) {
            replaceFragment(homeFragment)

            if (user!!.isAnonymous) {
                bottom_navigation.visibility = View.INVISIBLE
                guest_bot_banner.visibility = View.VISIBLE
            }
            else {
                bottom_navigation.visibility = View.VISIBLE
                guest_bot_banner.visibility = View.INVISIBLE
            }

            if (!user?.displayName.isNullOrEmpty()) {
                CustomToast(this, "Welcome to tellsquare, %s".format(user?.displayName)).success()
            }
            else {
                CustomToast(this, "Welcome to tellsquare, Guest").success()
            }
        }

        // the following code will replace the current fragment based on the selected navigation
        // item from the bottom navigation bar
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_people -> replaceFragment(communityFragment)
                R.id.ic_feed -> replaceFragment(feedFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }

            true
        }


        val closeButton: ImageView = closeFromNavView
        closeButton.setOnClickListener {
            Log.d("button", "close button clicked!!!")
            drawer_layout.closeDrawer(GravityCompat.START)
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
            transaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit
            )
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null);
            transaction.commit()
        }
        else {
            addFragment(fragment)
        }
    }


    private fun configureToolbar() {
        setSupportActionBar(toolbar as Toolbar?)
        // Call the method that assigns toolbar actions
        onCreateOptionsMenu((toolbar as Toolbar?)?.menu)

        val actionbar: ActionBar? = supportActionBar
        // To 'hide' Title display in actionbar
        actionbar?.setTitle("")

        // Open up navigation in the event that 'Menu icon' is clicked (it is actually Navigation button)
        (toolbar as Toolbar?)?.setNavigationOnClickListener {
            val drawerLayout: DrawerLayout = drawer_layout
            drawerLayout.openDrawer(GravityCompat.START)

            if (!user?.displayName.isNullOrEmpty()) {
                drawerLayout.user_displayname.text = "Greetings, %s".format(user?.displayName)
            }
            else {
                drawerLayout.user_displayname.text = "Greetings, Guest"
            }
        }

        // Set 'Menu icon' as the icon for Navigation button
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionbar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureNavigationDrawer() {
        val navigationView: NavigationView = navigation

        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            true
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val viewRect = Rect()

        navigation.getGlobalVisibleRect(viewRect)
        // uncomment the following then make changes so that drawer can be SWIPED open from LEFT
//        if (!viewRect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
//            if (drawerLayout.isDrawerVisible((GravityCompat.START)))
//                drawerLayout.closeDrawer(GravityCompat.START)
//            else drawerLayout.openDrawer(GravityCompat.START)
//        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Method which assigns toolbar actions (based on options_menu)
        // Must fill actions within options_menu for the following inflate to display those
        // actions as part of the Toolbar
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }



    fun onNavigationItemSelected(menuItem: MenuItem) {
        val f: Fragment? = null
        val menu_itemID = menuItem.itemId

        when (menu_itemID) {
            R.id.view_profile -> replaceFragment(profileFragment)
            R.id.logout_from_drawer -> Auth().signOut(this) {
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
            }
        }


        if (f != null) {
            val drawerLayout: DrawerLayout = drawer_layout
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, f)
            transaction.commit()
            drawerLayout.closeDrawers()
            true
        } else false
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        navigation.visibility = View.VISIBLE
        return super.onMenuOpened(featureId, menu)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}