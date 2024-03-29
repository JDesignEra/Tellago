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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.adapters.EditJourneyRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.models.Post.Companion.collection
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_edit_journey.*

class EditJourneyFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    private var goal = Goal()
    private var journey = Journey()
    private var adapter: EditJourneyRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name) ?: Goal()
        journey = bundle.getParcelable(journey::class.java.name) ?: Journey()
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        toast = CustomToast(requireContext())

        adapter = EditJourneyRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    collection
                        .whereEqualTo("uid", user?.uid)
                        .orderBy("createDate", Query.Direction.ASCENDING),
                    Post::class.java
                ).build()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar_edit_journey.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_edit_journey.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }

        journey.displayJourneyImage(requireContext(), iv_journey_image_edit_journey)
        et_journeyTitle_edit_journey.setText(journey.title)

        if (journey.pids.isNotEmpty()) {
            if (journey.pids.isNotEmpty()) adapter?.setPids(journey.pids)
        }

        post_recylcerView.layoutManager = LinearLayoutManager(requireContext())
        post_recylcerView.adapter = adapter

        iv_journey_image_edit_journey.setOnClickListener {
            pickImageIntent()
        }

        constraint_layout_edit_journey.setOnClickListener {
            journey.pids = adapter?.getPids() ?: ArrayList()

            if (et_journeyTitle_edit_journey.text!!.isNotBlank()) {
                adapter?.getPids()?.let { pids ->
                    Journey(
                        journey.jid,
                        title = et_journeyTitle_edit_journey.text.toString(),
                        pids = pids
                    ).updateByJid {
                        if (it != null) {
                            toast.success("Journey updated")

                            val intent = Intent(requireContext(), this::class.java)
                            intent.putExtra(journey::class.java.name, it)

                            targetFragment?.onActivityResult(
                                targetRequestCode,
                                Activity.RESULT_OK,
                                intent
                            )

                            fragmentUtils.popBackStack()
                        } else toast.error("Fail to update Journey, please try again")
                    }
                }
            } else et_journeyTitle_edit_journey.error = "Journey Name is required"
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.startListening()
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
                var uri_variable: Uri

                resultUri.let { uri ->
                    uri_variable = uri
                }

                setImage(uri_variable)
                journey.uploadJourneyImage(uri_variable)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("TAG", "Crop Error: ${result.error}")
            }
        }
    }

    private fun setImage(uri: Uri) {
        GlideApp.with(this)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(iv_journey_image_edit_journey)
    }

    private fun pickImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_GET_CONTENT

        Log.d("pickImageIntent", "FIRED")

        context?.let {
            CropImage.activity()
                .setAspectRatio(16, 9)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(it, this)
        }
    }

    private fun startCrop(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setAspectRatio(16, 9)
            .start(requireActivity())
    }
}