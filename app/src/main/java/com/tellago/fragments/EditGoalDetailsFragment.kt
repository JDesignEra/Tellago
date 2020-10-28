package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.text.SimpleDateFormat
import java.util.*

class EditGoalDetailsFragment : Fragment() {
    private var bundle: Bundle? = null
    private lateinit var goal: Goal

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
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
        return inflater.inflate(R.layout.fragment_edit_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        textInput_title.setText(goal.title)
        textInput_targetAmt.setText(String.format("%.2f", goal.targetAmt))
        //textInput_currentAmt.setText(String.format("%.2f", goal.currentAmt))

        if (goal.completed) {
            textInput_targetAmt.visibility = View.GONE
      //      linear_layout_current_amount_edit_goal_details.visibility = View.GONE
            linear_layout_target_duration_edit_goal_details.visibility = View.GONE
            linear_layout_reminder_frequency_edit_goal_details.visibility = View.GONE
        }

        if (goal.reminderMonthsFreq == 3) radioBtn_3MonthsReminder.isChecked = true
        else if (goal.reminderMonthsFreq == 6) radioBtn_6MonthsReminder.isChecked = true

        if (goal.categories.contains("career")) categories_toggleGrp_editGoalDetails.check(category_btn_1.id)
        if (goal.categories.contains("family")) categories_toggleGrp_editGoalDetails.check(category_btn_2.id)
        if (goal.categories.contains("leisure")) categories_toggleGrp_editGoalDetails.check(category_btn_3.id)


        constraint_layout_edit_goal_details.setOnClickListener {
            val errors = mutableMapOf<String, String>()
            val moneyRegex = Regex("\\d+?\\.\\d{3,}")

            textInput_title.error = null
            textInput_targetAmt.error = null
//            textInput_currentAmt.error = null

            if (textInput_title.text.isNullOrBlank()) errors["title"] = "Field is required"
//            when {
//                textInput_targetAmt.text.isNullOrBlank() -> errors["targetAmt"] = "Field is required"
//                textInput_targetAmt.text.toString().toDouble() < 0.01 -> errors["targetAmt"]  = "Needs to be more than 0"
//                moneyRegex.matches(textInput_targetAmt.text.toString()) -> errors["targetAmt"] = "Cents can't be more than 2 digits"
//                textInput_targetAmt.text.toString().toDouble() < textInput_currentAmt.text.toString().toDouble()-> errors["targetAmt"] = "Field has to more or same as Current Monetary Value"
//            }
            when {
                textInput_targetAmt.text.isNullOrBlank() -> errors["targetAmt"] = "Field is required"
//                textInput_currentAmt.text.isNullOrBlank() -> errors["currentAmt"] = "Field is required"
                moneyRegex.matches(textInput_targetAmt.text.toString()) -> errors["targetAmt"] = "Cents can't be more than 2 digits"
//                moneyRegex.matches(textInput_currentAmt.text.toString()) -> errors["currentAmt"] = "Cents can't be more than 2 digits"
//                textInput_currentAmt.text.toString().toDouble() > textInput_targetAmt.text.toString().toDouble() -> errors["currentAmt"] = "Field has to less or same as Target Monetary Value"
            }

            if (errors.isNotEmpty()) {
                errors["title"].let { textInput_title.error = it }
                errors["targetAmt"].let { textInput_targetAmt.error = it }
//                errors["currentAmt"].let { textInput_currentAmt.error = it }
            }
            else if (!goal.gid?.isBlank()!!) {
                updateGoalModel()

                goal.setByGid {
                    if (it != null) {
                        fragmentUtils.popBackStack()
                        toast.success("Goal updated")
                    }
                    else toast.error("Please try again, there was an issue when updating your goal")
                }
            }
        }

        linear_layout_edit_deadline.setOnClickListener {
            val dialogFragment = EditDeadlinePickerFragment()
            updateGoalModel()

            dialogFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(
                dialogFragment,
                setTargetFragment = this,
                requestCode = 0,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit
            )
        }
    }

    override fun onStart() {
        super.onStart()

        textView_deadline.setText(dateFormatter.format(goal.deadline))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Goal>(Goal::class.java.name).let {
                goal = it!!
            }
        }
    }

    private fun updateGoalModel() {
        goal.title = textInput_title.text.toString()
        goal.targetAmt = textInput_targetAmt.text.toString().toDouble()
//        goal.currentAmt = textInput_currentAmt.text.toString().toDouble()
        goal.deadline = dateFormatter.parse(textView_deadline.text.toString())!!
        goal.reminderMonthsFreq = when {
            radioBtn_3MonthsReminder.isChecked -> 3
            radioBtn_6MonthsReminder.isChecked -> 6
            else -> 0
        }



        val categories = goal.categories

        // Get unique IDs of buttons in categories_toggleGrp_editGoalDetails
        val uniqueBtnIDList : ArrayList<Int> = ArrayList()
        val toggleGrpButtonCount = categories_toggleGrp_editGoalDetails.childCount
        for (index in 0 .. (toggleGrpButtonCount - 1))
        {
            uniqueBtnIDList.add(categories_toggleGrp_editGoalDetails[index].id)
        }


        // reset categories
        categories.clear()
        for (btnId in categories_toggleGrp_editGoalDetails.checkedButtonIds)
        {
            // use r.id_int instead of unique ID for the buttons
            if (btnId == categories_toggleGrp_editGoalDetails[0].id) categories.add("career")
            if (btnId == categories_toggleGrp_editGoalDetails[1].id) categories.add("family")
            if (btnId == categories_toggleGrp_editGoalDetails[2].id) categories.add("leisure")
        }


        // reassign updated categories to goal.categories
        goal.categories = categories


    }

    private fun configureToolbar() {
        toolbar_edit_goal_details.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_edit_goal_details.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }
}