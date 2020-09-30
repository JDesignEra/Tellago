package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_1.*
import kotlinx.android.synthetic.main.fragment_create_goal_1.view.*

class CreateGoalFragment_1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_ToFragmentTwo.setOnClickListener {
            if (!view.et_goalTitle.text.isNullOrBlank()) {
                val createGoalFragment_2 = CreateGoalFragment_2()

                createGoalFragment_2.arguments = Bundle().apply {
                    putString("title", view.et_goalTitle.text.toString())
                    putBoolean("career", view.category_btn_1.isChecked)
                    putBoolean("family", view.category_btn_2.isChecked)
                    putBoolean("leisure", view.category_btn_3.isChecked)
                }

                FragmentUtils(
                    requireActivity().supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(createGoalFragment_2)
            }
            else {
                view.et_goalTitle.error = "Your goal is required"
            }
        }
    }
}