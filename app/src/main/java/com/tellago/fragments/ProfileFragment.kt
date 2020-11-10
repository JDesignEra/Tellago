package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.activities.EditProfileActivity
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.models.Auth.Companion.profile
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Communities
import com.tellago.models.Post
import com.tellago.models.User
import kotlinx.android.synthetic.main.fragment_display_other_user.*
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

        adapter = NewPostRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    Post.collection
                        .orderBy("createDate", Query.Direction.DESCENDING)
                        .whereEqualTo("uid", user?.uid),
                    Post::class.java
                ).build()
        )
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

        User(uid = user!!.uid).getUserWithUid {
            if (it != null) {
                tv_profile_follower_count.text = it.followerUids.size.toString()
                tv_profile_following_count.text = it.followingUids.size.toString()
                //tv_profile_community_count.text


                if (it.followerUids.size == 1)
                {
                    // small change for grammar
                    tv_profile_follower_count.text = "1"
                    tv_profile_follower_display.text = "Follower"
                }

                if (it.followingUids.size == 1)
                {
                    // small change for grammar
                    tv_profile_following_count.text = "1"
//                    tv_profile_following_display.text = "following"
                }

//                if (tv_profile_community_count.text == "1")
//                {
//                    // small change for grammar
//                    tv_profile_community_count.text = "1"
//                    tv_profile_community_display.text = "community"
//                }

            }
        }



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