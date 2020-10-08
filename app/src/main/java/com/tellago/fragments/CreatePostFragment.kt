package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Post
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_show_journeys.*


class CreatePostFragment : Fragment() {

    private lateinit var toast: CustomToast
    private lateinit var fragmentUtils: FragmentUtils

    private var post = Post()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        // On load, default view should be Message Post
        chip_message_radioToggle.isChecked = true


        var lastCheckedID = View.NO_ID
        chipGrp_post_type.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == View.NO_ID)
            {
                // User tried to uncheck, make sure to keep the chip checked
                group.check(lastCheckedID)
                return@setOnCheckedChangeListener
            }
            lastCheckedID = checkedId
            Log.d("lastCheckedID : ", lastCheckedID.toString())

            // For new selection
            if (chip_message_radioToggle.isChecked) {

                chip_poll_radioToggle.isChecked = false
                chip_multimedia_radioToggle.isChecked = false

                post.postType = "text post"

                // toggle layout accordingly
                textinputlayout_messageBody.visibility = View.VISIBLE
                textinputlayout_pollQuestion.visibility = View.GONE
                textView_attachMedia.visibility = View.GONE
            }
            else if (chip_poll_radioToggle.isChecked) {
                chip_message_radioToggle.isChecked = false
                chip_multimedia_radioToggle.isChecked = false

                post.postType = "poll"

                // toggle layout accordingly
                textinputlayout_pollQuestion.visibility = View.VISIBLE
                textinputlayout_messageBody.visibility = View.GONE
                textView_attachMedia.visibility = View.GONE
            }
            else if (chip_multimedia_radioToggle.isChecked) {
                chip_poll_radioToggle.isChecked = false
                chip_message_radioToggle.isChecked = false

                post.postType = "multimedia"

                textView_attachMedia.setOnClickListener {
                    pickImageIntent()
                }

                // Replace the following static data with user Input
                // post.multimediaURI = "someURI"

                Log.d("post.multimediaURI", post.multimediaURI.toString())

                // toggle layout accordingly
                textView_attachMedia.visibility = View.VISIBLE
                textinputlayout_messageBody.visibility = View.GONE
                textinputlayout_pollQuestion.visibility = View.GONE
            }

        }




        chip_add_poll.setOnClickListener {
            Log.d("chip for adding poll", "FIRED")
        }

        chip_add_journey.setOnClickListener {
            Log.d("chip for journey", "FIRED")
        }


        btn_CreatePost.setOnClickListener {
            Log.d("Creating post", "FIRED")

            post.uid = user?.uid
            // Assign journeyArray with user input below
            // Replace the following static data with user Input
            val journeyarraylist = ArrayList<String>()
            journeyarraylist.add("some journey ID")
            post.journeyArray = journeyarraylist

            // conditional based on type of post
            if (post.postType == "text post") {
                post.messageBody = et_PostMessage.text.toString()

                post.add {
                    if (it != null) {
                        // Post created successfully, so redirect to Feed fragment?
                        fragmentUtils.replace(FeedFragment(), null)

                        toast.success("Text Post created")
                    }
                    else toast.error("Please try again, there was an error creating your post")
                }

            }
            else if (post.postType == "poll")
            {
                post.pollQuestion = et_PollQuestion.text.toString()
                // Assign options to the poll below (start off with Int = 0 for each poll option)


                post.add {
                    if (it != null) {
                        // Post created successfully, so redirect to Feed fragment?
                        fragmentUtils.replace(FeedFragment(), null)

                        toast.success("Poll created")
                    }
                    else toast.error("Please try again, there was an error creating your post")
                }

            }
            else if (post.postType == "multimedia")
            {

//                textView_attachMedia.setOnClickListener {
//                    pickImageIntent()
//                }
//
//                // Replace the following static data with user Input
//                // post.multimediaURI = "someURI"
//
//                Log.d("post.multimediaURI", post.multimediaURI.toString())

                post.add {
                    if (it != null) {
                        // Post created successfully, so redirect to Feed fragment?
                        fragmentUtils.replace(FeedFragment(), null)

                        toast.success("Multimedia Post created")
                    }
                    else toast.error("Please try again, there was an error creating your post")
                }
            }


        }

    }

    private fun configureToolbar() {

        toolbar_create_post.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }


    private fun pickImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_GET_CONTENT

        Log.d("pickImageIntent", "FIRED")

        //startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_CODE)
        CropImage.startPickImageActivity(requireActivity())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // pick single image
            val imageUri = CropImage.getPickImageResultUri(requireContext(), data)

            if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)) {
                //uri = imageUri
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                startCrop(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {
                val resultUri: Uri = result.uri
                Log.d("resultUri", resultUri.toString())

                resultUri.let {
                    // assign uri to post.multimediaURI
                        uri ->  post.multimediaURI = uri.toString()

                    Auth.profile?.uploadDp(uri)?.addOnProgressListener {
                        // Can display a progress bar for upload status
                        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    }?.addOnSuccessListener {
                        toast.success("Attach successful")
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
            .start(requireActivity())
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(profile_image)
    }

}