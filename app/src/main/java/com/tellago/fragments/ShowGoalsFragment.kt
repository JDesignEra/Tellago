package com.tellago.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.R
import com.tellago.adapters.ShowGoalsRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_show_goals.*

class ShowGoalsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private var adapter: ShowGoalsRecyclerAdapter? = null
    private val createGoalFragment1: Fragment = CreateGoalFragment_1()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
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

        configureToolbar()

        recycler_view_show_goals_fragment.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_show_goals_fragment.adapter = adapter

        fab_add_goal.setOnClickListener {
//            startActivity(Intent(activity, GoalsActivity::class.java))
//            activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            fragmentUtils.replace(createGoalFragment1)
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

    private fun configureToolbar() {
        toolbar_show_goals.setNavigationOnClickListener {
            // Finish GoalsActivity to return to MainActivity
            activity?.finish()

        }
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        val drawerLayout: DrawerLayout = activity?.drawer_layout ?: DrawerLayout(this.requireContext())

        return ActionBarDrawerToggle(
            this.requireActivity(),
            drawerLayout,
            toolbar_show_goals,
            R.string.drawer_open,
            R.string.drawer_close
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val drawerToggle: ActionBarDrawerToggle = setupDrawerToggle()

        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawerToggle: ActionBarDrawerToggle = setupDrawerToggle()

        return if (drawerToggle.onOptionsItemSelected(item))
            true
        else
            super.onOptionsItemSelected(item)
    }
}