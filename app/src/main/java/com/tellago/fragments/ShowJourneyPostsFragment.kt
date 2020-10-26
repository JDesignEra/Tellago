package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.tellago.R
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.models.Post.Companion.collection
import com.tellago.models.UserPost
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_journey_posts.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowJourneyPostsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal: Goal
    private lateinit var journey: Journey
    private lateinit var post: Post

    private var adapter: NewPostRecyclerAdapter? = null
    private var bundle: Bundle? = null
    var updatedTitle: String? = null

    private val userPostArrayList = ArrayList<UserPost>()
    private val newPostArrayList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
        journey = Journey()
        post = Post()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!
        if (bundle != null) journey = bundle!!.getParcelable(journey::class.java.name) ?: Journey()

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_journey_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        if (journey.jid != null && journey.pids.isNotEmpty() && journey.title.isNotBlank()) {
            adapter = NewPostRecyclerAdapter(
                FirestoreRecyclerOptions.Builder<Post>()
                    .setQuery(
                        collection.whereIn(FieldPath.documentId(), journey.pids),
                        Post::class.java
                    ).build()
            )

            recycler_view_show_journey_posts_fragment.layoutManager = LinearLayoutManager(requireContext())
            recycler_view_show_journey_posts_fragment.adapter = adapter

            adapter?.startListening()

            text_view_journey_title.text = journey.title
        }
        else if (adapter == null) {
            if (goal.jid.isNotEmpty() && goal.jid[0].isNotBlank() ) {
                Journey(goal.jid[0]).getByJid {
                    journey = it ?: Journey()

                    if (it != null) {
                        adapter = NewPostRecyclerAdapter(
                            FirestoreRecyclerOptions.Builder<Post>()
                                .setQuery(
                                    collection.whereIn(FieldPath.documentId(), it.pids),
                                    Post::class.java
                                ).build()
                        )

                        recycler_view_show_journey_posts_fragment.layoutManager = LinearLayoutManager(requireContext())
                        recycler_view_show_journey_posts_fragment.adapter = adapter

                        adapter?.startListening()

                        text_view_journey_title.text = it.title
                    }
                }
            }
        }
        else {
            recycler_view_show_journey_posts_fragment.layoutManager = LinearLayoutManager(requireContext())
            recycler_view_show_journey_posts_fragment.adapter = adapter
        }

        constraint_layout_show_journey_posts_add_post.setOnClickListener {
            val editJourneyFragment = EditJourneyFragment()
            editJourneyFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
                putParcelable(journey::class.java.name, journey)
            }

            fragmentUtils.replace(
                editJourneyFragment,
                setTargetFragment = this,
                requestCode = 0
            )
        }

        btn_edit_journey_posts.setOnClickListener {
            val editJourneyFragment = EditJourneyFragment()
            editJourneyFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
                putParcelable(journey::class.java.name, journey)
            }

            fragmentUtils.replace(
                editJourneyFragment,
                setTargetFragment = this,
                requestCode = 0
            )
        }
    }

    override fun onStart() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStop()
        adapter?.stopListening()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            journey = data?.getParcelableExtra<Journey>(journey::class.java.name) ?: Journey()
        }
    }

    private fun configureToolbar() {
        // It will not be possible to collapse toolbar & floating action button when scrolling as these elements belong to ShowJourneyPostsFragment
        // not within an Activity which contains a fragment_container for the recycler view

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        text_view_journey_title.text = journey.title
        text_view_journey_creation_date.text = dateFormatter.format(goal.createDate)

        toolbar_view_journey_posts.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}