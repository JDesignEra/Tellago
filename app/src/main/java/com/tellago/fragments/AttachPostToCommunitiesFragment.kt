package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.adapters.ShowAvailableCommunitiesForPostAttachRecyclerAdapter
import com.tellago.adapters.ShowAvailableJourneysForPostAttachRecyclerAdapter
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Communities
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_attach_post_to_communities.*
import kotlinx.android.synthetic.main.fragment_attach_post_to_journeys.*


class AttachPostToCommunitiesFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var adapter: ShowAvailableCommunitiesForPostAttachRecyclerAdapter

    private var bundle: Bundle? = null
    private var post = Post()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

        if (this.arguments != null) bundle = requireArguments()
        post = bundle?.getParcelable(post::class.java.name) ?: Post()
        Log.e(this::class.java.name, post.poll.size.toString())

        // Query uids map based on user?uid as key
        val documentField = "uids.${user?.uid}"
        val communityRole : ArrayList<String> = ArrayList()
        communityRole.add("user")
        communityRole.add("admin")

        adapter = ShowAvailableCommunitiesForPostAttachRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Communities>()
                .setQuery(
                    Communities.collection.whereIn(documentField, communityRole),
                    Communities::class.java
                ).build()

        )



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attach_post_to_communities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        recycler_view_show_availableCommunity_posts_fragment.layoutManager =
            LinearLayoutManager(requireContext())
        recycler_view_show_availableCommunity_posts_fragment.adapter = adapter.apply {
            bundle?.getStringArrayList("selectedCids")?.let {
                setSelectedCids(it)
            }
        }


        for (s in post.poll) {
            Log.e(this::class.java.name, s.key)
        }

        btn_confirm_community_selection.setOnClickListener {
            val intent = Intent(requireContext(), this::class.java).apply {
                putExtra("selectedCommunityNames", adapter.getSelectedCommunityNames())
                putExtra("selectedCids", adapter.getSelectedCids())
                putExtra(post::class.java.name, post)
                bundle?.getString("imageUri").let {
                    if (!it.isNullOrBlank()) putExtra("imageUri", it)
                }
            }

            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            fragmentUtils.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun configureToolbar() {
        toolbar_view_availableCommunity_posts.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_view_availableCommunity_posts.setNavigationOnClickListener {
            fragmentUtils.popBackStack(null)
        }
    }

}