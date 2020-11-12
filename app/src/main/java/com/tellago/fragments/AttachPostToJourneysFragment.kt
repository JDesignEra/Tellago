package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.adapters.ShowAvailableJourneysForPostAttachRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_attach_post_to_journeys.*

class AttachPostToJourneysFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var adapter: ShowAvailableJourneysForPostAttachRecyclerAdapter

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

        adapter = ShowAvailableJourneysForPostAttachRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Journey>()
                .setQuery(
                    Journey.collection.whereEqualTo("uid", user?.uid),
                    Journey::class.java
                ).build()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attach_post_to_journeys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        recycler_view_show_availableJourney_posts_fragment.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_show_availableJourney_posts_fragment.adapter = adapter.apply {
            bundle?.getStringArrayList("selectedJids")?.let {
                setSelectedJids(it)
            }
        }

        btn_confirm_journey_selection.setOnClickListener {
            val intent = Intent(requireContext(), this::class.java).apply {
                putExtra("selectedJourneyTitles", adapter.getSelectedJourneyTitles())
                putExtra("selectedJids", adapter.getSelectedJids())
                putExtra("selectedCommunityNames",bundle?.getStringArrayList("selectedCommunityNames"))
                putExtra("selectedCids", bundle?.getStringArrayList("selectedCids"))
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
        toolbar_view_availableJourney_posts.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_view_availableJourney_posts.setNavigationOnClickListener {
            fragmentUtils.popBackStack(null)
        }
    }
}