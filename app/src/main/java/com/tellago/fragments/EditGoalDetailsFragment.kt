package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.text.SimpleDateFormat
import java.util.*

class EditGoalDetailsFragment : Fragment() {
    private var bundle: Bundle? = null
    private lateinit var goal: Goal

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        textInput_title.setText(goal.title)
        textInput_targetAmt.setText(String.format("%.2f", goal.targetAmt))
        textInput_currentAmt.setText(String.format("%.2f", goal.currentAmt))

        if (goal.completed) {
            textInputLayout_targetAmt.visibility = View.GONE
            textInputLayout_currentAmt.visibility = View.GONE
            textInputLayout_deadline.visibility = View.GONE
            tv_reminderFreqTitle.visibility = View.GONE
            radioGrp_reminder.visibility = View.GONE
        }

        if (goal.reminderMonthsFreq == 3) radioBtn_3MonthsReminder.isChecked = true
        else if (goal.reminderMonthsFreq == 6) radioBtn_6MonthsReminder.isChecked = true

        if (goal.categories.contains("career")) chip_career.isChecked = true
        if (goal.categories.contains("family")) chip_family.isChecked = true
        if (goal.categories.contains("leisure")) chip_leisure.isChecked = true

        btn_ConfirmEditGoalDetails.setOnClickListener {
            if (!goal.gid?.isBlank()!!) {
                updateGoalModel()

                goal.setByGid {
                    if (it != null) {
                        fragmentUtils.popBackStack()
                        toast.success("Goal updated")
                    }
                    else toast.error("Please try again, there was an issue when updating your goal")
                }
            }
        }

        textInputLayout_deadline.setEndIconOnClickListener {
            val dialogFragment = EditDeadlinePickerFragment()
            updateGoalModel()

            dialogFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(dialogFragment, setTargetFragment = this, requestCode = 0)
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = goal.gid).deleteByGid()
            fragmentUtils.replace(ShowGoalsFragment())
        }
    }

    override fun onStart() {
        super.onStart()

        textInput_deadline.setText(dateFormatter.format(goal.deadline))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Goal>(Goal::class.java.name).let {
                goal = it!!
            }
        }
    }

    private fun updateGoalModel() {
        goal.title = textInput_title.text.toString()
        goal.targetAmt = textInput_targetAmt.text.toString().toDouble()
        goal.currentAmt = textInput_currentAmt.text.toString().toDouble()
        goal.deadline = dateFormatter.parse(textInput_deadline.text.toString())!!
        goal.reminderMonthsFreq = when {
            radioBtn_3MonthsReminder.isChecked -> 3
            radioBtn_6MonthsReminder.isChecked -> 6
            else -> 0
        }
        goal.categories.apply {
            removeAll(this)
            if (chip_career.isChecked) add("career")
            if (chip_family.isChecked) add("family")
            if (chip_leisure.isChecked) add("leisure")
        }
    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }
}