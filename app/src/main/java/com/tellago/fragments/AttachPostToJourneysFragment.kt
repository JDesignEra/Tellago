package com.tellago.fragments

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
import com.tellago.adapters.ShowAvailableJourneysForPostAttachRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_attach_post_to_journeys.*



class AttachPostToJourneysFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal: Goal
    private lateinit var journey: Journey
    private lateinit var post: Post
    private lateinit var adapter: ShowAvailableJourneysForPostAttachRecyclerAdapter


    private var bundle: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
        journey = Journey()
        post = Post()

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )


        if (this.arguments != null) bundle = requireArguments()
        val availableJourneysArrayList =
            bundle?.getStringArrayList("availableJourneysArrayList") as ArrayList<String>


        val query = FirebaseFirestore.getInstance().collection("journeys").whereIn(
            FieldPath.documentId(), availableJourneysArrayList
        )

        Log.d("Query is: ", "$query")

        adapter = ShowAvailableJourneysForPostAttachRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Journey>()
                .setQuery(query, Journey::class.java)
                .build()
        )



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attach_post_to_journeys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        recycler_view_show_availableJourney_posts_fragment.layoutManager =
            LinearLayoutManager(requireContext())

        recycler_view_show_availableJourney_posts_fragment.adapter = adapter




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


    private fun configureToolbar() {
        toolbar_view_availableJourney_posts.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_view_availableJourney_posts.setNavigationOnClickListener {
            fragmentUtils.popBackStack(null)
        }
    }

}