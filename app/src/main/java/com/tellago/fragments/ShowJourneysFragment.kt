package com.tellago.fragments

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
import com.tellago.adapters.ShowJourneysRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_journeys.*


class ShowJourneysFragment : Fragment() {

    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal: Goal
    private lateinit var journey: Journey

    private var bundle: Bundle? = null

    private var adapter: ShowJourneysRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
        journey = Journey()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )


        // to update query based on unique identifier for each journey (with reference to arrayListJourneyID)
        val arrayListJourneyID = goal.jid
        val query = FirebaseFirestore.getInstance().collection("journeys").whereIn(FieldPath.documentId(), arrayListJourneyID)


        adapter = ShowJourneysRecyclerAdapter(
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
        return inflater.inflate(R.layout.fragment_show_journeys, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        recycler_view_show_journeys_fragment.layoutManager = LinearLayoutManager(
            requireContext())

        recycler_view_show_journeys_fragment.adapter = adapter
        Log.d("recycler view adapter", adapter.toString())

        fab_add_journey.setOnClickListener {
            Log.d("fab Journey create", "FIRED")
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

    private fun configureToolbar() {

        toolbar_show_journeys.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

}