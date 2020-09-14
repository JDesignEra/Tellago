package com.tellago

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tellago.fragments.ProfileTabFragment
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val profileTabFragment = ProfileTabFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        loadProfileFragment(profileTabFragment)
        StartTimer()

        button_edit_profile.setOnClickListener {
            var nextActivity : Intent = Intent(this, EditProfileActivity::class.java)
            startActivity(nextActivity)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

    private fun loadProfileFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        //transaction.replace(R.id.fragment_container_profile, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    override fun onBackPressed() {
        //slide from left to right to navigate back to Main Activity
        switchToMainActivity()
    }

    public fun switchToMainActivity() {
        var mainActivity : Intent = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
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