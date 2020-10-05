package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import com.tellago.utilities.NumPickerUtils
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*

class CreateGoalFragment_2 : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    lateinit var numPickerUtils: NumPickerUtils

    private var goal: Goal = Goal()
    private val reminderIdToVal = mapOf(
        R.id.radiobutton_reminder_0mth to 0,
        R.id.radiobutton_reminder_3mth to 3,
        R.id.radiobutton_reminder_6mth to 6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        numPickerUtils = NumPickerUtils(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todayCalendar = Calendar.getInstance()

        val yearPicker = number_picker_target_duration_year
        val monthPicker = number_picker_target_duration_month

        yearPicker.minValue = 0
        yearPicker.maxValue = 35

        monthPicker.minValue = 0
        monthPicker.maxValue = todayCalendar.getMaximum(Calendar.MONTH)

        val today = LocalDate.now()
        val deadline = Instant.ofEpochMilli(goal.deadline.time).atZone(ZoneId.systemDefault()).toLocalDate()
        val dateDiff = Period.between(today, deadline)
        val yearsDiff = dateDiff.years
        val monthsDiff = dateDiff.months

        yearPicker.value = yearsDiff
        monthPicker.value = monthsDiff

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

        yearPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue && monthPicker.value < monthPicker.maxValue) {
                numPickerUtils.animateValueByOne(monthPicker)
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue && monthPicker.value > monthPicker.minValue) {
                numPickerUtils.animateValueByOne(monthPicker, false)
            }
        }

        monthPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue && yearPicker.value < yearPicker.maxValue) {
                numPickerUtils.animateValueByOne(yearPicker)
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue && yearPicker.value > yearPicker.minValue) {
                numPickerUtils.animateValueByOne(yearPicker, false)
            }
        }
    }

    private fun updateGoalModel() {
        val deadline = Calendar.getInstance().apply {
            add(Calendar.MONTH, number_picker_target_duration_month.value)
            add(Calendar.YEAR, number_picker_target_duration_year.value)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.HOUR, 0)
        }

        goal.deadline = deadline.time
        goal.reminderMonthsFreq = reminderIdToVal.getValue(radioGroup_reminder.checkedRadioButtonId)
    }
}