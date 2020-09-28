package com.tellago

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.tellago.activities.AccountSettingsActivity
import com.tellago.activities.SplashActivity
import com.tellago.fragments.*
import com.tellago.interfaces.CreateGoalCommunicator
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
    private val settingsFragment = SettingsFragment()

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
            fragmentUtils.replace(homeFragment)

            if (!user?.displayName.isNullOrEmpty()) {
                toast.success("Welcome to tellsquare, %s".format(user?.displayName))
            }
        }

        // the following code will replace the current fragment based on the selected navigation
        // item from the bottom navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.ic_home -> {
                    fragmentUtils.replace(homeFragment)
                    true
                }
                R.id.ic_people -> {
                    fragmentUtils.replace(communityFragment)
                    true
                }
                R.id.ic_feed -> {
                    fragmentUtils.replace(feedFragment)
                    true
                }
                R.id.ic_profile -> {
                    fragmentUtils.replace(profileFragment)
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

        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            true
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val viewRect = Rect()

        profile_displayName.text = profile?.displayName ?: "Guest"
        profile_displayEmail.text = profile?.email ?: "guest@gmail.com"
        navigation.getGlobalVisibleRect(viewRect)
        profile?.displayProfilePicture(this, profile_image)

        // uncomment the following then make changes so that drawer can be SWIPED open from LEFT
//        if (!viewRect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
//            if (drawerLayout.isDrawerVisible((GravityCompat.START)))
//                drawerLayout.closeDrawer(GravityCompat.START)
//            else drawerLayout.openDrawer(GravityCompat.START)
//        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val f: Fragment? = null

        when (item.itemId) {
            R.id.view_profile -> fragmentUtils.replace(profileFragment)
            R.id.logout_from_drawer -> Auth().signOut(this) {
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
            }
        }

        if (f != null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, f)
            transaction.commit()
        }

        val drawerLayout: DrawerLayout = drawer_layout
        drawerLayout.closeDrawers()

        return true
    }


//    override fun passStringDataComOne(editTextStringInput: String) {
//        val bundle = Bundle()
//        bundle.putString("string 1", editTextStringInput)
//        Log.d("bundlePutString", editTextStringInput.toString())
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentTwo = CreateGoalFragmentTwo()
//        fragmentTwo.arguments = bundle
//
//        transaction.replace(R.id.fragment_container, fragmentTwo)
//        transaction.commit()
//
//    }

//    override fun passLongDataComOne(LongInput: Long) {
//        val bundle = Bundle()
//        bundle.putLong("Long 1", LongInput)
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentTwo = CreateGoalFragmentTwo()
//        fragmentTwo.arguments = bundle
//
//        transaction.replace(R.id.fragment_container, fragmentTwo)
//        transaction.commit()
//    }


//    override fun passTimestampDataComOne(TimeStampInput: Timestamp) {
//        val bundle = Bundle()
//        bundle.putString("Timestamp to String 1", TimeStampInput.toString())
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentTwo = CreateGoalFragmentTwo()
//        fragmentTwo.arguments = bundle
//
//        transaction.replace(R.id.fragment_container, fragmentTwo)
//        transaction.commit()
//    }
//
//
//    override fun passStringDataComTwo(editTextStringInput: String) {
//        val bundle = Bundle()
//        bundle.putString("string 2", editTextStringInput)
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentThree = CreateGoalFragmentThree()
//        fragmentThree.arguments = bundle
//
//        transaction.replace(R.id.fragment_container, fragmentThree)
//        transaction.commit()
//
//    }
//
//    override fun passLongDataComTwo(LongInput: Long) {
//        val bundle = Bundle()
//        bundle.putLong("Long 2", LongInput)
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentThree = CreateGoalFragmentThree()
//        fragmentThree.arguments = bundle
//
//        transaction.replace(R.id.fragment_container, fragmentThree)
//        transaction.commit()
//    }
//
//    override fun passTimestampDataComTwo(TimeStampInput: Timestamp) {
//        val bundle = Bundle()
//        bundle.putString("Timestamp to String 2", TimeStampInput.toString())
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentThree = CreateGoalFragmentThree()
//        fragmentThree.arguments = bundle
//
//        transaction.replace(R.id.fragment_container, fragmentThree)
//        transaction.commit()
//    }



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