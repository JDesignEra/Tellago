package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.tellago.R
import kotlinx.android.synthetic.main.fragment_community.*


class CommunityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //configureToolbar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // if search bar is in focus, then show 'SEARCH' text (adjust layout weights)
        search_bar_community.setOnQueryTextFocusChangeListener { _, hasFocus ->
            // hasFocus means searchView is selected
            if (hasFocus)
            {
                // changing layout weights
                displaySearchText()

            }
            else
            {
                // changing layout weights
                hideSearchText()

            }

        }

        search_bar_community.setOnSearchClickListener {
            displaySearchText()
        }
        
    }

    private fun hideSearchText() {
        val param = LinearLayout.LayoutParams(
            0,
            120,
            0.97f
        )
//        val param2 = LinearLayout.LayoutParams(
//            0,
//            120,
//            0.0f
//        )
        search_bar_community.setLayoutParams(param)
//        tv_community_search.layoutParams = param2
    }

    private fun displaySearchText() {
        val param = LinearLayout.LayoutParams(
            0,
            120,
            0.75f
        )
//        val param2 = LinearLayout.LayoutParams(
//            0,
//            140,
//            0.25f
//        )
//        val param3 = LinearLayout.LayoutParams(
//            0,
//            120,
//            0.25f
//        )
        search_bar_community.setLayoutParams(param)
//        tv_community_search.layoutParams = param3
    }
}