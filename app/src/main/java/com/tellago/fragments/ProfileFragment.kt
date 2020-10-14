package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.activities.EditProfileActivity
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.models.Auth.Companion.profile
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Post
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var adapter: NewPostRecyclerAdapter
    private lateinit var post: Post

//    private var adapter: PostForCreateGoalRecyclerAdapter? = null
//    private var adapter: NewPostRecyclerAdapter? = null
    private var pids: ArrayList<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = Post()

        // Testing Firestore query
        val query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("uid", user?.uid).orderBy("createDate", Query.Direction.DESCENDING)



        adapter = NewPostRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    query,
                    Post::class.java
                ).build()
        )

//        adapter = PostForCreateGoal

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

        recycler_view_profile_fragment.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_profile_fragment.adapter = adapter


        button_edit_profile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Code to fetch updated profile information
        // Updated display name
        profile_displayName.text = profile?.displayName ?: "Guest"
        // Updated bio
        profile_bio.text = profile?.bio ?: "Introduce yourself to the others."
        // Updated profile picture
        profile?.displayProfilePicture(requireContext(), profile_image)


    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
}