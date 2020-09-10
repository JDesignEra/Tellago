package com.tellago.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.MainActivity
import com.tellago.R
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

        if (MainActivity.user != null && !MainActivity.user!!.isAnonymous) {
            // User is signed in
            greeting.text = "Welcome to Tellago, %s".format(MainActivity.user!!.displayName)
        }
        else {
            // No user is signed in
            val greetingText: String = "Welcome to Tellago, Guest"
            greeting.text = greetingText
        }
    }
}