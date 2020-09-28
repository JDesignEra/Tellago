package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.tellago.R
import com.tellago.interfaces.GoalsCommunicator
import kotlinx.android.synthetic.main.fragment_create_goal_1.view.*

class CreateGoalFragment_1 : Fragment() {
    private lateinit var communicator: GoalsCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communicator = activity as GoalsCommunicator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_goal_1, container, false)

        view.btn_ToFragmentTwo.setOnClickListener {
            if (!view.et_goalTitle.text.isNullOrBlank()) {
                communicator.requestKey = "goal2"

                setFragmentResult(communicator.requestKey, Bundle().apply {
                    putString(communicator.titleKey, view.et_goalTitle.text.toString())
                    putBoolean(communicator.careerKey, view.category_btn_1.isChecked)
                    putBoolean(communicator.familyKey, view.category_btn_2.isChecked)
                    putBoolean(communicator.leisureKey, view.category_btn_3.isChecked)

                    communicator.replaceFragment(CreateGoalFragment_2())
                })
            }
            else {
                view.et_goalTitle.error = "Your goal is required"
            }
        }

        return view
    }
}