package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.text.SimpleDateFormat
import java.util.*


class EditGoalDetailsFragment : Fragment() {
    private var bundle: Bundle? = null
    private var goal = Goal()

    private val locale = Locale("en", "SG")
    private val timezone = TimeZone.getTimeZone("Asia/Singapore")
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", locale).apply {
        timeZone = timezone
    }

    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
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

        initElementStates(goal, requireArguments())

        setFragmentResultListener(EditDeadlinePickerFragment::class.java.name) { _, bundle ->
            bundle.getParcelable<Goal>(goal::class.java.name)?.let {
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

    private fun initElementStates(goalParam: Goal, bundleParam: Bundle) {
        // Assign to relevant edit text elements below
        textInput_title.setText(goalParam.title)
        textInput_targetAmt.setText(String.format("%.2f", goalParam.targetAmt))
        textInput_currentAmt.setText(String.format("%.2f", goalParam.currentAmt))
        textInput_deadline.setText(dateFormatter.format(goalParam.deadline))

        if (goalParam.reminderMonthsFreq == 3) radioBtn_3MonthsReminder.isChecked = true
        else if (goalParam.reminderMonthsFreq == 6) radioBtn_6MonthsReminder.isChecked = true

        val categoriesList = goalParam.categories

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
            if (!goalParam.gid?.isBlank()!!) {
                goal.title = textInput_title.text.toString()
                goal.categories = categoriesList
                goal.targetAmt = textInput_targetAmt.text.toString().toDouble()
                goal.currentAmt = textInput_currentAmt.text.toString().toDouble()
                goal.deadline = Calendar.getInstance(timezone, locale).apply {
                    time = dateFormatter.parse(textInput_deadline.text.toString())!!
                }.time

                goalParam.setByGid {
                    if (it != null) {
                        setFragmentResult(this::class.java.name, Bundle().apply {
                            putParcelable(goal::class.java.name, goal)
                        })

                        fragmentUtils.popBackStack()
                        toast.success("Goal updated")
                    }
                    else toast.error("Please try again, there was an issue when updating your goal")
                }
            }
        }

        textInputLayout_deadline.setEndIconOnClickListener {
            val dialogFragment = EditDeadlinePickerFragment()

            goal.title = textInput_title.text.toString()
            goal.categories = categoriesList
            goal.targetAmt = textInput_targetAmt.text.toString().toDouble()
            goal.currentAmt = textInput_currentAmt.text.toString().toDouble()
            goal.deadline = Calendar.getInstance(timezone, locale).apply {
                time = dateFormatter.parse(textInput_deadline.text.toString())!!
            }.time
            goal.reminderMonthsFreq = when {
                radioBtn_3MonthsReminder.isChecked -> 3
                radioBtn_6MonthsReminder.isChecked -> 6
                else -> 0
            }

            dialogFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(dialogFragment)
        }

        // Open Dialog to Edit Deadline
//        textInputLayout_deadline.setEndIconOnClickListener {
//            val dialogFragment = EditDeadlinePickerFragment()
//            val strs_deadline_toEdit = textInput_deadline.text.toString()
//                .split("/").toTypedArray()
//
//            // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
//            if (strs_deadline_toEdit.size != 0) {
//                val deadline_day_toEdit = strs_deadline_toEdit[0]
//                val deadline_month_toEdit = strs_deadline_toEdit[1]
//                val deadline_year_toEdit = strs_deadline_toEdit[2]
//
//                bundleParam.putParcelable("Goal",
//                    Goal(
//                        gid = goalParam.gid,
//                        title = textInput_title.text.toString(),
//                        categories = categoriesList,
//                        targetAmt = textInput_targetAmt.text.toString().toDouble(),
//                        currentAmt = textInput_currentAmt.text.toString().toDouble(),
//                        deadline = Timestamp.valueOf(convertToJdbc(textInput_deadline.text.toString()))
//                    )
//                )
//
//                // Add in bundle to pass current deadline to Dialog Fragment (calculation occurs in next Fragment)
//                bundleParam.putString("final_date", "default")
//                bundleParam.putString("day_toEdit", deadline_day_toEdit)
//                bundleParam.putString("month_toEdit", deadline_month_toEdit)
//                bundleParam.putString("year_toEdit", deadline_year_toEdit)
//
//                dialogFragment.arguments = bundleParam
//
//                // FragmentUtils does not support normal dialog
//                fragmentUtils.replace(dialogFragment)
//
//            }
//            else toast.error("Unable to edit Deadline")
//        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = goalParam.gid).deleteByGid()
            fragmentUtils.replace(ShowGoalsFragment())
        }
    }

//    private fun convertToJdbc(string_deadline: String): String {
//        // Converting deadline from dd-MM-yyyy to JDBC timestamp format
//        val strs_deadline = string_deadline.split("/").toTypedArray()
//        // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
//        var deadline_JDBC_string = string_deadline
//
//        if (strs_deadline.size != 0)
//        {
//            val deadline_day = strs_deadline[0]
//            val deadline_month = strs_deadline[1]
//            val deadline_year = strs_deadline[2]
//
//            deadline_JDBC_string = deadline_year +
//                    "-" + deadline_month +
//                    "-" + deadline_day +
//                    " 00:01:02.345678901"
//        }
//
//        return deadline_JDBC_string
//    }
}