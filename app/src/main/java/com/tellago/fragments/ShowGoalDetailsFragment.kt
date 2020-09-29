package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*


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

            Goal(gid = bundle.getString("goal_id")).getGoal {
                if (it != null) {
                    // Assign to relevant edit text elements below
                    tv_gid.text = it.gid
                    tv_uid.text = it.uid
                    tv_jid.text = it.jid
                    et_title.setText(it.title)
                    et_categories.setText("Null")
                    et_targetAmt.setText(it.targetAmt.toString())
                    et_currentAmt.setText(it.currentAmt.toString())
                    et_bucketList.setText("Null")
                    et_deadline.setText(it.deadline.toString())
                    tv_lastReminder.text = it.lastReminder.toString()
                    et_reminderMonthsFreq.setText(it.reminderMonthsFreq.toString())
                    tv_createDate.text = it.createDate.toString()

                }
            }
        }

        btn_EditGoalDetails.setOnClickListener {
            Log.d("btn EditGoal", "FIRED")
        }

    }

    private fun configureToolbar() {
        toolbar_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }
    }

}