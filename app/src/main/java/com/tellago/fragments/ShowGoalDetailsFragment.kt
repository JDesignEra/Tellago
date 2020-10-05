package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import java.text.SimpleDateFormat
import java.util.*

class ShowGoalDetailsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils

    private var bundle: Bundle? = null
    private var goal = Goal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

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

        tv_title.text = goal.title
        tv_categories.text = goal.categories.toString()
        tv_targetAmt.text = String.format("$%.2f", goal.targetAmt)
        tv_currentAmt.text = String.format("$%.2f", goal.currentAmt)
        tv_bucketList.text = goal.bucketList.toString()

        // Displaying deadline as DateTime rather than TimeStamp for user viewing
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        tv_deadline.text = dateFormatter.format(goal.deadline).toString()
        tv_lastReminder.text = goal.lastReminder.toString()
        tv_reminderMonthsFreq.text = goal.reminderMonthsFreq.toString()

        // Displaying createDate as DateTime rather than TimeStamp for user viewing
        tv_createDate.text = dateFormatter.format(goal.createDate).toString()

        btn_Bucket_List_View.setOnClickListener {
            Log.d("bucket list test", "FIRED")
        }

        btn_EditGoalDetails.setOnClickListener {
            val editGoalDetailsFragment = EditGoalDetailsFragment()

            editGoalDetailsFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(editGoalDetailsFragment)
        }

        btn_CompleteGoal.setOnClickListener {
            Log.d("Complete Goal", "FIRED")
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