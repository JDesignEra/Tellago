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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.models.UserPost
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_journey_posts.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowJourneyPostsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var journey: Journey
    private lateinit var post: Post
    private lateinit var goal: Goal
    private lateinit var adapter: NewPostRecyclerAdapter

    private var bundle: Bundle? = null

    private val userPostArrayList = ArrayList<UserPost>()
    private val newPostArrayList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        journey = Journey()
        post = Post()
        goal = Goal()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) journey = bundle!!.getParcelable(journey::class.java.name) ?: Journey()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

        // This recyclerview adapter should use data in 'Posts' collection on Firestore, but Post feature is not completed yet
        // So, static data will be passed to the adapter for now
        val journeyPostsList = journey.pids

        Log.d("journeyPostsList", journeyPostsList.toString())


        if (journeyPostsList.isNullOrEmpty()) {
            // Query if journeyPostsList is NullOrEmpty
            val query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo(
                "uid",
                user?.uid
            )

            Log.d("document with uid", query.toString())

            adapter = NewPostRecyclerAdapter(
                FirestoreRecyclerOptions.Builder<Post>()
                    .setQuery(query, Post::class.java)
                    .build()
            )
        } else {
            // Query if journeyPostsList is populated
            val query = FirebaseFirestore.getInstance().collection("posts").whereIn(
                FieldPath.documentId(),
                journeyPostsList
            )

            Log.d("document with jPosts", query.toString())

            adapter = NewPostRecyclerAdapter(
                FirestoreRecyclerOptions.Builder<Post>()
                    .setQuery(query, Post::class.java)
                    .build()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_journey_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        configureToolbar()

        recycler_view_show_journey_posts_fragment.layoutManager =
            LinearLayoutManager(requireContext())
        recycler_view_show_journey_posts_fragment.adapter = adapter

        val journeyPostsList2 = journey.pids
        if (journeyPostsList2.isNullOrEmpty()) {
            // Query if journeyPostsList is NullOrEmpty
            journeyPostsList2.add("-100")
            val query2 = FirebaseFirestore.getInstance().collection("posts").whereIn(
                FieldPath.documentId(),
                journeyPostsList2
            )
            query2.get().addOnSuccessListener { it ->

                // prompt user to assign posts to journey if recycler view is empty
                if (it.size() == 0) {
                    tv_show_journey_posts_empty.visibility = View.VISIBLE
                    recycler_view_show_journey_posts_fragment.visibility = View.GONE
                }

            }
        }


        // Same functionality for both constraint_layout_show_journey_posts_add_post and btn_edit_journey_posts
        // on click listeners
        constraint_layout_show_journey_posts_add_post.setOnClickListener {
            val createGoalFragment3 = CreateGoalFragment_3()
            createGoalFragment3.arguments = Bundle().apply {
                putBoolean(ShowJourneyPostsFragment::class.java.name, true)
                putStringArrayList("pids", journey.pids)
                putString("journeyTitle", journey.title)
                putString("jid", journey.jid)
            }

            fragmentUtils.replace(createGoalFragment3, setTargetFragment = this, requestCode = 0)
        }

        // TODO: Use a separate fragment instead in the future.
        btn_edit_journey_posts.setOnClickListener {
            val editJourneyFragment = EditJourneyFragment()
            editJourneyFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
                putParcelable(journey::class.java.name, journey)
            }

            fragmentUtils.replace(editJourneyFragment)
        }
    }

    override fun onStart() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStop()
        adapter.stopListening()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Journey>(Journey::class.java.name).let {
                journey = it!!
                Log.d("updated Journey", journey.toString())

                val journeyPostsList = journey.pids
                val query = FirebaseFirestore.getInstance().collection("posts").whereIn(
                    FieldPath.documentId(),
                    journeyPostsList
                )

                adapter = NewPostRecyclerAdapter(
                    FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post::class.java)
                        .build()
                )

            }
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