package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.tellago.R
import com.tellago.interfaces.CreateGoalCommunicator
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_create_goal_1.*
import kotlinx.android.synthetic.main.fragment_create_goal_1.view.*
import kotlinx.android.synthetic.main.fragment_create_goal_one.view.*

class CreateGoalFragment_1 : Fragment() {

    private lateinit var communicator: CreateGoalCommunicator

    private lateinit var toast: CustomToast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = CustomToast(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_goal_1, container, false)
        communicator = activity as CreateGoalCommunicator

        view.btn_ToFragmentTwo.setOnClickListener {
            var checkbox_state1 : Int = 0
            var checkbox_state2 : Int = 0
            var checkbox_state3 : Int = 0


            // If checkbox states have changed:
            if(view.category_btn_1.isChecked)
                // pass int 1 to communicator if 1st category button (Career) is selected
                checkbox_state1 = 1
            else
                // pass int 0 to communicator if 1st category button (Career) is not selected
                checkbox_state1 = 0
            if(view.category_btn_2.isChecked)
            // pass int 1 to communicator if 2nd category button (Family) is selected
                checkbox_state2 = 1
            else
            // pass int 0 to communicator if 2nd category button (Family) is not selected
                checkbox_state2 = 0
            if(view.category_btn_3.isChecked)
            // pass int 1 to communicator if 3rd category button (Leisure) is selected
                checkbox_state3 = 1
            else
            // pass int 0 to communicator if 3rd category button (Leisure) is not selected
                checkbox_state3 = 0

            // use communicator to pass String data from Edit Text_et_goalTitle & Int data from states of 3 checkboxes
            communicator.firstFormSubmit(
                view.et_goalTitle.text.toString(),
                checkbox_state1,
                checkbox_state2,
                checkbox_state3
            )

        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    companion object {

    }
}