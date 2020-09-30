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


        val bundle = this.arguments
        if (bundle != null) {

            Goal(gid = bundle.getString("goal_id")).getGoal {
                if (it != null) {
                    // Assign to relevant edit text elements below

                    et_title.setText(it.title.toString())
                    et_categories.setText(it.category.toString())
                    et_targetAmt.setText(it.targetAmt.toString())
                    et_currentAmt.setText(it.currentAmt.toString())
                    // JID can be modified, but it will not be using Edit Text
                    // MaterialButton for JID?
                    et_bucketList.setText(it.bucketList.toString())
                    et_deadline.setText(it.deadline.toString())
                    et_reminderMonthsFreq.setText(it.reminderMonthsFreq.toString())

                }
            }
        }


        btn_ConfirmEditGoalDetails.setOnClickListener {
            Log.d("Confirm Edit", "FIRED")

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