package com.tellago.fragments

import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.TopSpacingItemDecoration
import com.tellago.adapters.ShowGoalsRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_goals.*
import kotlinx.android.synthetic.main.layout_goal_list_item.view.*


class ShowGoalsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var adapter: FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>

    private val createGoalFragment: Fragment = CreateGoalFragment()


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


        // Query
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
            fragmentUtils.addFragmentToFragment(createGoalFragment)
        }


    }
//
//    override fun onStart() {
//        super.onStart()
//        adapter.startListening()
//    }


    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShowGoalsFragment.
         */

    }
}