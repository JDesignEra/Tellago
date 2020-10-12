package com.tellago.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import java.text.SimpleDateFormat
import java.util.*

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        if (goal.completed) btn_CompleteGoal.isEnabled = false

        tv_title.text = goal.title
        tv_categories.text = goal.categories.toString()
        tv_targetAmt.text = String.format("$%.2f", goal.targetAmt)
        tv_currentAmt.text = String.format("$%.2f", goal.currentAmt)


        // Displaying deadline as DateTime rather than TimeStamp for user viewing
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        tv_deadline.text = dateFormatter.format(goal.deadline).toString()
        tv_lastReminder.text = goal.lastReminder.toString()
        tv_reminderMonthsFreq.text = goal.reminderMonthsFreq.toString()

        // Displaying createDate as DateTime rather than TimeStamp for user viewing
        tv_createDate.text = dateFormatter.format(goal.createDate).toString()

        btn_Journey_View.setOnClickListener {
            val showJourneysFragment = ShowJourneysFragment()

            showJourneysFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }
            fragmentUtils.replace(showJourneysFragment)
        }

        btn_Bucket_List_View.setOnClickListener {
            val showBucketListItemsTabFragment = ShowBucketListItemsTabsFragment()

            showBucketListItemsTabFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }
            fragmentUtils.replace(showBucketListItemsTabFragment)
        }


        btn_EditGoalDetails.setOnClickListener {
            val editGoalDetailsFragment = EditGoalDetailsFragment()

            editGoalDetailsFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(editGoalDetailsFragment)
        }

        btn_CompleteGoal.setOnClickListener {
            val bucketFilter = goal.bucketList.toList().filter { !(it["completed"] as Boolean) }
            val currentAmt = tv_currentAmt.text.toString().substring(1).toDouble()
            val targetAmt = tv_targetAmt.text.toString().substring(1).toDouble()

            if (currentAmt < targetAmt) {
                toast.error("Current Amount needs to be more then or equals to Targeted Amount")
            } else if (!bucketFilter.isNullOrEmpty()) {
                toast.error("Bucket List contains in progress item(s)")
            } else {
                goal.completed = true
                goal.updateCompleteByGid {
                    if (it != null) toast.success("Goal completed successfully")
                    else toast.error("Please try again, there was an issue completing the goal")
                }
            }
        }

        // Function to update the progress bar and text views for Bucket List & Target Amount
        updateProgressBar()

    }

    private fun updateProgressBar() {
        val progressAmtPercentFloat = ((goal.currentAmt / goal.targetAmt) * 100).toFloat()
        val totalProgress: Float

        tv_progressAmt.text =
            String.format("You have saved %.1f %% of the target amount!", progressAmtPercentFloat)

        Log.d("bucketList count", "${goal.bucketList.count()}")
        if (goal.bucketList.count() != 0) {
            // include bucket list for progress calculation

            linear_layout_progressBucketList.visibility = View.VISIBLE

            var blItem_InProgress = 0
            var blItem_Completed = 0

            for (item in 0 until goal.bucketList.count()) {
                if (goal.bucketList[item].containsValue(true)) blItem_Completed += 1
                else blItem_InProgress += 1
            }

            // bucketListProgress is 50% of totalProgress
            val bucketListProgress =
                ((blItem_Completed * 50 / goal.bucketList.count()).toFloat())

            // amountSavedProgress is 50% of totalProgress
            val amountSavedProgress = progressAmtPercentFloat / 2
            // adding bucketListProgress to amountSavedProgress to obtain new totalProgress
            totalProgress = bucketListProgress + amountSavedProgress
            tv_progressBucketList.text =
                "${blItem_Completed} of ${goal.bucketList.count()} List Items Completed"

        } else {
            // do not include bucket list for progress calculation
            // amountSavedProgress is 100% of totalProgress
            totalProgress = progressAmtPercentFloat

        }

        Handler().post {
            progress_bar_progressAmt.progress = totalProgress.toInt()
        }
        tv_progress_bar_display.text = String.format("%.2f %%", totalProgress)
    }

    private fun configureToolbar() {
        toolbar_view_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_view_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}