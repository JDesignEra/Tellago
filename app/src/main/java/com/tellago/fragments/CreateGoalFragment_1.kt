package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.android.synthetic.main.fragment_create_goal_1.*
import kotlinx.android.synthetic.main.fragment_create_goal_1.view.*
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*

class CreateGoalFragment_1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.arguments != null) {
            val bundle = requireArguments()

            et_goalTitle.setText(bundle.getString("title", ""))
            category_btn_1.isChecked = bundle.getBoolean("career", false)
            category_btn_1.isChecked = bundle.getBoolean("family", false)
            category_btn_1.isChecked = bundle.getBoolean("leisure", false)
        }

        btn_ToFragmentTwo.setOnClickListener {

            val createGoalFragment2 = CreateGoalFragment_2()
            var title_valid = true
            var amount_valid = true

            // No need val
            if (!view.et_goalPrice.text.isNullOrBlank()) {

                // split text (confirmed numbers) based on decimal point
                val strs_goalPrice = view.et_goalPrice.text.toString().split(".").toTypedArray()
                // There will be 2 elements in the strs_goalPrice ArrayList (internal conversion)

                if (strs_goalPrice.size == 2)
                {
                    val goalPrice_dollar = strs_goalPrice[0]
                    Log.d("dollar", goalPrice_dollar.toString())
                    val goalPrice_cent = strs_goalPrice[1]

                    // check if dollar is negative (Are there actually keyboards which would allow negative value for numberDecimal input type?)
//                    if (goalPrice_dollar.toInt() < 0)
//                    {
//                        et_goalPrice.error = "Your goal amount cannot be a negative value"
//                        amount_valid = false
//                    }

                    // check if dollar is too unrealistic (10 million & above)
                    if (goalPrice_dollar.toInt() > 9999999)
                    {
                        et_goalPrice.error = "Try a lower goal amount."
                        amount_valid = false
                    }

                    // check if cent is more than 2 decimal places
                    if (goalPrice_cent.toInt() > 100)
                    {
                        et_goalPrice.error = "Please enter an amount with a valid number of cents"
                        amount_valid = false
                    }

                    // check if no error message for et_goalPrice
                    if (et_goalPrice.error.isNullOrBlank())
                    {
                        amount_valid = true
                    }
                }

                else
                {
                    // here, the user did not enter a cent amount. So conduct check for dollar amount only
                    // check if dollar is too unrealistic (10 million & above)
                    if (et_goalPrice.text.toString().toInt() > 9999999)
                    {
                        et_goalPrice.error = "Try a lower goal amount."
                        amount_valid = false
                    }

                    // check if no error message for et_goalPrice
                    if (et_goalPrice.error.isNullOrBlank())
                    {
                        amount_valid = true
                    }

                }


            }
            else
            {
                et_goalPrice.error = "Your goal amount is required"
                amount_valid = false
            }


            if (!view.et_goalTitle.text.isNullOrBlank()) {
                title_valid = true
            }
            else {
                et_goalTitle.error = "Your goal is required"
                title_valid = false
            }


            if (title_valid && amount_valid)
            {
                createGoalFragment2.arguments = Bundle().apply {
                    putString("title", et_goalTitle.text.toString())
                    putString("price", et_goalPrice.text.toString())
                    putBoolean("career", category_btn_1.isChecked)
                    putBoolean("family", category_btn_2.isChecked)
                    putBoolean("leisure", category_btn_3.isChecked)
                }

                FragmentUtils(
                    requireActivity().supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(createGoalFragment2)
            }


        }


    }


}