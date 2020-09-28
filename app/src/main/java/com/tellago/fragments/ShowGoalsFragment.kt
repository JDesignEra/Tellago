package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.TopSpacingItemDecoration
import com.tellago.activities.GoalsActivity
import com.tellago.adapters.ShowGoalsRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_goals.*


class ShowGoalsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var adapter: FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>

//    private val createGoalFragment: Fragment = CreateGoalFragment_1()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseFirestore = FirebaseFirestore.getInstance()
        val query: Query = firebaseFirestore.collection("goals")


        // RecyclerOptions
        val options: FirestoreRecyclerOptions<Goal> = FirestoreRecyclerOptions.Builder<Goal>()
            .setQuery(query, Goal::class.java)
            .build()

        //adapter = FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>(options)
        //adapter = ShowGoalsRecyclerAdapter(options)

        //recycler_view_show_goals_fragment.adapter = adapter


        recycler_view_show_goals_fragment.apply {
            layoutManager = LinearLayoutManager(activity?.application?.baseContext)

            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)

            Log.d("Adapter applied to RV", "FIRED")
            adapter = ShowGoalsRecyclerAdapter(options)
            (adapter as ShowGoalsRecyclerAdapter).startListening()
        }

        Log.d("Adapter assigned", "FIRED")

        fab_add_goal.setOnClickListener {
            var nextActivity: Intent = Intent(activity, GoalsActivity::class.java)
            startActivity(nextActivity)
            activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }

    }

//    override fun onStart() {
//        super.onStart()
//        adapter.startListening()
//    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}