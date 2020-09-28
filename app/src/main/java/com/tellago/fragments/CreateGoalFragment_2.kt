package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.interfaces.CreateGoalCommunicator
import kotlinx.android.synthetic.main.fragment_create_goal_2.view.*
import kotlinx.android.synthetic.main.fragment_create_goal_two.view.*

class CreateGoalFragment_2 : Fragment() {

    private lateinit var communicator: CreateGoalCommunicator

    // The message received from the previous fragment is shown using the following variable
    var displayMessage1: String? = ""
    var displayMessage2: String? = ""
    var displayMessage3: String? = ""
    var displayMessage4: String? = ""


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

        view.btn_ToFragmentThree.setOnClickListener {

            communicator.secondFormSubmit(
                displayMessage1.toString(),
                displayMessage2!!.toInt(),
                displayMessage3!!.toInt(),
                displayMessage4!!.toInt(),
                100,
                "Test 200",
                300
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


        // Show the previous fragment's data (retrieved from bundle & key = string 1) within tv_FragmentTwoReceipt
//        view.tv_Fragment2Receipt.text = displayMessage1
//        Log.d("displayMessage1 in TV", displayMessage1.toString())
//        view.tv_category_btn_1.text = "Value of Category Checkbox = %s".format(displayMessage2)
//        view.tv_category_btn_2.text = "Value of Family Checkbox = %s".format(displayMessage3)
//        view.tv_category_btn_3.text = "Value of Leisure Checkbox = %s".format(displayMessage4)



    }

    companion object {

    }
}