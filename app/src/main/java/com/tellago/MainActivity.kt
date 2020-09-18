package com.tellago

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.tellago.activities.AuthActivity
import com.tellago.activities.SplashActivity
import com.tellago.fragments.*
import com.tellago.models.Auth
import com.tellago.services.ExitService
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val feedFragment = FeedFragment()
    private val profileFragment = ProfileFragment()
    private val settingsFragment = SettingsFragment()


    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
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

        val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        val window: Window = getWindow()

        // In Activity's onCreate() for instance
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        configureNavigationDrawer()

        StartTimer()

        if (Auth.user != null) {
            // starting fragment is communityFragment
            replaceFragment(communityFragment)
            bottom_app_bar.visibility = View.VISIBLE
            //bottom_navigation.visibility = View.VISIBLE

//            if (Auth.user!!.isAnonymous) {
//                bottom_navigation.visibility = View.INVISIBLE
//
//                // when app is opened, redirect user to AuthActivity if they are not signed in
//                // or if their last use was as a Guest
//
//            }
//            else {
//                bottom_navigation.visibility = View.VISIBLE
//
//                }

            if (!Auth.user?.displayName.isNullOrEmpty()) {
                CustomToast(
                    this,
                    "Welcome to tellsquare, %s".format(Auth.user?.displayName)
                ).success()
            }
//            else {
//                CustomToast(this, "Welcome to tellsquare, Guest").success()
//            }
        }

        // the following code will replace the current fragment based on the selected navigation
        // item from the bottom navigation bar
//        bottom_navigation.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.ic_home -> replaceFragment(homeFragment)
//                R.id.ic_people -> replaceFragment(communityFragment)
//                R.id.ic_feed -> replaceFragment(feedFragment)
//                R.id.ic_profile -> replaceFragment(profileFragment)
//            }
//
//            true
//        }
//

        bottom_app_bar.setOnClickListener {
            Log.d("bottom_app_bar", "FIRED HERE")
        }

        bottomNavigationView.setOnClickListener {
            Log.d("bottomNavigationView", it.id.toString())
            when(it.id)
            {
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_people -> replaceFragment(communityFragment)
                R.id.ic_feed -> replaceFragment(feedFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }
        }



        fab_main.setOnClickListener {
            Log.d("fab_main", "FIRED!!")
        }

        // Close navDrawer when user clicks on Left Chevron icon
        val closeButton: ImageView = closeFromNavView
        closeButton.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        // Navigate to account fragment when user clicks on 'Gear' icon from navDrawer
        val accountButton: ImageView = accountSettings
        accountButton.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            addFragment(settingsFragment)
        }
    }

    public fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    public fun replaceFragment(fragment: Fragment) {
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
        } else {
            addFragment(fragment)
        }
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
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, f)
            transaction.commit()
            true
        } else
            false

        val drawerLayout: DrawerLayout = drawer_layout
        drawerLayout.closeDrawers()
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