package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import java.util.*

class CreateGoalFragment_2 : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils

    private var goal: Goal = Goal()
    private val reminderIdToVal = mapOf(
        R.id.radiobutton_reminder_0mth to 0,
        R.id.radiobutton_reminder_3mth to 3,
        R.id.radiobutton_reminder_6mth to 6
    )
    private val locale = Locale("en", "SG")
    private val timezone = TimeZone.getTimeZone("Asia/Singapore")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yearPicker = number_picker_target_duration_year
        val monthPicker = number_picker_target_duration_month

        yearPicker.minValue = 0
        yearPicker.maxValue = 35

        monthPicker.minValue = 0
        monthPicker.maxValue = 11

        yearPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue && monthPicker.value < monthPicker.maxValue) {
                monthPicker.value += 1
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue && monthPicker.value > monthPicker.minValue) {
                monthPicker.value -= 1
            }
        }

        monthPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue && yearPicker.value < yearPicker.maxValue) {
                yearPicker.value += 1
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue && yearPicker.value > yearPicker.minValue) {
                yearPicker.value -= 1
            }
        }

        val calDiff = Calendar.getInstance(timezone, locale).apply {
            timeInMillis = goal.deadline.time - Calendar.getInstance(timezone, locale).time.time
        }

        number_picker_target_duration_month.value = calDiff.get(Calendar.MONTH)
        number_picker_target_duration_year.value = calDiff.get(Calendar.YEAR) - 1970

        if (goal.reminderMonthsFreq != 0) {
            radioGroup_reminder.check(
                reminderIdToVal.filterValues { it == goal.reminderMonthsFreq }.keys.first()
            )
        }

        btn_ToFragmentOne.setOnClickListener {
            updateGoalModel()
            fragmentUtils.popBackStack()
        }

        btn_ToFragmentThree.setOnClickListener {
            val createGoalFragment3 = CreateGoalFragment_3()

            updateGoalModel()
            createGoalFragment3.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(createGoalFragment3)
        }
    }

    private fun updateGoalModel() {
        val deadline = Calendar.getInstance(timezone, locale)

        deadline.add(Calendar.MONTH, number_picker_target_duration_month.value)
        deadline.add(Calendar.YEAR, number_picker_target_duration_year.value)

        goal.deadline = deadline.time
        goal.reminderMonthsFreq = reminderIdToVal.getValue(radioGroup_reminder.checkedRadioButtonId)
    }
}