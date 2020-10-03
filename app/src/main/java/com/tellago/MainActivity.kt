package com.tellago

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.tellago.activities.AccountSettingsActivity
import com.tellago.activities.GoalsActivity
import com.tellago.activities.SplashActivity
import com.tellago.fragments.CommunityFragment
import com.tellago.fragments.FeedFragment
import com.tellago.fragments.HomeFragment
import com.tellago.fragments.ProfileFragment
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.profile
import com.tellago.models.Auth.Companion.user
import com.tellago.services.ExitService
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_header.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toast: CustomToast

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val fragmentUtils = FragmentUtils(supportFragmentManager, R.id.fragment_container)
    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val feedFragment = FeedFragment()
    private val profileFragment = ProfileFragment()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService(Intent(this, ExitService::class.java))

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        toast = CustomToast(this)

        window.decorView.setOnSystemUiVisibilityChangeListener {
            (bottomAppBarCoordinatorLayout.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0

            if (it == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                Log.d("HIDE_NAV", "FIRED")
            }
            else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                Log.d("UNHIDE_NAV", "FIRED")
            }
        }

        setContentView(R.layout.activity_main)

        configureNavigationDrawer()
        StartTimer()

        if (user != null) {
            // starting fragment is homeFragment
            fragmentUtils.replace(homeFragment, null)

            if (!user?.displayName.isNullOrEmpty()) {
                toast.success("Welcome to tellsquare, %s".format(user?.displayName))
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.ic_home -> {
                    fragmentUtils.replace(homeFragment, null)
                    true
                }
                R.id.ic_people -> {
                    fragmentUtils.replace(communityFragment, null)
                    true
                }
                R.id.ic_feed -> {
                    fragmentUtils.replace(feedFragment, null)
                    true
                }
                R.id.ic_profile -> {
                    fragmentUtils.replace(profileFragment, null)
                    true
                }
                else -> false
            }
        }

        fab_main.setOnClickListener {
            // TODO: FAB logic
            Log.d("fab_main", "FIRED!!")
        }

        // Close navDrawer when user clicks on Left Chevron icon
        val closeButton: ImageView = closeFromNavView
        closeButton.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        // Navigate to account fragment when user clicks on 'Gear' icon from navDrawer
        val accountButton: LinearLayout = accountSettings
        accountButton.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
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

    private fun configureNavigationDrawer() {
        val navigationView: NavigationView = navigation
        val drawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, null, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)

                if (newState == DrawerLayout.STATE_DRAGGING || newState == DrawerLayout.STATE_SETTLING) {
                    profile_displayName.text = profile?.displayName ?: "tellsquare"
                    profile_displayEmail.text = profile?.email ?: "Welcome, Guest"
                    profile?.displayProfilePicture(drawer_layout.context, profile_image)
                }
            }
        }

        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navigationView: NavigationView = navigation

        when (item.itemId) {
            R.id.view_Life_Aspiration -> {
                val intent = Intent(this, GoalsActivity::class.java)
                startActivity(intent)
                this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }

            R.id.logout_from_drawer -> Auth().signOut(this) {
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
            }
        }

        val drawerLayout: DrawerLayout = drawer_layout
        drawerLayout.closeDrawers()

        return true
    }

    private fun hideSystemUI() {
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

}