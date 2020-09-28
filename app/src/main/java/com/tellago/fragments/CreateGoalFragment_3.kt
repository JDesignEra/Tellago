package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.tellago.R
import com.tellago.interfaces.GoalsCommunicator
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_create_goal_3.view.*
import java.util.*


class CreateGoalFragment_3 : Fragment() {
    private lateinit var communicator: GoalsCommunicator
    private lateinit var toast: CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communicator = activity as GoalsCommunicator
        toast = CustomToast(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_goal_3, container, false)

        view.btn_BackToFragmentTwo.setOnClickListener {
            communicator.popBackFragment()
        }

        view.btn_CreateGoal.setOnClickListener {
            setFragmentResultListener(communicator.requestKey) { _, bundle ->
                Log.e("Title", bundle.getString(communicator.titleKey).toString())
                val locale = Locale("en", "SG")
                val timezone = TimeZone.getTimeZone("Asia/Singapore")
                val category = mutableListOf<String>()
                val deadline = Calendar.getInstance(timezone, locale)
                deadline.add(Calendar.MONTH, bundle.getInt(communicator.durationKey))

                if (bundle.getBoolean(communicator.careerKey)) category.add(communicator.careerKey)
                if (bundle.getBoolean(communicator.familyKey)) category.add(communicator.familyKey)
                if (bundle.getBoolean(communicator.leisureKey)) category.add(communicator.leisureKey)

                Goal(
                    uid = user?.uid,
                    title = bundle.getString(communicator.titleKey),
                    category = category,
                    targetAmt = 5000,
                    deadline = deadline.time,
                    reminderMonthsFreq = bundle.getInt(communicator.reminderKey),
                    createDate = Calendar.getInstance(timezone, locale).time
                ).add {
                    if (it != null) {
                        toast.success("Goal created successfully")
                        requireActivity().finish()
                    }
                    else {
                        toast.error("Failed to create goal, please try again")
                    }
                }
            }
        }

        return view
    }
}