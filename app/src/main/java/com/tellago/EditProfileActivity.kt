package com.tellago

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.tellago.models.Auth
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*

class EditProfileActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        StartTimer()
        configureToolbar()

        editText_changeDisplayName.setText(Auth.profile?.displayName ?: "")
        editText_changeBio.setText(Auth.profile?.bio ?: "")
    }

//    override fun onBackPressed() {
//        //by default, kill the current activity
//    }

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
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    private fun configureToolbar() {
        // Indicate that toolbar_editProfile will replace Actionbar
        setSupportActionBar(toolbar_editProfile as Toolbar?)

        val actionbar: ActionBar? = supportActionBar
        // To 'hide' Title display in actionbar
        actionbar?.setTitle("Edit Profile")
        actionbar?.setDisplayHomeAsUpEnabled(true)

        // Navigate back to MainActivity (by closing the current Edit Profile Activity)
        (toolbar_editProfile as Toolbar?)?.setNavigationOnClickListener {
            this.finish()
            Log.d("back pressed", "you have pressed backbackback")
        }
    }
}
