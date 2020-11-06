package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tellago.R
import com.tellago.adapters.ViewPagerBucketListItemsFragmentStateAdapter
import com.tellago.models.Communities
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_community_tabs.*


class CommunityTabsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private var communityID_received: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.display_community_fragment_container
        )

        communityID_received =  requireActivity().intent.getStringExtra("communityID")
        Log.d("CID is: ", communityID_received)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()
        setUpTabs()


        //Populate toolbar based on values obtained through Firestore query
        Communities(cid = communityID_received).getByCid {
            if (it != null) {
                community_tabs_toolbar_name.text = it.name
                val countTotalMembers = it.uids.count()
                community_tabs_toolbar_membersCount.text = "$countTotalMembers members"
                community_tabs_toolbar_summary.text = it.summary
            }

        }

    }

    private fun configureToolbar() {
        toolbar_tabLayout_community_fragment.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }


    private fun setUpTabs() {
        val viewPager2 = view_pager_community_search_tabs
        val fragmentList = arrayListOf<Fragment>()
        fragmentList.add(CommunityFeedFragment())
        fragmentList.add(CommunityMembersFragment())


        // set Orientation in ViewPager2
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.adapter =
            ViewPagerBucketListItemsFragmentStateAdapter(this.requireActivity(), fragmentList)
        viewPager2.isUserInputEnabled = true

        val tabLayout = tab_layout_community_fragment


        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "FEED"

                }
                1 -> {
                    tab.text = "MEMBERS"

                }
            }
        }.attach()
    }


}