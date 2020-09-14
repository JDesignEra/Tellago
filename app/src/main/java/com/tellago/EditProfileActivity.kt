package com.tellago

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*

class EditProfileActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        StartTimer()


    }

    override fun onBackPressed() {
        //slide from left to right to navigate back to Main Activity
        switchToProfileActivity()
    }

    public fun switchToProfileActivity() {
        var profileActivity : Intent = Intent(this, ProfileActivity::class.java)
        startActivity(profileActivity)
        //Slide from left to right
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }

}