package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.R
import com.tellago.adapters.ShowBucketListItemsRecyclerAdapter
import com.tellago.adapters.UserPostRecyclerAdapter
import com.tellago.models.Journey
import com.tellago.models.UserPost
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_bucket_list_items_tab.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.toolbar_view_goal_details
import kotlinx.android.synthetic.main.fragment_show_journey_posts.*
import kotlinx.android.synthetic.main.fragment_show_journeys.*


class ShowJourneyPostsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var journey: Journey

    private var bundle: Bundle? = null
    private var adapter: UserPostRecyclerAdapter? = null
    private val userPostArrayList = ArrayList<UserPost>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        journey = Journey()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) journey = bundle!!.getParcelable(journey::class.java.name)!!


        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )


        // This recyclerview adapter should use data in 'Posts' collection on Firestore, but Post feature is not completed yet
        // So, static data will be passed to the adapter for now
        val journeyPostsList = journey.pids

        if (journeyPostsList != null) {
            for (journeyPost in journeyPostsList) {
                val newUserPostObj = UserPost(
                    title = journeyPost,
                    image = R.drawable.ic_email_round_96.toString(),
                    displayName = "Test Poster",
                    profilePic = R.drawable.ic_android_photo.toString(),
                    duration = "3 days ago",
                    likes = "1288",
                    comments = "417"
                )
                userPostArrayList.add(newUserPostObj)
                Log.d("userPostArrayList", userPostArrayList.size.toString())
            }

//            adapter = UserPostRecyclerAdapter(journey)
            adapter = UserPostRecyclerAdapter(userPostArrayList)
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

        // prompt user to assign posts to journey if recycler view is empty
        if (userPostArrayList.size == 0)
        {
            tv_show_journey_posts_empty.visibility = View.VISIBLE
            recycler_view_show_journey_posts_fragment.visibility = View.GONE
        }


        fab_edit_journey_posts.setOnClickListener {
            Log.d("redirect to EDIT", "FIRED")
        }


    }

//    override fun onStart() {
//        // Adapter which is populated using Firestore data (through query) will require this function
//        super.onStart()
//        adapter?.startListening()
//    }
//
//    override fun onStop() {
//        // Adapter which is populated using Firestore data (through query) will require this function
//        super.onStop()
//        adapter?.stopListening()
//    }

    private fun configureToolbar() {
        // It will not be possible to collapse toolbar & floating action button when scrolling as these elements belong to ShowJourneyPostsFragment
        // not within an Activity which contains a fragment_container for the recycler view

        toolbar_view_journey_posts.title = "Journey: ${journey.title}"
        toolbar_view_journey_posts.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_view_journey_posts.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }

}