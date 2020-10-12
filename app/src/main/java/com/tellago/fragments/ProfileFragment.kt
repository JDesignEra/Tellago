package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.DataSource
import com.tellago.R
import com.tellago.TopSpacingItemDecoration
import com.tellago.activities.EditProfileActivity
import com.tellago.adapters.ProfilePostRecyclerAdapter
import com.tellago.models.Auth.Companion.profile
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var profilePostAdapter: ProfilePostRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile?.displayProfilePicture(requireContext(), profile_image)
        initRecyclerView()
        addDataSet()

        button_edit_profile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }

    override fun onResume(){
        super.onResume()
        // Code to fetch updated profile information
        // Updated display name
        profile_displayName.text = profile?.displayName ?: "Guest"
        // Updated bio
        profile_bio.text = profile?.bio ?: "Introduce yourself to the others."
        // Updated profile picture
        profile?.displayProfilePicture(requireContext(), profile_image)
    }

    private fun addDataSet() {
        // data created in DataSource data class should be retrieved from Firebase Storage & Cloud Firestore
        val data = DataSource.createDataSetForHome()
        profilePostAdapter.submitList(data)
        Log.d("addDataSet()", "FIRED")
    }

    private fun initRecyclerView() {
        Log.d("initRecyclerView()", "FIRED")
        // recyclerview from fragment_home.xml
        recycler_view_home_fragment.apply {

            // Step 1: set layoutManager for recyclerview
            Log.d("LayoutManager Home", "FIRED")
            layoutManager = LinearLayoutManager(activity?.application?.baseContext)

            // Step 2: Adding padding decoration for spacing between viewholders (defined in TopSpacingItemDecoration.kt)
//            val topSpacingDecoration = TopSpacingItemDecoration(20)
//            addItemDecoration(topSpacingDecoration)

            // Step 3: Initialise the lateinit variable homePostAdapter
            profilePostAdapter = ProfilePostRecyclerAdapter()
            adapter = profilePostAdapter
        }
    }
}