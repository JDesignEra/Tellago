package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.R
import com.tellago.activities.GoalsActivity
import com.tellago.adapters.ShowGoalsRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_goals.*

class ShowGoalsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private var adapter: ShowGoalsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

        adapter = ShowGoalsRecyclerAdapter(Goal(uid = user?.uid).getRecyclerOptionsByUid())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view_show_goals_fragment.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_show_goals_fragment.adapter = adapter

        fab_add_goal.setOnClickListener {
            startActivity(Intent(activity, GoalsActivity::class.java))
            activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }
    }

    override fun onStart() {
        super.onStart()

        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) adapter!!.stopListening()
    }
}