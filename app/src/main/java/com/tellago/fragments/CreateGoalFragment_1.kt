package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_1.*

class CreateGoalFragment_1 : Fragment() {
    private var goal: Goal = Goal()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_ToFragmentTwo.setOnClickListener {
            val errors = mutableMapOf<String, String>()

            et_goalTitle.error = null
            et_goalPrice.error = null

            if (et_goalTitle.text.isNullOrBlank()) errors["title"] = "Field is required"
            if (et_goalPrice.text.isNullOrBlank()) errors["amount"] = "Field is required"
            if (Regex("\\d+?\\.\\d{3,}").matches(et_goalPrice.text.toString())) errors["amount"] = "Cents can't be more then 2 digits"

            if (errors.isNotEmpty()) {
                errors["title"].let { et_goalTitle.error = it }
                errors["amount"].let { et_goalPrice.error = it }
            }
            else {
                val createGoalFragment2 = CreateGoalFragment_2()
                val categories = ArrayList<String>()

                if (category_btn_1.isChecked) categories.add("career")
                if (category_btn_2.isChecked) categories.add("family")
                if (category_btn_3.isChecked) categories.add("leisure")

                goal.title = et_goalTitle.text.toString()
                goal.targetAmt = et_goalPrice.text.toString().toDouble()
                goal.categories = categories

                createGoalFragment2.arguments = Bundle().apply {
                    putParcelable(goal::class.java.name, goal)
                }

                FragmentUtils(
                    requireActivity().supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(createGoalFragment2)
            }
        }
    }
}