package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tellago.R
import com.tellago.adapters.ViewPagerBucketListItemsFragmentStateAdapter
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_bucket_list_items_tab.*

class ShowBucketListItemsTabsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
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

        tv_goal_title.text = goal.title

        if (goal.completed) {
            tv_instruction_footer.visibility = View.GONE
            fab_add_bucketListItem.visibility = View.GONE
        }

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
        val fragmentList = arrayListOf<Fragment>()
        if (!goal.completed) fragmentList.add(ShowBucketListItemsOngoingFragment())
        fragmentList.add(ShowBucketListItemsCompletedFragment())

        // set Orientation in ViewPager2
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.adapter = ViewPagerBucketListItemsFragmentStateAdapter(this.requireActivity(), fragmentList)
        viewPager2.isUserInputEnabled = false

        val tabLayout = tab_layout_ShowBucketListItemsTabsFragment
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> tv_instruction_footer.text = fromHtml(getString(R.string.bucket_list_tabs_footer_instruction, "complete"), FROM_HTML_MODE_COMPACT)
                    1 -> tv_instruction_footer.text = fromHtml(getString(R.string.bucket_list_tabs_footer_instruction, "in progress"), FROM_HTML_MODE_COMPACT)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    if (goal.completed) {
                        tab.text = "Completed"
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_circle_48)
                    }
                    else {
                        tab.text = "In Progress"
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_hourglass_top_white_48dp)
                    }
                }
                1 -> {
                    tab.text = "Completed"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_circle_48)
                }
            }
        }.attach()
    }

    private fun configureToolbar() {
        toolbar_tabLayout_bucketListItems.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

    companion object {
        lateinit var goal: Goal
    }
}