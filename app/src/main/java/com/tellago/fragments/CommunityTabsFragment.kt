package com.tellago.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tellago.R
import com.tellago.adapters.ViewPagerBucketListItemsFragmentStateAdapter
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Communities
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_community_tabs.*


class CommunityTabsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    lateinit var toast: CustomToast
    private var communityID_received: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.display_community_fragment_container
        )
        toast = CustomToast(requireContext())
        communityID_received =  requireActivity().intent.getStringExtra("communityID")
        Log.d("CID is: ", communityID_received)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                it.displayImageByCid(context = requireContext(), imageView = iv_toolbar_community_tab)

                updateJoinBtn(it.uids.containsKey(user?.uid))
            }
        }

        if (user?.isAnonymous!!) {
            linear_layout_join_community_search_1.setBackgroundResource(R.drawable.searchbar_background)
            join_tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextDarkGray))
            linear_layout_join_community_search_1.setOnClickListener {
                toast.warning("Please sign in or register to join the community")
            }
        }
        else {
            linear_layout_join_community_search_1.setOnClickListener {
                user?.uid?.let { uid ->
                    Communities(cid = communityID_received).followByCid(uid) {
                        if (it != null) {
                            toast.success("Community ${if (it.uids.containsKey(user?.uid!!)) "join" else "leave"} successfully")
                            updateJoinBtn(it.uids.containsKey(user?.uid!!))
                        }
                        else {
                            toast.success("Please try again")
                        }
                    }
                }
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
        viewPager2.adapter = ViewPagerBucketListItemsFragmentStateAdapter(this.requireActivity(), fragmentList)
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

    private fun updateJoinBtn(inCommunity: Boolean) {
        if (inCommunity) {
            linear_layout_join_community_search_1.setBackgroundResource(R.drawable.header_white_background_rectangle_8dp)
            join_tv.text = "LEAVE"
            join_tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
        else {
            linear_layout_join_community_search_1.setBackgroundResource(R.drawable.header_primary_background_rectangle_8dp)
            join_tv.text = "JOIN"
            join_tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhiteBackground))
        }
    }
}