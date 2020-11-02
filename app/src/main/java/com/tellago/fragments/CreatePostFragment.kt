package com.tellago.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlin.math.roundToInt

class CreatePostFragment : Fragment() {
    private lateinit var toast: CustomToast
    private lateinit var fragmentUtils: FragmentUtils

    private var post = Post()
    private var selectedJourneyTitles: ArrayList<String> = ArrayList()
    private var selectedJids: ArrayList<String> = ArrayList()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        when (post.postType) {
            "multimedia" -> {
                chip_multimedia_radioToggle.isChecked = true
                post_msg_mcv.visibility = View.GONE
                poll_mcv.visibility = View.GONE
                media_mcv.visibility = View.VISIBLE

                imageUri?.let {setImage(it) }
            }
            "poll" -> {
                chip_poll_radioToggle.isChecked = true
                post_msg_mcv.visibility = View.GONE
                media_mcv.visibility = View.GONE
                poll_mcv.visibility = View.VISIBLE

                if (post.poll.isNotEmpty()) {
                    for (m in post.poll) {
                        addPollOptionView(m.key)
                    }
                }
            }
            else -> chip_message_radioToggle.isChecked = false
        }

        chipGrp_post_type.setOnCheckedChangeListener { _, checkedId ->
            post_msg_mcv.visibility = View.GONE
            media_mcv.visibility = View.GONE
            poll_mcv.visibility = View.GONE

            when (checkedId) {
                chip_multimedia_radioToggle.id -> media_mcv.visibility = View.VISIBLE
                chip_poll_radioToggle.id -> poll_mcv.visibility = View.VISIBLE
                else -> post_msg_mcv.visibility = View.VISIBLE
            }
        }

        if (selectedJourneyTitles.isNotEmpty()) {
            linearLayout_selected_journey_titles.removeAllViews()

            for (title in selectedJourneyTitles) {
                val textView = TextView(requireContext()).apply {
                    text = title
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextDarkGray))
                }

                linearLayout_selected_journey_titles.addView(textView)
            }
        }

        media_imageView.setOnClickListener {
            it.hideKeyboard()
            pickImageIntent()
        }

        media_textView.setOnClickListener {
            it.hideKeyboard()
            pickImageIntent()
        }

        add_poll_iv.setOnClickListener {
            it.hideKeyboard()
            addPollOptionView()
        }

        attach_journey_iv.setOnClickListener {
            val attachPostToJourneysFragment = AttachPostToJourneysFragment()
            it.hideKeyboard()

            setPostModel()
            attachPostToJourneysFragment.arguments = Bundle().apply {
                putParcelable(post::class.java.name, post)
                putStringArrayList("selectedJids", selectedJids)
                if (post.postType == "multimedia") putString("imageUri", imageUri.toString())
            }

            fragmentUtils.replace(
                attachPostToJourneysFragment,
                setTargetFragment = this,
                requestCode = -1
            )
        }

        constraint_layout_create_post.setOnClickListener {
            it.hideKeyboard()
            setPostModel()

            val errors: MutableMap<String, String> = mutableMapOf()

            if (chip_message_radioToggle.isChecked && msg_et.text.isNullOrBlank()) errors["msg"] = "Field is required"
            else if (chip_multimedia_radioToggle.isChecked) {
                if (mediaMsg_et.text.toString().isNullOrBlank()) errors["mediaMsg"] = "Field is required"
                if (imageUri == null) errors["mediaImage"] = "You have not picked an image for your post."
            }
            else if (chip_poll_radioToggle.isChecked) {
                if (pollMsg_et.text.isNullOrBlank()) errors["pollMsg"] = "Field is required"
                if (post.poll.isNullOrEmpty()) errors["pollOptions"] = "You need at least 1 poll option"
            }

            if (errors.isNotEmpty()) {
                errors["msg"]?.let { msg_et.error = it }
                errors["mediaMsg"]?.let { mediaMsg_et.error = it }
                errors["mediaImage"]?.let { if (it.isNotBlank()) toast.error(it) }
                errors["pollMsg]"]?.let { pollMsg_et.error = it }
                errors["pollOptions"]?.let { if (it.isNotBlank()) toast.error(it) }
            }
            else {
                post.add {
                    if (it != null) {
                        if (it.postType == "multimedia") imageUri?.let { uri -> it.uploadPostMedia(uri) }

                        if (selectedJids.isNotEmpty()) {
                            for ((i, jid) in selectedJids.withIndex()) {
                                it.pid?.let { pid -> Journey(jid).addPidByJid(pid) }

                                if (i >= selectedJids.size - 1) {
                                    toast.success("Post created successfully")
                                    fragmentUtils.popBackStack()
                                }
                            }
                        }
                        else {
                            toast.success("Post created successfully")
                            fragmentUtils.popBackStack()
                        }
                    }
                    else toast.error("Fail to create post, please try again")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                    val imageUri = CropImage.getPickImageResultUri(requireContext(), data)

                    if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)) {
                        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                    }
                    else {
                        startCrop(imageUri)
                    }
                }
                -1 -> {
                    selectedJourneyTitles = data?.getStringArrayListExtra("selectedJourneyTitles") ?: ArrayList()
                    selectedJids = data?.getStringArrayListExtra("selectedJids") ?: ArrayList()
                    post = data?.getParcelableExtra(post::class.java.name) ?: Post()
                    data?.getStringExtra("imageUri").let {
                        if (it != null) Uri.parse(it)
                    }
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {
                result.uri.let {
                    imageUri = it
                    setImage(it)
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error
            }
        }
    }

    private fun configureToolbar() {
        toolbar_create_post.setNavigationOnClickListener {
            it.hideKeyboard()
            fragmentUtils.popBackStack("addPostStack")
        }
    }

    private fun setPostModel() {
        when {
            chip_message_radioToggle.isChecked -> {
                post = Post(
                    uid = user?.uid,
                    messageBody = msg_et.text.toString(),
                    postType = "text post"
                )
            }
            chip_multimedia_radioToggle.isChecked -> {
                post = Post(
                    uid = user?.uid,
                    messageBody = mediaMsg_et.text.toString(),
                    postType = "multimedia"
                )
            }
            chip_poll_radioToggle.isChecked -> {
                val pollOptions: MutableMap<String, ArrayList<String>> = mutableMapOf()

                for (v in linear_layout_poll_options.children) {
                    if (v is TextInputLayout) {
                        for (view in (v[0] as ViewGroup).children) {
                            if (view is TextInputEditText) {
                                pollOptions[view.text.toString()] = ArrayList()
                            }
                        }
                    }
                }

                post = Post(
                    uid = user?.uid,
                    messageBody = pollMsg_et.text.toString(),
                    poll = pollOptions,
                    postType = "poll"
                )
            }
        }
    }

    private fun addPollOptionView(text: String? = null) {
        pollPlaceholder_tv.visibility = View.GONE

        val textInputLayout = TextInputLayout(requireContext()).apply {
            isEndIconVisible = true
            endIconDrawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_remove_circle_outline_24
            )
        }
        textInputLayout.setEndIconOnClickListener {
            it.hideKeyboard()
            linear_layout_poll_options.removeView(it.parent.parent.parent.parent as View)

            if (linear_layout_poll_options.childCount < 2) {
                pollPlaceholder_tv.visibility = View.VISIBLE
            }
        }

        val textInputEditText = TextInputEditText(requireContext()).apply {
            if (!text.isNullOrBlank()) setText(text)
            width = LinearLayout.LayoutParams.MATCH_PARENT
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTransparent))
        }

        textInputLayout.addView(textInputEditText)
        linear_layout_poll_options.addView(textInputLayout)
    }

    private fun pickImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_GET_CONTENT

        context?.let {
            CropImage.activity().start(it, this)
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

    private fun setImage(uri: Uri) {
        GlideApp.with(this)
            .load(uri)
            .transform(
                CenterInside(),
                RoundedCorners((8 * resources.displayMetrics.density).roundToInt())
            ).into(media_imageView)

        media_textView.text = "Change Image"
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}