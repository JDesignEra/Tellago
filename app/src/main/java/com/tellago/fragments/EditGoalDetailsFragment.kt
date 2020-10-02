package com.tellago.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditGoalDetailsFragment : Fragment() {
    val locale = Locale("en", "SG")
    val timezone = TimeZone.getTimeZone("Asia/Singapore")
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", locale)

    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    override fun onStart() {
        super.onStart()

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        dateFormatter.timeZone = timezone
    }

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
        // assign value of gid to element on page 'persistent variable' for bundling
        tv_goalID_edit_gone.text = gid

        Goal(gid = gid).getByGid {
            if (it != null) {
                // Assign to relevant edit text elements below
                textInput_title.setText(it.title.toString())
                textInput_targetAmt.setText(it.targetAmt.toString())
                textInput_currentAmt.setText(it.currentAmt.toString())

                if (it.reminderMonthsFreq == 3) radioBtn_3MonthsReminder.isChecked = true
                else if (it.reminderMonthsFreq == 6) radioBtn_6MonthsReminder.isChecked = true

                val deadline = if (it.deadline != null) {
                    // Assign based on Firestore field value if bundle key 'final_date' has "default" value
                    if (bundle.getString("final_date") == "default"){
                        dateFormatter.format(it.deadline)
                    }
                    else {
                        // Reassign value to textInput_deadline if bundle key 'final_date' is not "default"
                        bundle.getString("final_date")
                    }
                } else  ""

                textInput_deadline.setText(deadline)
//                textInput_reminderFreq.setText((it.reminderMonthsFreq ?: 0).toString())

                val categoriesList = it.category ?: ArrayList()

                if (categoriesList.contains("career")) {
                    chip_career.isChecked = true
                }
                else {
                    chip_career.checkedIconTint = AppCompatResources
                        .getColorStateList(requireContext(), R.color.iconDefaultColor)
                }

                if (categoriesList.contains("family")) {
                    chip_family.isChecked = true
                }
                else {
                    chip_family.checkedIconTint = AppCompatResources
                        .getColorStateList(requireContext(), R.color.iconDefaultColor)
                }

                if (categoriesList.contains("leisure")) {
                    chip_leisure.isChecked = true
                }
                else {
                    chip_leisure.checkedIconTint = AppCompatResources
                        .getColorStateList(requireContext(), R.color.iconDefaultColor)
                }

                chip_career.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (!categoriesList.contains("career")) {
                            categoriesList.add("career")

                            chip_career.checkedIconTint = AppCompatResources
                                .getColorStateList(requireContext(), R.color.colorPrimary)
                        }
                    }
                    else {
                        categoriesList.remove("career")
                        chip_career.checkedIconTint = AppCompatResources
                            .getColorStateList(requireContext(), R.color.iconDefaultColor)
                    }
                }

                chip_family.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (!categoriesList.contains("family")) {
                            categoriesList.add("family")
                            chip_family.checkedIconTint = AppCompatResources
                                .getColorStateList(requireContext(), R.color.colorPrimary)
                        }
                    }
                    else {
                        categoriesList.remove("family")
                        chip_family.checkedIconTint = AppCompatResources
                            .getColorStateList(requireContext(), R.color.iconDefaultColor)
                    }
                }

                chip_leisure.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (!categoriesList.contains("leisure")) {
                            categoriesList.add("leisure")
                            chip_leisure.checkedIconTint = AppCompatResources
                                .getColorStateList(requireContext(), R.color.colorPrimary)
                        }
                    }
                    else {
                        categoriesList.remove("leisure")
                        chip_leisure.checkedIconTint = AppCompatResources
                            .getColorStateList(requireContext(), R.color.iconDefaultColor)
                    }
                }

                btn_ConfirmEditGoalDetails.setOnClickListener {
                    // Converting deadline from dd-MM-yyyy to JDBC timestamp format
                    val strs_deadline = textInput_deadline.text.toString().split("/").toTypedArray()
                    // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
                    var deadline_JDBC_string = textInput_deadline.text.toString()
                    if (strs_deadline.size != 0)
                    {
                        val deadline_day = strs_deadline[0]
                        val deadline_month = strs_deadline[1]
                        val deadline_year = strs_deadline[2]
                        deadline_JDBC_string = deadline_year +
                                "-" + deadline_month +
                                "-" + deadline_day +
                                " 00:01:02.345678901"
                    }

                    if (!gid.isBlank()) {
                        Goal(
                            gid = gid,
                            title = textInput_title.text.toString(),
                            category = categoriesList,
                            targetAmt = textInput_targetAmt.text.toString().toInt(),
                            currentAmt = textInput_currentAmt.text.toString().toInt(),
                            deadline = Timestamp.valueOf(deadline_JDBC_string)
                        ).updateByGid()
                    }

                    // Allow user to return to ShowGoalDetailsFragment?
//                    FragmentUtils(
//                        requireActivity().supportFragmentManager,
//                        R.id.fragment_container_goal_activity
//                    ).replace(ShowGoalDetailsFragment())
                    toast = CustomToast(requireContext())
                    toast.success("Goal Updated!")

                }
            }
        }

        // Open Dialog to Edit Deadline
        textInputLayout_deadline.setEndIconOnClickListener {
            val dialogFragment = EditDeadlinePickerFragment()

            // Add in bundle to pass current deadline to Dialog Fragment (calculation occurs in next Fragment)
            bundle.putString("goal_id", tv_goalID_edit_gone.text.toString())
            Log.d("gid to PickerFragment: ", tv_goalID_edit_gone.text.toString())
            bundle.putString("final_date", "default")

            dialogFragment.arguments = bundle

            // FragmentUtils does not support normal dialog
            fragmentUtils.replace(dialogFragment, backStackName = "secondaryStack")
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = gid).deleteByGid()

            // Redirect user to ShowGoalsFragment
            fragmentUtils.replace(ShowGoalsFragment())
        }
    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack (which is not 'secondaryStack')
            fragmentUtils.popBackStack(backStackName = "secondaryStack")
        }
    }
}