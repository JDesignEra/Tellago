package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.text.SimpleDateFormat
import java.util.*


class EditGoalDetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()

        val bundle = requireArguments()
        val gid: String = bundle.getString("goal_id", null)
        lateinit var categoriesList: MutableList<String>

        Goal(gid = gid).getByGid {
            if (it != null) {
                // Assign to relevant edit text elements below
                textInput_title.setText(it.title.toString())

                textInput_targetAmt.setText(it.targetAmt.toString())
                textInput_currentAmt.setText(it.currentAmt.toString())

                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")

                val deadline = if (it.deadline != null) {
                    dateFormatter.format(it.deadline)
                } else  ""

                textInput_deadline.setText(deadline)
                textInput_reminderFreq.setText((it.reminderMonthsFreq ?: 0).toString())

                categoriesList = it.category?.toMutableList() ?: mutableListOf()

                if (categoriesList.contains("career")) btnToggleGrp_category.check(R.id.btn_careerCategory)
                if (categoriesList.contains("family")) btnToggleGrp_category.check(R.id.btn_familyCategory)
                if (categoriesList.contains("leisure")) btnToggleGrp_category.check(R.id.btn_leisureCategory)
            }
        }

        btnToggleGrp_category.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId)  {
                R.id.btn_careerCategory -> {
                    if (isChecked) categoriesList.add("career")
                    else categoriesList.remove("career")
                }
                R.id.btn_familyCategory -> {
                    if (isChecked) categoriesList.add("family")
                    else categoriesList.remove("family")
                }
                R.id.btn_leisureCategory -> {
                    if (isChecked) categoriesList.add("leisure")
                    else categoriesList.remove("leisure")
                }
            }
        }

        // Open Categories List Dialog
//        showEditCategoriesListDialog()

        btn_ConfirmEditGoalDetails.setOnClickListener {
            if (!gid.isBlank()) {
                Goal(
                    gid = gid,
                    title = textInput_title.text.toString(),
                    category = categoriesList?.toList(),
                    targetAmt = textInput_targetAmt.text.toString().toInt(),
                    currentAmt = textInput_currentAmt.text.toString().toInt()
                ).updateByGid()
            }
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = gid).deleteByGid()
        }
    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            ).popBackStack()
        }
    }

//    private fun showEditCategoriesListDialog() {
//        btn_categories_edit.setOnClickListener {
//
//            // Building Normal Dialog Fragment
//            val dialogFragment = CategoriesDialogFragment()
//            val bundle = Bundle()
//
//            bundle.putString("goal_id", tv_goalID_edit_gone.text.toString())
//            dialogFragment.arguments = bundle
//
//            FragmentUtils(
//                requireActivity().supportFragmentManager,
//                R.id.fragment_container_goal_activity
//            )
//                .replace(dialogFragment)
//
//        }
//    }
}