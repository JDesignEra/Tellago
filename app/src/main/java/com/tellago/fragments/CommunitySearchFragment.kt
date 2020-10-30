package com.tellago.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.android.synthetic.main.fragment_community_search.*


class CommunitySearchFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_community_search, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make search bar focused as soon as this fragment is brought up
        search_bar_communitySearch.isIconified = false
//        search_bar_communitySearch.isFocusedByDefault = true

        // if search bar is in focus, then show 'SEARCH' text (adjust layout weights)
        search_bar_communitySearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            // hasFocus means searchView is selected
            if (hasFocus)
            {

//                displaySearchText()


            }
            else
            {

//                hideSearchText()
                fragmentUtils.replace(
                    CommunityFragment(),
                    null,
                    enter = R.anim.fragment_slide_right_enter_slow,
                    exit = R.anim.fragment_slide_right_exit_slow
                )

            }

        }

        tv_communitySearch_search.setOnClickListener {
            // Launch search when this button is pressed
            text_view_communitySearch.visibility = View.VISIBLE
            text_view_communitySearch.text = search_bar_communitySearch.query.toString()

        }

    }
}