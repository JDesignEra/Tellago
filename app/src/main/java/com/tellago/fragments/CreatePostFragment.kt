package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.tellago.R
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Post
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_post.*


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
                btn_add_poll_option.visibility = View.GONE
                linear_layout_poll_options.visibility = View.GONE
                linear_layout_poll_remove_buttons.visibility = View.GONE
                textView_attachMedia.visibility = View.GONE
            }
            else if (chip_poll_radioToggle.isChecked) {
                chip_message_radioToggle.isChecked = false
                chip_multimedia_radioToggle.isChecked = false

                post.postType = "poll"

                // toggle layout accordingly
                textinputlayout_pollQuestion.visibility = View.VISIBLE
                btn_add_poll_option.visibility = View.VISIBLE
                linear_layout_poll_options.visibility = View.VISIBLE
                linear_layout_poll_remove_buttons.visibility = View.VISIBLE
                textinputlayout_messageBody.visibility = View.GONE
                textView_attachMedia.visibility = View.GONE
            }
            else if (chip_multimedia_radioToggle.isChecked) {
                chip_poll_radioToggle.isChecked = false
                chip_message_radioToggle.isChecked = false

                post.postType = "multimedia"

                textView_attachMedia.setOnClickListener {
                    pickImageIntent()
                    attach_post_image.visibility = View.VISIBLE
                }

                attach_post_image.setOnClickListener {
                    pickImageIntent()
                }


                // toggle layout accordingly
                textView_attachMedia.visibility = View.VISIBLE
                textinputlayout_messageBody.visibility = View.GONE
                textinputlayout_pollQuestion.visibility = View.GONE
                btn_add_poll_option.visibility = View.GONE
                linear_layout_poll_options.visibility = View.GONE
                linear_layout_poll_remove_buttons.visibility = View.GONE
            }

        }



        btn_add_poll_option.setOnClickListener {
            Log.d("button for adding poll", "FIRED")

            linear_layout_poll_options.addView(createNewPollOptionEditText())
            linear_layout_poll_remove_buttons.addView(createNewPollOptionDeleteButton())
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
                val pollOptions = mutableListOf<String>()

                for (optionNo in 1 .. linear_layout_poll_options.childCount)
                {
                    val editText : EditText = linear_layout_poll_options[optionNo - 1] as EditText
                    Log.d("edit text: ", editText.text.toString())

                    pollOptions.add(editText.text.toString())

                }

                val map = mutableMapOf<String, Int>()
                val pollArrayList = ArrayList<MutableMap<String, Int>>()

                for (optionNo in 1 .. pollOptions.size)
                {
                    // pollOptions[optionNo] is the option String
                    // assign map[String] = 0 because each option starts off with 0 votes/likes
                    map[pollOptions[optionNo - 1]] = 0
                }

                pollArrayList.add(map)

                post.poll = pollArrayList

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


                post.add {
                    if (it != null) {
                        Log.d("added multimediaURI", post.multimediaURI.toString())
                        val multimediaURI = post.multimediaURI?.toUri()

                        if (multimediaURI != null) {
                            post.uploadPostMedia(multimediaURI).addOnSuccessListener {
                                toast.success("Attach successful")

                                // reassign post.multimediaURI to match storageRef of postID ???
                                post.multimediaURI = post.pid

                            }.addOnFailureListener {
                                // Failed to upload
                                toast.error("Error occurred during attach")
                            }
                        }

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
            fragmentUtils.popBackStack("addPostStack")
        }
    }


    private fun createNewPollOptionEditText() : EditText {

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val editTextPollOption = EditText(requireContext())
        editTextPollOption.setHint("Add option to your poll...")


        editTextPollOption.layoutParams = lparams

        return editTextPollOption
    }

    private fun createNewPollOptionDeleteButton() : Button {

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // button to remove corresponding edit text
        val btnRemoveET = Button(requireContext())
        btnRemoveET.setBackgroundResource(R.drawable.ic_baseline_delete_outline_12)

        btnRemoveET.setOnClickListener {
            Log.d("Clicked on", btnRemoveET.toString())

            // linearParent will refer to Linear Layout with Horizontal Orientation & Weight of 5
            val linearParent = it.parent.parent as LinearLayout

            // linearSibling will refer to Linear Layout with Vertical Orientation & contains editText
            val linearSibling = linearParent[0] as LinearLayout

            val linearChild = it.parent as LinearLayout

            val indexOfCurrentButton = linearChild.indexOfChild(it)
            Log.d("current Child index: ", indexOfCurrentButton.toString())

            linearSibling.removeView(linearSibling[indexOfCurrentButton])
            linearChild.removeView(linearChild[indexOfCurrentButton])

        }

        btnRemoveET.layoutParams = lparams

        return btnRemoveET
    }

    private fun pickImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_GET_CONTENT

        Log.d("pickImageIntent", "FIRED")


        getContext()?.let {
            CropImage.activity()
                .start(it, this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("requestCode", requestCode.toString())

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // pick single image
            val imageUri = CropImage.getPickImageResultUri(requireContext(), data)
            Log.d("imageUri", imageUri.toString())

            if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)) {
                //uri = imageUri
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                Log.d("begin cropping", "FIRED")
                startCrop(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {
                val resultUri: Uri = result.uri
                Log.d("resultUri", resultUri.toString())

                resultUri.let {

                    // display selected image as attach_post_image (only shown locally; not yet updated to Storage)
                        uri ->  setImage(uri)

                    textView_attachMedia.text = "Change Image / Video"

                    post.multimediaURI = uri.toString()


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
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setFixAspectRatio(true)
            .setMultiTouchEnabled(true)
            .start(requireActivity())
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(attach_post_image)
    }

}