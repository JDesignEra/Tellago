package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.activities.GoalsActivity
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_1.*

class CreateGoalFragment_1 : Fragment() {
    private var goal: Goal = Goal()
    private var bundle: Bundle? = null
    private lateinit var fragmentUtils: FragmentUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = arguments
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_goal_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        tv_next_to_fragment_2.setOnClickListener {
            navigateToFragment2()
        }

        image_view_create_goal_1_to_2.setOnClickListener {
            navigateToFragment2()
        }
    }

    private fun navigateToFragment2() {
        val errors = mutableMapOf<String, String>()

        et_goalTitle.error = null
        et_goalPrice.error = null

        if (et_goalTitle.text.isNullOrBlank()) errors["title"] = "Field is required"
        when {
            et_goalPrice.text.isNullOrBlank() -> errors["amount"] = "Field is required"
            et_goalPrice.text.toString().toDouble() < 0.01 -> errors["amount"] =
                "Needs to be more than 0"
            Regex("\\d+?\\.\\d{3,}").matches(et_goalPrice.text.toString()) -> errors["amount"] =
                "Cents can't be more then 2 digits"
        }

        if (errors.isNotEmpty()) {
            errors["title"].let { et_goalTitle.error = it }
            errors["amount"].let { et_goalPrice.error = it }
        } else {
            val createGoalFragment2 = CreateGoalFragment_2()
            val categories = ArrayList<String>()

            
            when (categories_toggleGrp.checkedButtonId)
            {
                R.id.btn_career -> categories.add("career")
                R.id.btn_family -> categories.add("family")
                R.id.btn_leisure -> categories.add("leisure")
            }

            Log.d("Printing categories", categories.toString())

//            if (btn_career.isSelected) Log.d("career select", "FIRED")
//            else Log.d("career unselect", "FIRED")
//
//            if (btn_family.isSelected) Log.d("family select", "FIRED")
//            else Log.d("family unselect", "FIRED")
//
//            if (btn_leisure.isSelected) Log.d("leisure select", "FIRED")
//            else Log.d("leisure unselect", "FIRED")

    //                if (category_btn_1.isChecked) categories.add("career")
    //                if (category_btn_2.isChecked) categories.add("family")
    //                if (category_btn_3.isChecked) categories.add("leisure")

            goal.title = et_goalTitle.text.toString()
            goal.targetAmt = et_goalPrice.text.toString().toDouble()
            goal.categories = categories

            Log.d("Printing G categories", goal.categories.toString())


            createGoalFragment2.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
                putStringArrayList("pids", bundle?.getStringArrayList("pids"))
            }

            fragmentUtils.replace(createGoalFragment2, setTargetFragment = this, requestCode = -1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == -1) {
            bundle?.putStringArrayList("pids", data?.getStringArrayListExtra("pids"))
        }
    }

    private fun configureToolbar() {
        tv_toolbar_cancel_create_goal_1.setOnClickListener {
            if (requireActivity().intent.getStringExtra(HomeFragment::class.java.name) == "show") {
                val intent = Intent(requireContext(), GoalsActivity::class.java)
                intent.putExtra(HomeFragment::class.java.name, "show")

                startActivity(intent)
            }

            requireActivity().finish()
        }

        toolbar_createGoalFragment1.setNavigationOnClickListener {
            if (requireActivity().intent.getStringExtra(HomeFragment::class.java.name) == "show") {
                val intent = Intent(requireContext(), GoalsActivity::class.java)
                intent.putExtra(HomeFragment::class.java.name, "show")

                startActivity(intent)
            }

            requireActivity().finish()
        }
    }
}