package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.tv_jid
import java.text.SimpleDateFormat
import java.util.*


class ShowGoalDetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val bundle = this.arguments
        if (bundle != null) {
            Goal(gid = bundle.getString("goal_id")).getByGid {
                if (it != null) {
                    // Assign to relevant edit text elements below
                    tv_goalID_gone.text = it.gid
                    tv_jid.text = it.jid
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
        }

        btn_EditGoalDetails.setOnClickListener {

            val editGoalDetailsFragment = EditGoalDetailsFragment()
            //    Use bundle to pass goal_id Data to editGoalDetailsFragment
            val bundle = Bundle()
            bundle.putString("goal_id", tv_goalID_gone.text as String?)
            bundle.putString("final_date", "default")
            editGoalDetailsFragment.arguments = bundle
            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            )
                .replace(editGoalDetailsFragment)
        }

        btn_CompleteGoal.setOnClickListener {
            // Allow user to 'Complete' the current Goal --> award a badge for achievement
            Log.d("Complete Goal", "FIRED")
        }

    }

    private fun configureToolbar() {
        toolbar_view_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_view_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }
    }

}