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
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import java.text.SimpleDateFormat
import java.util.*


class ShowGoalDetailsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        bundle = requireArguments()
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

        Goal(gid = bundle.getString("gid")).getByGid {
            if (it != null) {
                bundle.putParcelable("Goal", it)

                // Assign to relevant edit text elements below
                tv_title.text = it.title
                tv_categories.text = it.category.toString()
                tv_targetAmt.text = it.targetAmt.toString()
                tv_currentAmt.text = it.currentAmt.toString()
                tv_bucketList.text = it.bucketList.toString()
                // Displaying deadline as DateTime rather than TimeStamp for user viewing
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")
                tv_deadline.text = dateFormatter.format(it.deadline).toString()
                tv_lastReminder.text = it.lastReminder.toString()
                tv_reminderMonthsFreq.text = it.reminderMonthsFreq.toString()
                // Displaying createDate as DateTime rather than TimeStamp for user viewing
                tv_createDate.text = dateFormatter.format(it.createDate).toString()
            }
        }

        btn_EditGoalDetails.setOnClickListener {
            val editGoalDetailsFragment = EditGoalDetailsFragment()

            bundle.putString("final_date", "default")
            editGoalDetailsFragment.arguments = bundle

            fragmentUtils.replace(editGoalDetailsFragment)
        }

        btn_CompleteGoal.setOnClickListener {
            Log.d("Complete Goal", "FIRED")
        }

        setFragmentResultListener("deadlinePicker") { _, bundle ->
            Log.e("Test", "Fired")
            bundle.getString("update Categories")
            textInput_deadline.setText(bundle.getString("final_date"))
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