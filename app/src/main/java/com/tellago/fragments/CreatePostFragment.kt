package com.tellago.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Journey
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
    private var journey = Journey()
    private var bundle: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) post = bundle!!.getParcelable(post::class.java.name)!!

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


        // On load without bundle, default view should be Message Post
        chip_message_radioToggle.isChecked = true
        chip_poll_radioToggle.isChecked = false
        chip_multimedia_radioToggle.isChecked = false


        val post_type_from_bundle = post.postType

        // Changes to chip highlight depending on Bundle value
        when (post_type_from_bundle) {
            "text post" -> chip_message_radioToggle.isChecked = true
            "poll" -> chip_poll_radioToggle.isChecked = true
            "multimedia" -> chip_multimedia_radioToggle.isChecked = true
        }

        // Display initial layout depending on chip radio toggles
        // For initial selection
        resetLayoutUsingChipRadioToggle()


        var lastCheckedID = View.NO_ID
        chipGrp_post_type.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == View.NO_ID) {
                // User tried to uncheck, make sure to keep the chip checked
                group.check(lastCheckedID)
                return@setOnCheckedChangeListener
            }
            lastCheckedID = checkedId
            Log.d("lastCheckedID : ", lastCheckedID.toString())

            // For new selection
            resetLayoutUsingChipRadioToggle()

        }


        // Assign values to text view for jid if it is available from bundle
        val availableJID = bundle?.getStringArrayList("arrayListString")
        if (availableJID != null) {
            // textView_jid_from_bundle remains View.GONE

            availableJID.remove(null)
//            availableJID.remove("null")
            Log.d("received availableJID", availableJID.toString())
            val journeyTitleList = ArrayList<String>()
            for (jid in availableJID) {
                Log.d("jid is", jid)
                Journey(jid = jid).getByJid {
                    val journeyTitle = it?.title
                    Log.d("journey title", journeyTitle)
                    if (journeyTitle != null) {
                        journeyTitleList.add(journeyTitle)
                        Log.d("journey list is: ", journeyTitleList.toString())
                        create_post_journey_titles_mcv.visibility = View.VISIBLE
                        text_view_selected_journey_titles.visibility = View.VISIBLE
                        textView_journey_titles.visibility = View.VISIBLE
                        val previousTitle = textView_journey_titles.text
                        // set new title(s)

                        if (previousTitle == "") {
                            textView_journey_titles.text = "$journeyTitle"
                        } else {
                            textView_journey_titles.text = "$previousTitle, $journeyTitle"
                        }

                    } else {
                        create_post_journey_titles_mcv.visibility = View.GONE
                        text_view_selected_journey_titles.visibility = View.GONE
                        textView_journey_titles.visibility = View.GONE
                    }


                    // Replace this text view with for loop values instead of displaying entire List
                    textView_jid_from_bundle.text = journeyTitleList.toString()
                    Log.d("journey title TV", textView_jid_from_bundle.text.toString())

                }

            }


        }


        // Run this sequence of code onViewCreated instead of onClickListener for btn_AddJourney
        val availableJourneysArrayList = ArrayList<String>()
        var jidsFromGoal = ArrayList<String>()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("goals").whereEqualTo("uid", user?.uid)
            .get()
            .addOnSuccessListener { it ->
                Log.d("it is: ", it.toString())
                if (it.isEmpty)
                {
                    linear_layout_journey_selection_create_post.visibility = View.GONE
                }
                else {
                    linear_layout_journey_selection_create_post.visibility = View.VISIBLE
                }

                for (document in it.documents) {
                    jidsFromGoal = document["jid"] as ArrayList<String>
                    Log.d("jidsFromGoal is", jidsFromGoal.toString())
                    for (jid in jidsFromGoal) {
                        availableJourneysArrayList.add(jid)
                    }
                    Log.d("availableJourneys", availableJourneysArrayList.toString())
                }
            }
            .addOnFailureListener {
                linear_layout_journey_selection_create_post.visibility = View.GONE
            }




        btn_AddJourney.setOnClickListener {
            Log.d("Button for journey", "FIRED")

            // Hide keyboard before displaying the next Fragment
            it.hideKeyboard()

            val bundle = Bundle()
            updatePostModelPartial()
            updateAndStorePollOptions()
            Log.d("storing poll values", post.poll.toString())


            Log.d("availJourneysArrayList1", availableJourneysArrayList.toString())


            // short redirect to new fragment to select from available Journeys
            val attachPostToJourneysFragment = AttachPostToJourneysFragment()
            attachPostToJourneysFragment.arguments = bundle.apply {
                putParcelable(post::class.java.name, post)
                putStringArrayList("availableJourneysArrayList", availableJourneysArrayList)
                // Function not completed: Pass previously selected journey title to AttachPostToJourneysFragment
//                putString("attachedJourneys", textView_jid_from_bundle.text.toString())
            }
            fragmentUtils.replace(attachPostToJourneysFragment)


        }


        linear_layout_add_poll_option.setOnClickListener {
            Log.d("button for adding poll", "FIRED")

            linear_layout_poll_options.addView(createNewPollOptionEditText())
            linear_layout_poll_remove_buttons.addView(createNewPollOptionDeleteButton())

            updateAndStorePollOptions()
            Log.d("Assign poll option ADD", post.poll.toString())

        }


        constraint_layout_create_post.setOnClickListener {
            Log.d("Creating post", "FIRED")

            post.uid = user?.uid

            // hide the keyboard before attempting to Create Post
            it.hideKeyboard()

            // conditional based on type of post
            if (post.postType == "text post") {
                post.messageBody = et_PostMessage.text.toString()
                clearPostModelMultimediaURI()

                post.add {
                    if (it != null) {
                        // iterate through availableJID to updateByJid
                        if (availableJID != null) {
                            updateJourneyPIDS(availableJID)
                        }
                        // Post created successfully, so redirect to Feed fragment (do so after Community Feature is Complete)
//                        fragmentUtils.replace(FeedFragment(), null)
                        fragmentUtils.replace(ProfileFragment(), null)

                        toast.success("Text Post created")
                    } else toast.error("Please try again, there was an error creating your post")
                }

            } else if (post.postType == "poll") {
                post.pollQuestion = et_PollQuestion.text.toString()
                clearPostModelMultimediaURI()

                updateAndStorePollOptions()

                post.add {
                    if (it != null) {
                        // iterate through availableJID to updateByJid
                        if (availableJID != null) {
                            updateJourneyPIDS(availableJID)
                        }
                        // Post created successfully, so redirect to Feed fragment (do so after Community Feature is Complete)
//                        fragmentUtils.replace(FeedFragment(), null)
                        fragmentUtils.replace(ProfileFragment(), null)

                        toast.success("Poll created")
                    } else toast.error("Please try again, there was an error creating your post")
                }

            } else if (post.postType == "multimedia") {
                post.messageBody = et_PostMessage_for_media.text.toString()

                post.add {
                    if (it != null) {

                        Log.d("added multimediaURI", post.multimediaURI.toString())
                        val multimediaURI = post.multimediaURI?.toUri()

                        if (multimediaURI != null) {
                            post.uploadPostMedia(multimediaURI).addOnSuccessListener {
                                toast.success("Attach successful")

                                // reassign post.multimediaURI to match storageRef of postID ???
                                post.multimediaURI = post.pid
                                // iterate through availableJID to updateByJid
                                if (availableJID != null) {
                                    updateJourneyPIDS(availableJID)
                                }


                            }.addOnFailureListener {
                                // Failed to upload
                                toast.error("Error occurred during attach")
                            }
                        }

                        // Post created successfully, so redirect to Feed fragment (do so after Community Feature is Complete)
//                        fragmentUtils.replace(FeedFragment(), null)
                        fragmentUtils.replace(ProfileFragment(), null)

                        toast.success("Multimedia Post created")
                    } else toast.error("Please try again, there was an error creating your post")
                }

            }


        }

    }

    private fun updateAndStorePollOptions() {
        // Assign options to the poll below (start off with Int = 0 for each poll option)
        val pollOptions = mutableListOf<String>()

        for (optionNo in 1..linear_layout_poll_options.childCount) {
            val editText: EditText = linear_layout_poll_options[optionNo - 1] as EditText
            Log.d("edit text: ", editText.text.toString())

            pollOptions.add(editText.text.toString())

        }

        val map = mutableMapOf<String, ArrayList<String>>()
        val pollArrayList = ArrayList<MutableMap<String, ArrayList<String>>>()

        for (optionNo in 1..pollOptions.size) {
            // pollOptions[optionNo] is the option String
            // assign map[String] = 0 because each option starts off with 0 votes/likes
            map[pollOptions[optionNo - 1]] = ArrayList<String>()
        }

        pollArrayList.add(map)
        post.poll = pollArrayList
    }


    private fun resetLayoutUsingChipRadioToggle() {
        if (chip_message_radioToggle.isChecked) {

            chip_poll_radioToggle.isChecked = false
            chip_multimedia_radioToggle.isChecked = false

            post.postType = "text post"

            // Displaying text for et_Message if messageBody available from Post Model
            if (post.messageBody != null) {
                et_PostMessage.setText(post.messageBody)
            }

            // toggle layout accordingly
            create_post_message_mcv.visibility = View.VISIBLE
            linear_layout_poll_toggle.visibility = View.GONE
            create_post_media_with_message_mcv.visibility = View.GONE
            create_post_message_for_media_mcv.visibility = View.GONE

        } else if (chip_poll_radioToggle.isChecked) {
            chip_message_radioToggle.isChecked = false
            chip_multimedia_radioToggle.isChecked = false

            post.postType = "poll"

            // Displaying text for et_PollQuestion if pollQuestion available from Post Model
            if (post.pollQuestion != null) {
                et_PollQuestion.setText(post.pollQuestion)
            }

            // poll = [{3=[], haha=[], fou=[]}]
            if (post.poll.isNotEmpty()) {
                // to store element values
                val pollElementsList: ArrayList<String> = ArrayList()
                // iterating
                for (stuffLayer1 in post.poll) {
                    Log.d("printing Pt1: ", stuffLayer1.toString())

                    for (Layer2 in stuffLayer1) {
                        Log.d("printing Pt2: ", Layer2.toString())
                        val splitElements = Layer2.toString().split("=")
                        pollElementsList.add(splitElements[0].toString())
                    }

                }
                Log.d("print elements", pollElementsList.toString())

                // Iterate through elements in pollElementsList to dynamically populate linear_layout_poll_options
                for (element in pollElementsList) {
                    linear_layout_poll_options.addView(populateNewPollOptionEditText(element))
                    linear_layout_poll_remove_buttons.addView(createNewPollOptionDeleteButton())
                    Log.d("new layout created", "FIRED")
                }

            }

            // toggle layout accordingly

            linear_layout_poll_toggle.visibility = View.VISIBLE
            create_post_message_mcv.visibility = View.GONE
            create_post_media_with_message_mcv.visibility = View.GONE
            create_post_message_for_media_mcv.visibility = View.GONE

        } else if (chip_multimedia_radioToggle.isChecked) {
            chip_poll_radioToggle.isChecked = false
            chip_message_radioToggle.isChecked = false

            post.postType = "multimedia"

            // Displaying image on attach_post_image if available from Post Model
            if (post.multimediaURI != null) {
                attach_post_image.visibility = View.VISIBLE
                setImage(post.multimediaURI!!.toUri())
            }

            textView_attachMedia.setOnClickListener {
                pickImageIntent()
                attach_post_image.visibility = View.VISIBLE
            }

            attach_post_image.setOnClickListener {
                pickImageIntent()
            }


            // toggle layout accordingly
            create_post_media_with_message_mcv.visibility = View.VISIBLE
            create_post_message_mcv.visibility = View.GONE
            linear_layout_poll_toggle.visibility = View.GONE

            // Display alternative text box for message when media tab is selected
            create_post_message_for_media_mcv.visibility = View.VISIBLE


        }
    }

    private fun updateJourneyPIDS(availableJID: java.util.ArrayList<String>) {
        for (jid in availableJID) {

            Journey(jid = jid).getByJid {
                val pids = it?.pids
                Log.d("before PIDS", "$pids")
                post.pid?.let { pids?.add(it) }
                if (pids != null) {
                    it.updateByJid(pids)
                }
                Log.d("after PIDS", "$pids")
            }

        }
    }

    private fun configureToolbar() {

        toolbar_create_post.setNavigationOnClickListener {
            fragmentUtils.popBackStack("addPostStack")
        }
    }


    private fun createNewPollOptionEditText(): EditText {

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val editTextPollOption = EditText(requireContext())
        editTextPollOption.setHint("Add option to poll...")

        // Text size to coincide with provided space & accompanying text in surroundings
        editTextPollOption.textSize = 16F


        editTextPollOption.layoutParams = lparams

        return editTextPollOption
    }

    private fun populateNewPollOptionEditText(userOptionInput: String): EditText {

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val editTextPollOption = EditText(requireContext())
        editTextPollOption.setText(userOptionInput)


        editTextPollOption.layoutParams = lparams

        return editTextPollOption
    }

    private fun createNewPollOptionDeleteButton(): Button {

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // button to remove corresponding edit text
        val btnRemoveET = Button(requireContext())

        // Use a 'minus' icon here
        btnRemoveET.setBackgroundResource(R.drawable.toolbar_cancel_icon_white)

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


            updateAndStorePollOptions()
            Log.d("Assign poll option REM", post.poll.toString())

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
                        uri ->
                    setImage(uri)

                    // Change text & display user's cropped image after they have completed image selection activity
                    textView_attachMedia.text = "Change Image"
                    updatePostModelMultimediaURI(uri)


                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("TAG", "Crop Error: ${result.error}")
            }
        }
    }

    private fun updatePostModelPartial() {
        // post.jid is meant to be used for Posts which post entire Journey (late game feature)
        // can use post.jid to store previously-selected Journeys as short-term feature --> still uncompleted
//        post.jid = textView_jid_from_bundle.text.toString()
        post.messageBody = et_PostMessage.text.toString()
        post.pollQuestion = et_PollQuestion.text.toString()
        post.uid = user?.uid

    }


    private fun updatePostModelMultimediaURI(uri: Uri) {
        post.multimediaURI = uri.toString()
    }

    private fun clearPostModelMultimediaURI() {
        post.multimediaURI = null
    }

    private fun startCrop(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setFixAspectRatio(true)
            .setMultiTouchEnabled(true)
            .start(requireActivity())
    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(attach_post_image)
    }


    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}