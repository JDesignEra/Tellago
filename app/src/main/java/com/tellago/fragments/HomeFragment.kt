package com.tellago.fragments

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
import com.tellago.adapters.HomePostRecyclerAdapter
import com.tellago.models.Auth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_guest_to_sign_in_up.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homePostAdapter: HomePostRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        addDataSet()

//        if (Auth.user != null && !Auth.user!!.isAnonymous) {
//            // User is signed in
//            greeting.text = "Welcome to tellsquare, %s".format(Auth.user!!.displayName)
//        } else {
//            // No user is signed in
//            val greetingText: String = "Welcome to tellsquare"
//            greeting.text = greetingText
//        }

        configureToolbar()
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


    private fun addDataSet() {
        // data created in DataSource data class should be retrieved from Firebase Storage & Cloud Firestore
        val data = DataSource.createDataSetForHome()
        homePostAdapter.submitList(data)
        Log.d("addDataSet()", "FIRED")
    }

    private fun initRecyclerView() {
        Log.d("initRecyclerView()", "FIRED")
        // recyclerview from fragment_home.xml
        recycler_view_home_fragment.apply {

            // Step 1: set layoutManager for recyclerview
            Log.d("LayoutManager Home", "FIRED")
            layoutManager = LinearLayoutManager(activity?.application?.baseContext)

            // Step 2: Adding padding decoration for spacing between viewholders (defined in TopSpacingItemDecoration.kt)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)

            // Step 3: Initialise the lateinit variable homePostAdapter
            homePostAdapter = HomePostRecyclerAdapter()
            adapter = homePostAdapter
        }
    }


}