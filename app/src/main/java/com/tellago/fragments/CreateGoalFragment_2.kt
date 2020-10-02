package com.tellago.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        number_picker_target_duration_year.minValue = 0
        number_picker_target_duration_year.maxValue = 35

        number_picker_target_duration_month.minValue = 0
        number_picker_target_duration_month.maxValue = 11

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
            setFragmentResult(CreateGoalFragment_1::class.java.name, Bundle().apply {
                putParcelable("Goal", goal)
            })

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