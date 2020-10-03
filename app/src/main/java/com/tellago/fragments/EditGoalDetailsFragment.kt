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
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class EditGoalDetailsFragment : Fragment() {
    private var bundle: Bundle? = null
    private lateinit var goal: Goal

    private val locale = Locale("en", "SG")
    private val timezone = TimeZone.getTimeZone("Asia/Singapore")
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).apply {
        timeZone = timezone
    }
    private var finalDate: String? = null

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

            val strs_deadline_toEdit = textInput_deadline.text.toString()
                .split("/").toTypedArray()

             // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
            if (strs_deadline_toEdit.isNotEmpty()) {
                val deadline_day_toEdit = strs_deadline_toEdit[0]
                val deadline_month_toEdit = strs_deadline_toEdit[1]
                val deadline_year_toEdit = strs_deadline_toEdit[2]

                dialogFragment.arguments = Bundle().apply {
                    putParcelable(goal::class.java.name, goal)
                    putString("final_date", "default")
                    putString("day_toEdit", deadline_day_toEdit)
                    putString("month_toEdit", deadline_month_toEdit)
                    putString("year_toEdit", deadline_year_toEdit)
                }
            }
            else toast.error("Please try again, there was an error opening up the Deadline picker")

            fragmentUtils.replace(dialogFragment, setTargetFragment = this, requestCode = 0)
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = goal.gid).deleteByGid()
            fragmentUtils.replace(ShowGoalsFragment())
        }
    }

    override fun onStart() {
        super.onStart()

        textInput_deadline.setText(finalDate ?: dateFormatter.format(goal.deadline))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data?.getStringExtra("final_date") != null) {
                finalDate = data.getStringExtra("final_date")

                goal.deadline = Timestamp.valueOf(
                    convertToJdbc(finalDate!!)
                )
            }
        }
    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

    private fun updateGoalModel() {
        goal.title = textInput_title.text.toString()
        goal.targetAmt = textInput_targetAmt.text.toString().toDouble()
        goal.currentAmt = textInput_currentAmt.text.toString().toDouble()
        goal.deadline = Calendar.getInstance(timezone, locale).apply {
            time = dateFormatter.parse(textInput_deadline.text.toString())!!
        }.time
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

    private fun convertToJdbc(string_deadline: String): String {
        // Converting deadline from dd-MM-yyyy to JDBC timestamp format
        val strs_deadline = string_deadline.split("/").toTypedArray()
        // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
        var deadline_JDBC_string = string_deadline

        if (strs_deadline.size != 0)
        {
            val deadline_day = strs_deadline[0]
            val deadline_month = strs_deadline[1]
            val deadline_year = strs_deadline[2]

            deadline_JDBC_string = deadline_year +
                    "-" + deadline_month +
                    "-" + deadline_day +
                    " 00:01:02.345678901"
        }

        return deadline_JDBC_string
    }
}