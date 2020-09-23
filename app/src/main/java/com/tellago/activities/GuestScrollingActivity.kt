package com.tellago.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.DataSource
import com.tellago.R
import com.tellago.TopSpacingItemDecoration
import com.tellago.adapters.UserPostRecyclerAdapter
import com.tellago.services.ExitService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_guest_to_sign_in_up.*


class GuestScrollingActivity : AppCompatActivity() {

    private lateinit var userPostAdapter: UserPostRecyclerAdapter
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("Guest status bar", "FIRED")
            //  set status text dark after check for minimum SDK
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        setContentView(R.layout.bottom_guest_to_sign_in_up)

        initRecyclerView()
        addDataSet()

        StartTimer()

        val window: Window = getWindow()

        // In Activity's onCreate() for instance
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


//        window.decorView.setOnSystemUiVisibilityChangeListener {
//            (bottomAppBarCoordinatorLayout.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0
//
//            if (it == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
//                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//                Log.d("HIDE_NAV", "FIRED")
//            }
//            else {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//                Log.d("UNHIDE_NAV", "FIRED")
//            }
//        }

        fab.setOnClickListener {
            Log.d("Floating Action Button", "Fired Floating Action Button!")
            // close GuestScrollingActivity.
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
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