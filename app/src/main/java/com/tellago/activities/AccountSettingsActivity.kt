package com.tellago.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.fragments.SettingsFragment
import com.tellago.services.ExitService
import kotlinx.android.synthetic.main.activity_main.*

class AccountSettingsActivity : AppCompatActivity() {
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val settingsFragment = SettingsFragment()



    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)

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

        setContentView(R.layout.activity_account_settings)

        addFragment(settingsFragment)

        StartTimer()

    }


    fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.accountSettings_fragment_container, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit
            )
            transaction.replace(R.id.accountSettings_fragment_container, fragment)
            transaction.addToBackStack(null);
            transaction.commit()
        } else {
            addFragment(fragment)
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

    private fun hideSystemUI() {
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

}