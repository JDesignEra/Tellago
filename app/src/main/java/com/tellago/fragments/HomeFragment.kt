package com.tellago.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.DataSource
import com.tellago.R
import com.tellago.TopSpacingItemDecoration
import com.tellago.activities.CreateGoalActivity
import com.tellago.activities.EditProfileActivity
import com.tellago.adapters.ProfilePostRecyclerAdapter
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private val showGoalsFragment : Fragment = ShowGoalsFragment()
//    private val createGoalFragment: Fragment = CreateGoalFragment()
//    private val createGoalPassDataFragment: Fragment = CreateGoalFragmentOne()
//    private val createGoalFragment1: Fragment = CreateGoalFragment_1()


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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        btn_ShowAllGoals.setOnClickListener {
            fragmentUtils.addFragmentToFragment(showGoalsFragment)
        }

        btn_AddGoal.setOnClickListener {
            // Test for inserting new document into Firestore (COMPLETE)
//            fragmentUtils.addFragmentToFragment(createGoalFragment)
            // Test for passing data between fragments
            //fragmentUtils.addFragmentToFragment(createGoalPassDataFragment)
            // Actual implementation (direct to FIRST page of CreateGoal input form in new Activity)
            var nextActivity: Intent = Intent(activity, CreateGoalActivity::class.java)
            startActivity(nextActivity)
            activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            //fragmentUtils.addFragmentToFragment(createGoalFragment1)

        }

    }

    private fun configureToolbar() {
        toolbar_Home.setNavigationOnClickListener {
            // Try to open navDrawer here
            Log.d("test", "Opening nav drawer from HOME fragment")
            val drawerLayout: DrawerLayout =
                activity?.drawer_layout ?: DrawerLayout(this.requireContext())
            drawerLayout.openDrawer(GravityCompat.START)

            // Set up toggle to display hamburger icon with animation
            val drawerToggle: ActionBarDrawerToggle = setupDrawerToggle()
            drawerToggle.syncState()

            // Tie DrawerLayout events to ActionBarToggle
            drawerLayout.addDrawerListener(drawerToggle)
        }
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        val drawerLayout: DrawerLayout =
            activity?.drawer_layout ?: DrawerLayout(this.requireContext())
        return ActionBarDrawerToggle(
            this.requireActivity(),
            drawerLayout,
            toolbar_Home,
            R.string.drawer_open,
            R.string.drawer_close
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val drawerToggle: ActionBarDrawerToggle = setupDrawerToggle()
        // Pass any configuration change to the drawer toggles
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