package com.tellago.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.tellago.MainActivity
import com.tellago.R
import com.tellago.models.Auth
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Auth.user != null && !Auth.user!!.isAnonymous) {
            // User is signed in
            greeting.text = "Welcome to tellsquare, %s".format(Auth.user!!.displayName)
        } else {
            // No user is signed in
            val greetingText: String = "Welcome to tellsquare, Guest"
            greeting.text = greetingText
        }

        configureToolbar()

    }


    private fun configureToolbar() {
        toolbar_Home.setNavigationOnClickListener {
            // Try to open navDrawer here
            Log.d("test", "Opening nav drawer from HOME fragment")
            val drawerLayout : DrawerLayout = activity?.drawer_layout ?: DrawerLayout(this.requireContext())
            drawerLayout.openDrawer(GravityCompat.START)

            // Set up toggle to display hamburger icon with animation
            val drawerToggle: ActionBarDrawerToggle = setupDrawerToggle()
            drawerToggle.syncState()

            // Tie DrawerLayout events to ActionBarToggle
            drawerLayout.addDrawerListener(drawerToggle)

        }
    }

    private fun setupDrawerToggle() : ActionBarDrawerToggle {
        val drawerLayout : DrawerLayout = activity?.drawer_layout ?: DrawerLayout(this.requireContext())
        return ActionBarDrawerToggle(this.requireActivity(), drawerLayout, toolbar_Home, R.string.drawer_open, R.string.drawer_close)
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