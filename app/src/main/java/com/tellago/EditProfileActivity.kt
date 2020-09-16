package com.tellago

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.tellago.models.Auth
import com.tellago.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*

class EditProfileActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val PICK_IMAGE_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        StartTimer()
        configureToolbar()

        editText_changeDisplayName.setText(Auth.profile?.displayName ?: "")
        editText_changeBio.setText(Auth.profile?.bio ?: "")

        saveBtn.setOnClickListener {
            Auth().update(
                displayName = editText_changeDisplayName.text.toString(),
                bio = editText_changeBio.text.toString()
            )

        }

        imageView_changePhoto.setOnClickListener {
            pickImageIntent()
        }

        textView_changePhoto.setOnClickListener {
            pickImageIntent()
        }

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
        }
    }


    private fun pickImageIntent(){
        Log.d("pickImageINTENT", "creating INTENT")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE){
            if (resultCode == Activity.RESULT_OK)
            {
                // pick single image
                val imageUri = data?.data

                // display selected image as profile_image (only shown locally; not yet updated to Storage)
                profile_image.setImageURI(imageUri)

                // update profile_image for current user (update to Firebase Storage)


            }
        }

    }

}
