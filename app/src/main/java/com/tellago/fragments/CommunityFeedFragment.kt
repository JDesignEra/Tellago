package com.tellago.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.adapters.UserPostCommunityRecyclerAdapter
import com.tellago.models.Communities
import com.tellago.models.Post
import kotlinx.android.synthetic.main.fragment_community_feed.*


class CommunityFeedFragment : Fragment() {

    private lateinit var adapter: UserPostCommunityRecyclerAdapter
    private var communityID_received: String? = null

    private var bundle: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityID_received = requireActivity().intent.getStringExtra("communityID")
        Log.d("CID in CommFeed is: ", communityID_received)

        if (this.arguments != null) bundle = requireArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_feed, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate card views (not yet in adapter) using values obtained from Firestore query
        Communities(cid = communityID_received).getByCid {
            if (it != null) {
                Log.d("Community Feed ID: ", it.cid)
            }
        }

        // Query uids map based on user?uid as key
        val communityID : ArrayList<String> = ArrayList()
        communityID_received?.let { communityID.add(it) }

        Log.d("CommunityID is: ", communityID.toString())

        // Query from Firestore to be passed to Adapter
        adapter = UserPostCommunityRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    Post.collection
                        .whereArrayContainsAny("cids", communityID)
                        .orderBy("createDate", Query.Direction.DESCENDING)
                    ,
                    Post::class.java
                ).build()
        )


        recycler_view_community_feed.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_community_feed.adapter = adapter

        adapter.startListening()
    }
}