package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        requireArguments().getParcelable<Goal>("Goal")?.let {
            initElementStates(it, requireArguments())
        }

        setFragmentResultListener("deadlinePicker") { _, bundle ->
            bundle.getParcelable<Goal>("Goal")?.let {
                initElementStates(it, bundle)
            }
        }
    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

    private fun initElementStates(goal: Goal, bundle: Bundle) {
        // Assign to relevant edit text elements below
        textInput_title.setText(goal.title.toString())
        textInput_targetAmt.setText(goal.targetAmt.toString())
        textInput_currentAmt.setText(goal.currentAmt.toString())

        if (goal.reminderMonthsFreq == 3) radioBtn_3MonthsReminder.isChecked = true
        else if (goal.reminderMonthsFreq == 6) radioBtn_6MonthsReminder.isChecked = true

        val deadline = if (goal.deadline != null) {
            // Assign based on Firestore field value if bundle key 'final_date' has "default" value
            if (bundle.getString("final_date") == "default") {
                dateFormatter.format(goal.deadline)
            }
            else {
                // Reassign value to textInput_deadline if bundle key 'final_date' is not "default"
                bundle.getString("final_date")
            }
        } else  ""

        textInput_deadline.setText(deadline)

        val categoriesList = goal.category ?: ArrayList()

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


            if (!goal.gid?.isBlank()!!) {
                Goal(
                    gid = goal.gid,
                    title = textInput_title.text.toString(),
                    category = categoriesList,
                    targetAmt = textInput_targetAmt.text.toString().toInt(),
                    currentAmt = textInput_currentAmt.text.toString().toInt(),
                    deadline = Timestamp.valueOf(convertToJdbc(textInput_deadline.text.toString()))
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

        // Open Dialog to Edit Deadline
        textInputLayout_deadline.setEndIconOnClickListener {
            val dialogFragment = EditDeadlinePickerFragment()

            val strs_deadline_toEdit = textInput_deadline.text.toString().split("/").toTypedArray()
            // There will be 3 elements in the strs_deadline ArrayList (internal conversion)

            if (strs_deadline_toEdit.size != 0) {
                val deadline_day_toEdit = strs_deadline_toEdit[0]
                val deadline_month_toEdit = strs_deadline_toEdit[1]
                val deadline_year_toEdit = strs_deadline_toEdit[2]

                val goal = Goal(
                    gid = goal.gid,
                    title = textInput_title.text.toString(),
                    category = categoriesList,
                    targetAmt = textInput_targetAmt.text.toString().toInt(),
                    currentAmt = textInput_currentAmt.text.toString().toInt(),
                    deadline = Timestamp.valueOf(convertToJdbc(textInput_deadline.text.toString()))
                )

                bundle.putParcelable("Goal", goal)

                // Add in bundle to pass current deadline to Dialog Fragment (calculation occurs in next Fragment)
                bundle.putString("final_date", "default")
                bundle.putString("day_toEdit", deadline_day_toEdit)
                bundle.putString("month_toEdit", deadline_month_toEdit)
                bundle.putString("year_toEdit", deadline_year_toEdit)

                dialogFragment.arguments = bundle

                // FragmentUtils does not support normal dialog
                fragmentUtils.replace(dialogFragment)

            }
            else toast.error("Unable to edit Deadline")
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = goal.gid).deleteByGid()
            fragmentUtils.replace(ShowGoalsFragment())
        }
    }

    private fun convertToJdbc(string_deadline: String): String {
        // Converting deadline from dd-MM-yyyy to JDBC timestamp format
        val strs_deadline = string_deadline.split("/").toTypedArray()
        // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
        var deadline_JDBC_string = string_deadline

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

        return deadline_JDBC_string
    }
}