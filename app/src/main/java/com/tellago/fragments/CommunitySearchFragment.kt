package com.tellago.fragments

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.activities.DisplayCommunityActivity
import com.tellago.adapters.CommunitySearchAdapter
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.models.Communities
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_community_search.*

class CommunitySearchFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast
    private var adapter: CommunitySearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

        toast = CustomToast(requireContext())
        adapter = CommunitySearchAdapter(
            FirestoreRecyclerOptions.Builder<Communities>().setQuery(
                Communities.collection,
                Communities::class.java
            ).build()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_community_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communities_recyclerView.layoutManager = LinearLayoutManager(requireContext())
        communities_recyclerView.adapter = adapter

        search_bar_communitySearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotBlank()) adapter?.search(newText)
                else adapter?.resetSearch()
                return true
            }
        })

        // Make search bar focused as soon as this fragment is brought up
//        search_bar_communitySearch.isIconified = false
//        search_bar_communitySearch.isFocusedByDefault = true

        // if search bar is in focus, then show 'SEARCH' text (adjust layout weights)

//        search_bar_communitySearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
//            // hasFocus means searchView is selected
//            if (hasFocus) {
////                displaySearchText()
//
//            } else {
////                hideSearchText()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    circleReveal(toolbar_communitySearch, -1, true, false)
//                    text_view_communitySearch.visibility = View.GONE
//
//                } else {
//
//                    toolbar_communitySearch.visibility = View.GONE
//                }
//            }
//        }

        iv_fragment_community_search_back.setOnClickListener {
            it.hideKeyboard()
            fragmentUtils.popBackStack()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                circleReveal(toolbar_communitySearch, -1, true, false)
//                text_view_communitySearch.visibility = View.GONE
//            } else {
//                toolbar_communitySearch.visibility = View.GONE
//            }
        }

        // Testing query to populate card
//        val Communities = Communities()
//        FirestoreRecyclerOptions.Builder<Communities>()

//        Communities.getAll {
//            if (it != null) {
//                Log.d("size of collection: ", it.size.toString())
//
//                // Assign first card view with values
//                tv_communitySearch_cardview_3_title.text = it.get(0).name
//                val countTotalMembers = it.get(0).uids.count()
//                tv_communitySearch_cardview_3_memberCount.text =
//                    String.format("$countTotalMembers members")
//                val subStringOfSummary = it.get(0).summary?.subSequence(0, 50)
//
//                if (subStringOfSummary != null) {
//
//                    // Check the length of 'summary' value for this community
//                    if (it.get(0).summary!!.length > 50) {
//                        tv_communitySearch_cardview_3_summary.text = String.format("$subStringOfSummary ......")
//                    }
//                    else
//                    {
//                        tv_communitySearch_cardview_3_summary.text = subStringOfSummary
//                    }
//                }
//
//            }
//        }

        //        cardview_career_communities_1.setOnClickListener {
//
//
//            // Display selected Community in new Activity
//            val intent = Intent(requireContext(), DisplayCommunityActivity::class.java)
//            // use intent.putExtra to pass the community ID to be displayed
////            intent.putExtra("communityID", "communityID")
//            startActivity(intent)
//
//        }

//        cardview_career_communities_3.setOnClickListener {
//            // Display selected Community in new Activity
//            val intent = Intent(requireContext(), DisplayCommunityActivity::class.java)
//            // use intent.putExtra to pass the community ID to be displayed
//            Communities.getAll {
//                if (it != null) {
//                    val communityID_community_3 = it.get(0).cid
//
//                    intent.putExtra("communityID", "$communityID_community_3")
//                    startActivity(intent)
//                }
//            }
//        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    fun circleReveal(
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
                fragmentUtils.popBackStack()
            }, 600
        )
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}