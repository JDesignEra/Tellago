package com.tellago.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.DataSource
import com.tellago.R
import com.tellago.TopSpacingItemDecoration
import com.tellago.adapters.UserPostRecyclerAdapter
import com.tellago.services.ExitService
import kotlinx.android.synthetic.main.bottom_guest_to_sign_in_up.*


class GuestScrollingActivity : AppCompatActivity() {


    private lateinit var userPostAdapter: UserPostRecyclerAdapter

    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        setContentView(R.layout.bottom_guest_to_sign_in_up)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("Guest status bar", "FIRED")
            //  set status text dark after check for minimum SDK
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val window: Window = getWindow()

        // In Activity's onCreate() for instance
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        /*initRecyclerView()
        addDataSet()*/


        Log.d("onCreate Guest", "Fired onCreate!")

        fab.setOnClickListener {
            Log.d("Floating Action Button", "Fired Floating Action Button!")
            // close GuestScrollingActivity.
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }


    private fun addDataSet() {
        // data created in DataSource data class should be retrieved from Firebase Storage & Cloud Firestore
        /*val data = DataSource.createDataSet()
        userPostAdapter.submitList(data)
        Log.d("addDataSet()", "FIRED")*/
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
}