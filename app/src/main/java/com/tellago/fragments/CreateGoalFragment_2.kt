package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import kotlinx.android.synthetic.main.fragment_create_goal_2.view.*

class CreateGoalFragment_2 : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        number_picker_target_duration_year.minValue = 0
        number_picker_target_duration_year.maxValue = 35

        number_picker_target_duration_month.minValue = 0
        number_picker_target_duration_month.maxValue = 11


        btn_ToFragmentOne.setOnClickListener {
            fragmentUtils.popBackStack()
        }

        btn_ToFragmentThree.setOnClickListener {
            var durationMonth = view.number_picker_target_duration_month.value
            var durationYear = view.number_picker_target_duration_year.value
            // Convert year value from number picker into months and then reassign to durationMonth
            durationMonth += (durationYear * 12)

            var reminderMonth: Int = 0

//            when (view.radioGroup_duration.checkedRadioButtonId) {
//                R.id.radioBtn_duration_years -> durationMonth *= 12   // Convert to Months
//            }

            when (radioGroup_reminder.checkedRadioButtonId) {
                R.id.radiobutton_reminder_3mth -> reminderMonth = 3
                R.id.radiobutton_reminder_6mth -> reminderMonth = 6
            }

            val createGoalFragment3 = CreateGoalFragment_3()

            createGoalFragment3.arguments = Bundle().apply {
                putAll(requireArguments())
                putInt("duration", durationMonth)
                putInt("reminder", reminderMonth)
            }

            fragmentUtils.replace(createGoalFragment3)
        }
    }
}