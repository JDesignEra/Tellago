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
import java.time.*
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

        val today = LocalDate.now()

        val yearPicker = number_picker_target_duration_year
        val monthPicker = number_picker_target_duration_month

        yearPicker.minValue = 0
        yearPicker.maxValue = 35

        monthPicker.minValue = 0
        monthPicker.maxValue = YearMonth.of(today.year, today.month).lengthOfMonth() - 1

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

        btn_Bucket_List.setOnClickListener {

            val showBucketListItemsFragment = ShowBucketListItemsFragment()

            showBucketListItemsFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(showBucketListItemsFragment)
        }

    }

    private fun updateGoalModel() {
        var deadline = LocalDate.now()
        deadline = deadline.plusYears(number_picker_target_duration_year.value.toLong())
        deadline = deadline.plusMonths(number_picker_target_duration_month.value.toLong())

        goal.deadline = Calendar.getInstance().apply {
            time = Date.from(deadline.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
            set(Calendar.MILLISECOND, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.HOUR, 0)
        }.time
        goal.reminderMonthsFreq = reminderIdToVal.getValue(radioGroup_reminder.checkedRadioButtonId)
    }
}