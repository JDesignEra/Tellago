package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import java.text.SimpleDateFormat
import java.util.*


class EditGoalDetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()

        var goalID : String = ""

        val bundle = this.arguments
        if (bundle != null) {
            goalID = bundle.getString("goal_id").toString()
            Goal(gid = goalID).getGoal {
                if (it != null) {
                    // Assign to relevant edit text elements below

                    et_title.setText(it.title.toString())
                    //et_title.hint = it.title.toString()
                    et_categories.setText(it.category.toString())
                    et_targetAmt.setText(it.targetAmt.toString())
                    et_currentAmt.setText(it.currentAmt.toString())
                    // JID can be modified, but it will not be using Edit Text
                    // MaterialButton for JID?
                    et_bucketList.setText(it.bucketList.toString())
                    // Displaying deadline as DateTime rather than TimeStamp for user viewing
                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")
                    et_deadline.setText(dateFormatter.format(it.deadline).toString())
                    et_reminderMonthsFreq.setText(it.reminderMonthsFreq.toString())

                }
            }
        }


        btn_ConfirmEditGoalDetails.setOnClickListener {
            Log.d("Confirm Edit", "FIRED")
            if(goalID != "")
            {
                Goal(
                    gid = goalID,
                    title = et_title.text.toString(),
                    targetAmt = et_targetAmt.text.toString().toInt(),
                    currentAmt = et_currentAmt.text.toString().toInt()

                ).update {
                }
            }

        }

        btn_DeleteGoal.setOnClickListener {
            Log.d("Delete Goal", "FIRED")
        }
        
    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            )
                .popBackStack()
        }
    }
}