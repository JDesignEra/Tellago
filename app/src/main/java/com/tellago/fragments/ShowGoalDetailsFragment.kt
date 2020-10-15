package com.tellago.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
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
        tv_createDate.text = dateFormatter.format(goal.createDate)
        tv_deadline.text = dateFormatter.format(goal.deadline)
        if (goal.reminderMonthsFreq > 0) tv_reminderMonthsFreq.text = "Every ${goal.reminderMonthsFreq} months"

        if (monetaryPercent < 80) tv_monetary_progress.setTextColor(getColor(requireContext(), R.color.colorTextBlack))
        tv_monetary_progress.text = "$%.2f / $%.2f".format(goal.currentAmt, goal.targetAmt)

        if (bucketListPercent < 60) tv_bucketList_progress.setTextColor(getColor(requireContext(), R.color.colorTextBlack))
        tv_bucketList_progress.text = "${goal.bucketList.filter { it["completed"] as Boolean }.size} / ${goal.bucketList.size}"

        Handler().post {
            progressIndicator_overallProgress.progress = overallPercent
            progressIndicator_monetary.progress = monetaryPercent
            progressIndicator_bucketList.progress = bucketListPercent
        }

        if (goal.categories.size > 0) {
            val categoryToDrawableId = mapOf(
                "career" to R.drawable.ic_baseline_work_24,
                "family" to R.drawable.ic_family_restroom_24,
                "leisure" to R.drawable.ic_baseline_local_airport_24
            )

            for ((idx, category) in goal.categories.withIndex()) {
                val chipHolder = Chip(chipGroup_category.context).apply {
                    iconEndPadding = chip_category_none.iconStartPadding
                    iconStartPadding = chip_category_none.iconStartPadding
                    isCheckedIconVisible = chip_category_none.isChipIconVisible
                    isCheckable = chip_category_none.isCheckable
                    rippleColor = null
                    setEnsureMinTouchTargetSize(false)
                }

                chipHolder.text = category
                chipHolder.chipIcon = categoryToDrawableId[category]?.let { getDrawable(requireContext(), it) }
                chipGroup_category.addView(chipHolder)
            }

            chipGroup_category.removeView(chip_category_none)
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


        btn_EditGoalDetails.setOnClickListener {
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

        btn_CompleteGoal.setOnClickListener {
            val bucketFilter = goal.bucketList.toList().filter { !(it["completed"] as Boolean) }

            if (goal.currentAmt < goal.targetAmt) {
                toast.error("Current Amount needs to be more then or equals to Targeted Amount")
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
        toolbar_view_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_view_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}