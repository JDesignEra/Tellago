package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
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

        initElementStates(goal)
        
        setFragmentResultListener(this::class.java.name) { _, bundle ->
            bundle.getParcelable<Goal>(goal::class.java.name)?.let { initElementStates(it) }
        }

        btn_CompleteGoal.setOnClickListener {
            Log.d("Complete Goal", "FIRED")
        }
    }

    private fun initElementStates(goalParam: Goal) {
        tv_title.text = goalParam.title
        tv_categories.text = goalParam.categories.toString()
        tv_targetAmt.text = goalParam.targetAmt.toString()
        tv_currentAmt.text = goalParam.currentAmt.toString()
        tv_bucketList.text = goalParam.bucketList.toString()

        // Displaying deadline as DateTime rather than TimeStamp for user viewing
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("en", "SG"))
        dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")

        tv_deadline.text = dateFormatter.format(goalParam.deadline).toString()
        tv_lastReminder.text = goalParam.lastReminder.toString()
        tv_reminderMonthsFreq.text = goalParam.reminderMonthsFreq.toString()

        // Displaying createDate as DateTime rather than TimeStamp for user viewing
        tv_createDate.text = dateFormatter.format(goalParam.createDate).toString()

        btn_Bucket_List_View.setOnClickListener {
            Log.d("bucket list test", "FIRED")
        }

        btn_EditGoalDetails.setOnClickListener {
            val editGoalDetailsFragment = EditGoalDetailsFragment()

            editGoalDetailsFragment.arguments = Bundle().apply {
//                putString("final_date", "default")
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(editGoalDetailsFragment)
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