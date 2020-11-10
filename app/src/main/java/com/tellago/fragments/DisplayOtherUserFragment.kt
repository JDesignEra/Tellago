package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.models.Auth
import com.tellago.models.Post
import com.tellago.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_display_other_user.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.profile_image


class DisplayOtherUserFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var intendedUserID: String

    private lateinit var adapter: NewPostRecyclerAdapter
    private lateinit var post: Post


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        intendedUserID = bundle.getString("userID").toString()

        post = Post()

        adapter = NewPostRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    Post.collection
                        .orderBy("createDate", Query.Direction.DESCENDING)
                        .whereEqualTo("uid", intendedUserID),
                    Post::class.java
                ).build()
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_other_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = User(uid = intendedUserID)

        user.getUserWithUid {

            if (it != null) {
                profile_displayName_display_other_user.text =
                    String.format("${it.displayName}")

                profile_bio_display_other_user.text =
                    String.format("${it.bio}")

                loadOtherUserProfilePic(it.uid)

                tv_other_user_follower_count.text = it.followerUids.size.toString()
                tv_other_user_following_count.text = it.followingUids.size.toString()
                //tv_other_user_community_count.text


                if (it.followerUids.size == 1)
                {
                    // small change for grammar
                    tv_other_user_follower_count.text = "1"
                    tv_other_user_follower_display.text = "Follower"
                }

                if (it.followingUids.size == 1)
                {
                    // small change for grammar
                    tv_other_user_following_count.text = "1"
//                    tv_other_user_following_display.text = "following"
                }

//                if (tv_other_user_community_count.text == "1")
//                {
//                    // small change for grammar
//                    tv_other_user_community_count.text = "1"
//                    tv_other_user_community_display.text = "community"
//                }


            }

            recycler_view_display_other_user_profile_fragment.layoutManager =
                LinearLayoutManager(requireContext())
            recycler_view_display_other_user_profile_fragment.adapter = adapter

        }


        toolbar_display_other_user_navigation_cancel.setOnClickListener {
            requireActivity().finish()
        }


        // Initial layout for 'Follow' & 'YetToFollow' based on whether intendedUser's UID is
        // contained within currentUser's followingUids

        val currentUser = Auth.user?.uid?.let { User(uid = it) }

        currentUser!!.getUserWithUid {
            if (it != null) {
                if (intendedUserID in it.followingUids) {

                    linear_layout_follow_other_user_yetToFollow.visibility = View.GONE
                    linear_layout_follow_other_user_followed.visibility = View.VISIBLE
                }
                else
                {

                    linear_layout_follow_other_user_followed.visibility = View.GONE
                    linear_layout_follow_other_user_yetToFollow.visibility = View.VISIBLE
                }

            }
        }


        // Variable checks if current user was originally a follower of intended user
        var initialFollower = 0
        if (Auth.user?.uid in user.followerUids)
        {
            initialFollower = 1
        }


        // simple layout change to toggle the 'Following' status of the current profile
        linear_layout_follow_other_user_yetToFollow.setOnClickListener {
            // Function to 'Follow' intended User
//            it.userFollowUser(it.uid, intendedUserID)
            user.userFollowUser(currentUser.uid, intendedUserID)
            linear_layout_follow_other_user_yetToFollow.visibility = View.GONE
            linear_layout_follow_other_user_followed.visibility = View.VISIBLE


            // Update follower count (force client-facing layout to update manually)
            // if current user was not an initial follower, then following will
            // increase intended follower size by 1
            if (initialFollower == 0)
            {
                tv_other_user_follower_count.text = "${user.followerUids.size + 1}"
            }
            else
            {
                tv_other_user_follower_count.text = "${user.followerUids.size}"
            }

            if (tv_other_user_follower_count.text == "1")
            {
                // small change for grammar
                tv_other_user_follower_display.text = "Follower"
            }


        }

        linear_layout_follow_other_user_followed.setOnClickListener {
            // Function to 'Unfollow' intended User
            user.userFollowUser(currentUser.uid, intendedUserID)
            linear_layout_follow_other_user_followed.visibility = View.GONE
            linear_layout_follow_other_user_yetToFollow.visibility = View.VISIBLE


            // Update follower count (force client-facing layout to update manually)
            // if current user was an initial follower, then unfollowing will decrease
            // intended follower size by 1
            if (initialFollower == 1)
            {
                tv_other_user_follower_count.text = "${user.followerUids.size - 1}"
            }
            else
            {
                tv_other_user_follower_count.text = "${user.followerUids.size}"
            }

            if (user.followerUids.size == 1)
            {
                // small change for grammar
                tv_other_user_follower_count.text = "1"
                tv_other_user_follower_display.text = "Follower"
            }


        }

    }

    fun loadOtherUserProfilePic(uid: String) {

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")
        val storageRef = storage.reference

        GlideApp.with(this)
            .load(storageRef.child("uploads/dp/$uid"))
            .apply {
                transform(CenterInside(), CircleCrop())
            }.into(profile_image_display_other_user)

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