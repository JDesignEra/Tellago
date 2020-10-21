package com.tellago.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ShowGoalDetailsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    private var bundle: Bundle? = null
    private var goal = Goal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        toast = CustomToast(requireContext())

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        val heroImagesId = arrayOf(
            R.drawable.job_invisi_bg,
            R.drawable.job_invisi_bg,
            R.drawable.job_invisi_bg
        )

        linearLayout_heroHeader_indicators.removeAllViews()

        for (i in heroImagesId.indices) {
            val indicatorsImageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (30 * resources.displayMetrics.density).roundToInt(),
                    (5 * resources.displayMetrics.density).roundToInt()
                ).apply {
                    marginStart = (3 * resources.displayMetrics.density).roundToInt()
                    marginEnd = (3 * resources.displayMetrics.density).roundToInt()
                }

                scaleType = ImageView.ScaleType.FIT_XY

                if (i == 0) setImageResource(R.drawable.progress_rectangle_selected)
                else setImageResource(R.drawable.progress_rectangle_unselected)
            }

            linearLayout_heroHeader_indicators.addView(indicatorsImageView)
        }

        heroHeader_carouselView.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    for ((i, v) in linearLayout_heroHeader_indicators.children.withIndex()) {
                        val indicatorImageView = v as ImageView

                        if (i == position) indicatorImageView.setImageResource(R.drawable.progress_rectangle_selected)
                        else indicatorImageView.setImageResource(R.drawable.progress_rectangle_unselected)
                    }
                }
            })

            setImageListener { position, imageView ->
                imageView.apply {
                    setImageResource(heroImagesId[position])
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }

            pageCount = heroImagesId.size
        }

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val monetaryPercent = (goal.currentAmt / goal.targetAmt * 100).roundToInt()
        val bucketListPercent = if (goal.bucketList.size > 0) {
            (goal.bucketList.filter { it["completed"] as Boolean }.toMutableList().size.toDouble() / goal.bucketList.size.toDouble() * 100).roundToInt()
        }
        else 100

        val overallPercent = if (goal.bucketList.size < 1) {
            monetaryPercent
        }
        else (monetaryPercent + bucketListPercent) / 2

        tv_overallProgress.text = "$overallPercent%"
        tv_title.text = goal.title
        tv_deadline.text = dateFormatter.format(goal.deadline)

        Handler().post {
            progressIndicator_overallProgress.progress = overallPercent
        }

        val categoryStrToBtn = mapOf<String, Int>(
            "career" to btn_career.id,
            "family" to btn_family.id,
            "leisure" to btn_leisure.id
        )

        goal.categories.forEach {
            categoryStrToBtn[it]?.let { id -> categories_toggleGrp.check(id) }
        }

        if (goal.completed) {
            btn_CompleteGoal.isEnabled = false
            btn_CompleteGoal.text = "Completed"
        }

        btn_Journey_View.setOnClickListener {
            val showJourneysFragment = ShowJourneysFragment()

            showJourneysFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }
            fragmentUtils.replace(
                showJourneysFragment,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit)
        }

        btn_Bucket_List_View.setOnClickListener {
            val showBucketListItemsTabFragment = ShowBucketListItemsTabsFragment()

            showBucketListItemsTabFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }
            fragmentUtils.replace(
                showBucketListItemsTabFragment,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit)
        }


        constraint_layout_edit_goal_details.setOnClickListener {
            val editGoalDetailsFragment = EditGoalDetailsFragment()

            editGoalDetailsFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(
                editGoalDetailsFragment,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit
            )
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = goal.gid).deleteByGid()
            fragmentUtils.popBackStack()
        }

        btn_CompleteGoal.setOnClickListener {
            val bucketFilter = goal.bucketList.toList().filter { !(it["completed"] as Boolean) }

            if (goal.currentAmt < goal.targetAmt) {
                toast.error("Current Amount needs to be more than or equals to Targeted Amount")
            }
            else if (!bucketFilter.isNullOrEmpty()) {
                toast.error("Bucket List contains in progress item(s)")
            }
            else {
                goal.completed = true
                goal.updateCompleteByGid {
                    if (it != null) {
                        btn_CompleteGoal.isEnabled = false
                        toast.success("Goal completed successfully")
                    }
                    else toast.error("Please try again, there was an issue completing the goal")
                }
            }
        }
    }

    private fun configureToolbar() {
        toolbar_view_goal_details.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_view_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}