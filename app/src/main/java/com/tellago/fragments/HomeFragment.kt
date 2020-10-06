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
import com.tellago.R
import com.tellago.activities.GoalsActivity
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils

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
            val intent = Intent(activity, GoalsActivity::class.java)
            intent.putExtra(this::class.java.name, "show")
            startActivity(intent)
        }

        btn_AddGoal.setOnClickListener {
            val intent = Intent(activity, GoalsActivity::class.java)
            intent.putExtra(this::class.java.name, "add")
            startActivity(intent)
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