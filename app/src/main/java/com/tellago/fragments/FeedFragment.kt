package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.adapters.FeedAdapter
import com.tellago.adapters.UserPostCommunityRecyclerAdapter
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Communities
import com.tellago.models.Post
import com.tellago.models.User
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user?.uid?.let { uid ->
            User(uid = uid).getUserWithUid { user ->
                Communities().getByUid(uid) { communities ->
                    Log.e(this::class.java.name, communities?.size.toString())
                    if (user?.followingUids!!.isNotEmpty()) {
                        Post(uid = uid).getPostsByUids(user.followingUids) {
                            val posts = it ?: ArrayList()


                            Post(cids = communities?.map { it.cid } as ArrayList<String>).getPostsByCids {
                                if (it != null) posts.addAll(it)

                                feed_recyclerView.layoutManager =
                                    LinearLayoutManager(requireContext())
                                feed_recyclerView.adapter = FeedAdapter(posts)
                            }
                        }
                    } else {
                        Log.e(this::class.java.name, "Fired")

                        val cids = ArrayList<String>()
                        communities?.forEach {
                            it.cid?.let { it1 -> cids.add(it1) }
                        }



                        Post(cids = cids).getPostsByCids {
                            Log.e(this::class.java.name, "Fired 1")
                            if (it != null) {
                                Log.e(this::class.java.name, "Fired 2")
                                feed_recyclerView.layoutManager =
                                    LinearLayoutManager(requireContext())

                                it.sortWith(compareByDescending { it.createDate })

                                feed_recyclerView.adapter = FeedAdapter(it)
                            }
                        }
                    }
                }
            }
        }
    }
}