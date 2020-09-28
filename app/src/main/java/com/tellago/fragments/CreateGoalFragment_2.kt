package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.interfaces.CreateGoalCommunicator
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import kotlinx.android.synthetic.main.fragment_create_goal_2.view.*

class CreateGoalFragment_2 : Fragment() {

    private lateinit var communicator: CreateGoalCommunicator

    private val fragmentUtils = activity?.supportFragmentManager?.let {
        FragmentUtils(
            it,
            R.id.fragment_container_create_goal
        )
    }


    // The message received from the previous fragment is shown using the following variable
    var displayMessage1: String? = ""
    var displayMessage2: String? = ""
    var displayMessage3: String? = ""
    var displayMessage4: String? = ""
    var durationInt: Int? = 0
    var durationString: String? = ""
    var reminderInt: Int? = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_goal_2, container, false)
        communicator = activity as CreateGoalCommunicator


        view.btn_ToFragmentOne.setOnClickListener {
            // Return to previous fragment
            fragmentManager?.popBackStack()
        }


        view.btn_ToFragmentThree.setOnClickListener {

            durationInt = number_picker_target_duration.value

            Log.d("durationInt", durationInt.toString())

            Log.d("durationString", durationString)

            Log.d("reminderInt", reminderInt.toString())

            communicator.secondFormSubmit(
                displayMessage1.toString(),
                displayMessage2!!.toInt(),
                displayMessage3!!.toInt(),
                displayMessage4!!.toInt(),
                durationInt!!,
                durationString!!,
                reminderInt!!
            )

            Log.d("To Fragment 3", "FIRED")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayMessage1 = arguments?.getString("string 1")
        displayMessage2 = arguments?.getInt("careerSelected").toString()
        displayMessage3 = arguments?.getInt("familySelected").toString()
        displayMessage4 = arguments?.getInt("leisureSelected").toString()

        Log.d("dMsg2", displayMessage2.toString())
        Log.d("dMsg3", displayMessage3.toString())
        Log.d("dMsg4", displayMessage4.toString())

        // Define range of number picker (0 to 30)
        number_picker_target_duration.minValue = 0
        number_picker_target_duration.maxValue = 30

        // Show the previous fragment's data (retrieved from bundle & key = string 1) within tv_FragmentTwoReceipt
//        view.tv_Fragment2Receipt.text = displayMessage1
//        Log.d("displayMessage1 in TV", displayMessage1.toString())
//        view.tv_category_btn_1.text = "Value of Category Checkbox = %s".format(displayMessage2)
//        view.tv_category_btn_2.text = "Value of Family Checkbox = %s".format(displayMessage3)
//        view.tv_category_btn_3.text = "Value of Leisure Checkbox = %s".format(displayMessage4)

        target_duration_month.setOnClickListener {
            durationString = "mth"
        }

        target_duration_year.setOnClickListener {
            durationString = "yr"
        }

        radiobutton_reminder_0mth.setOnClickListener {
            reminderInt = 0
        }

        radiobutton_reminder_3mth.setOnClickListener {
            reminderInt = 3
        }

        radiobutton_reminder_6mth.setOnClickListener {
            reminderInt = 6
        }


    }

    companion object {

    }
}