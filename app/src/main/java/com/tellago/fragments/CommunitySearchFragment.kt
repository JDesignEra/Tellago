package com.tellago.fragments

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import com.tellago.R
import com.tellago.activities.CallToActionActivity
import com.tellago.activities.DisplayCommunityActivity
import com.tellago.utilities.FragmentUtils
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
            if (hasFocus) {

//                displaySearchText()


            } else {

//                hideSearchText()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(toolbar_communitySearch, -1, true, false)
                    text_view_communitySearch.visibility = View.GONE

                } else {

                    toolbar_communitySearch.visibility = View.GONE
                }

            }

        }

        iv_fragment_community_search_back.setOnClickListener {
            it.hideKeyboard()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleReveal(toolbar_communitySearch, -1, true, false)
                text_view_communitySearch.visibility = View.GONE

            } else {

                toolbar_communitySearch.visibility = View.GONE
            }

        }


        tv_communitySearch_search.setOnClickListener {
            // Launch search when this button is pressed
            text_view_communitySearch.visibility = View.VISIBLE
            text_view_communitySearch.text = search_bar_communitySearch.query.toString()

        }

        cardview_career_communities_1.setOnClickListener {


            // Display selected Community in new Activity
            val intent = Intent(requireContext(), DisplayCommunityActivity::class.java)
            // use intent.putExtra to pass the community ID to be displayed
//            intent.putExtra("communityID", "communityID")
            startActivity(intent)

        }

    }

    public fun circleReveal(
        viewID: View,
        posFromLeft: Int,
        containsOverflow: Boolean,
        isShow: Boolean
    ) {
        val myView = viewID
        var width = myView.width

        if (posFromLeft > 0)
            width += (posFromLeft * 48) + (48 / 2)
        else
            width -= (posFromLeft * 48) - (48 / 2)

        if (containsOverflow)
            width -= 36

        val cx: Int = width
        val cy: Int = myView.height / 2

        val anim: Animator

        if (isShow)
            anim =
                    // animate from left to right, then pop as circle
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0.0F, 0.0F)
        else
            anim =
                    // pop from circle, then animation from right to left
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0.0F)

        anim.duration = 700

        // Change View to Invisible after animation has completed
        anim.addListener {
            if (!isShow) {

                myView.visibility = View.INVISIBLE
            }
        }

        // Change View to Visible to start the animation
        if (isShow) myView.visibility = View.VISIBLE

        // Start the animiation
        anim.start()

        Handler().postDelayed(
            {
                fragmentUtils.replace(
                    CommunityFragment(),
                    null,
                    animate = false
                )
            }, 600

        )

    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}