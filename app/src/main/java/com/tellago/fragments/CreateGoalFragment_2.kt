package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.tellago.R
import com.tellago.activities.GoalsActivity
import com.tellago.interfaces.GoalsCommunicator
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import kotlinx.android.synthetic.main.fragment_create_goal_2.view.*

class CreateGoalFragment_2 : Fragment() {
    private lateinit var communicator: GoalsCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communicator = activity as GoalsCommunicator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_goal_2, container, false)

        view.btn_ToFragmentOne.setOnClickListener {
            communicator.popBackFragment()
        }

        view.btn_ToFragmentThree.setOnClickListener {
            var durationMonth = view.number_picker_target_duration.value
            var reminderMonth: Int? = null

            when (view.radioGroup_duration.checkedRadioButtonId) {
                R.id.radioBtn_duration_years -> durationMonth *= 12   // Convert to Months
            }

            when (radioGroup_reminder.checkedRadioButtonId) {
                R.id.radiobutton_reminder_3mth -> reminderMonth = 3
                R.id.radiobutton_reminder_6mth -> reminderMonth = 6
            }

            setFragmentResultListener(communicator.requestKey) { _, bundle ->
                communicator.requestKey = "goal3"

                setFragmentResult(communicator.requestKey, bundle.apply {
                    putAll(bundle)
                    putInt(communicator.durationKey, durationMonth)
                    if (reminderMonth != null) putInt(communicator.requestKey, reminderMonth)

                    communicator.replaceFragment(CreateGoalFragment_3())
                })
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        number_picker_target_duration.minValue = 1
        number_picker_target_duration.maxValue = 30
    }
}