package com.tellago.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.interfaces.CreateGoalCommunicator
import kotlinx.android.synthetic.main.fragment_create_goal_2.view.*
import kotlinx.android.synthetic.main.fragment_create_goal_3.view.*


class CreateGoalFragment_3 : Fragment() {

    // The message received from the previous fragment is shown using the following variable
    var displayMessage1: String? = ""
    var displayMessage2: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_goal_3, container, false)


        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayMessage1 = "Second Fragment needs to pass the necessary information to Third Fragment"

        displayMessage2 =
                    arguments?.getString("string 2") +
                    arguments?.getInt("careerSelected").toString() +
                    arguments?.getInt("familySelected").toString() +
                    arguments?.getInt("leisureSelected").toString() +
                    arguments?.getInt("duration Int").toString() +
                    arguments?.getString("duration String") +
                    arguments?.getInt("reminder Int").toString()


        view.tv_Fragment3Receipt_1.text = displayMessage1
        view.tv_Fragment3Receipt_2.text = displayMessage2

    }

    companion object {

    }
}