package com.tellago.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.fragments.ConfirmEditProfileFragment
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.profile
import com.tellago.utilities.CustomToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), ConfirmEditProfileFragment.NoticeDialogListener {
    private lateinit var toast: CustomToast

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val PICK_IMAGE_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)

        setContentView(R.layout.activity_edit_profile)

        toast = CustomToast(this)

        // In Activity's onCreate() for instance
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        StartTimer()
        configureToolbar()
        profile?.displayProfilePicture(this, profile_image)

        editText_changeDisplayName.setText(profile?.displayName ?: "")
        editText_changeBio.setText(profile?.bio ?: "")

        updateBtn.setOnClickListener {
            if (editText_changeDisplayName.text.isNullOrEmpty()) {
                editText_changeDisplayName.error = "Display name cannot be empty"
            } else
                confirmEditProfileAlert()

        }

        profile_image.setOnClickListener {
            pickImageIntent()
        }

        textView_changePhoto.setOnClickListener {
            pickImageIntent()
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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun configureToolbar() {
        // Indicate that toolbar_editProfile will replace Actionbar
        setSupportActionBar(toolbar_editProfile as Toolbar?)

        val actionbar: ActionBar? = supportActionBar
        // To 'hide' Title display in actionbar
        actionbar?.setTitle("  Edit Profile")
        actionbar?.setDisplayHomeAsUpEnabled(false)
        
        (toolbar_editProfile as Toolbar?)?.setNavigationIcon(R.drawable.toolbar_cancel_icon)

        // Navigate back to MainActivity (by closing the current Edit Profile Activity)
        (toolbar_editProfile as Toolbar?)?.setNavigationOnClickListener {
            this.apply {
                this.finish()
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }


    }

    private fun pickImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_GET_CONTENT
        //startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_CODE)
        CropImage.startPickImageActivity(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // pick single image
            val imageUri = CropImage.getPickImageResultUri(this, data)

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                //uri = imageUri
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                startCrop(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri

                resultUri?.let {
                    // display selected image as profile_image (only shown locally; not yet updated to Storage)
                    uri ->  setImage(uri)

                    profile?.uploadDp(uri)?.addOnProgressListener {
                        // Can display a progress bar for upload status
                        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    }?.addOnSuccessListener {
                        toast.success("Profile Picture uploaded successfully")
                    }?.addOnFailureListener {
                        // Failed
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("TAG", "Crop Error: ${result.error}")
            }
        }
    }

    private fun startCrop(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.OVAL)
            .setFixAspectRatio(true)
            .setMultiTouchEnabled(true)
            .start(this)
    }

    private fun setImage(uri: Uri){
        GlideApp.with(this)
            .load(uri).apply {
                transform(CenterInside(), CircleCrop())
            }.into(profile_image)
    }

    private fun confirmEditProfileAlert() {
        val newFragment = ConfirmEditProfileFragment()
        newFragment.show(supportFragmentManager, "Edit Profile Confirmation")
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the ConfirmEditProfileFragment.NoticeDialogListener interface
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        // User touched the dialog's positive button
        Auth().update(
            editText_changeDisplayName.text.toString(),
            editText_changeBio.text.toString()
        )

        // Navigate back to the MainActivity + ProfileFragment, but make sure to update first
        finish() //finish EditProfileActivity.
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User touched the dialog's negative button, so close the ConfirmEditProfileFragment()
        supportFragmentManager.beginTransaction().remove(ConfirmEditProfileFragment()).commit()
    }
}
