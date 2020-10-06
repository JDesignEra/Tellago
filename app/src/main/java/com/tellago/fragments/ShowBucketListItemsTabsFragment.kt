package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tellago.R
import com.tellago.adapters.ViewPagerBucketListItemsFragmentStateAdapter
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_bucket_list_items_tab.*


class ShowBucketListItemsTabsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal: Goal

    private var bundle: Bundle? = null
    private val createBucketListItemFragment: Fragment = CreateBucketListItemFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bucket_list_items_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()
        setUpTabs()

        fab_add_bucketListItem.setOnClickListener {
            createBucketListItemFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            ).replace(createBucketListItemFragment, setTargetFragment = this)
        }
    }

    private fun setUpTabs() {
        val viewPager2 = view_pager_ShowBucketListItemsTabsFragment
        val ongoingItemsFragment = ShowBucketListItemsOngoingFragment()
        val completedItemsFragment = ShowBucketListItemsCompletedFragment()

        ongoingItemsFragment.arguments = Bundle().apply {
            putParcelable(goal::class.java.name, goal)
        }

        completedItemsFragment.arguments = Bundle().apply {
            putParcelable(goal::class.java.name, goal)
        }

        val fragmentList = arrayListOf(
            ongoingItemsFragment,
            completedItemsFragment
        )

        // set Orientation in ViewPager2
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.adapter = ViewPagerBucketListItemsFragmentStateAdapter(this.requireActivity(), fragmentList)


        val tabLayout = tab_layout_ShowBucketListItemsTabsFragment


        TabLayoutMediator(tabLayout, viewPager2) { tab, position -> // Styling each tab here
            when (position) {
                0 -> tab.text = "Ongoing"
                1 -> tab.text = "Completed"
            }
        }.attach()
    }

    private fun configureToolbar() {
        toolbar_tabLayout_bucketListItems.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }
}