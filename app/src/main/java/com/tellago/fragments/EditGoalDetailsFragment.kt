package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    private lateinit var toast: CustomToast

    override fun onStart() {
        super.onStart()

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

                textView_deadline.text = deadline
                textInput_reminderFreq.setText((it.reminderMonthsFreq ?: 0).toString())

                val categoriesList = it.category ?: ArrayList()

                if (categoriesList.contains("career")) btnToggleGrp_category.check(R.id.btn_careerCategory)
                if (categoriesList.contains("family")) btnToggleGrp_category.check(R.id.btn_familyCategory)
                if (categoriesList.contains("leisure")) btnToggleGrp_category.check(R.id.btn_leisureCategory)

                btnToggleGrp_category.addOnButtonCheckedListener { _, checkedId, isChecked ->
                    val idStrings = mapOf(
                        R.id.btn_careerCategory to "career",
                        R.id.btn_familyCategory to "family",
                        R.id.btn_leisureCategory to "leisure"
                    )

                    if (isChecked) {
                        if (!categoriesList.contains(idStrings[checkedId])) {
                            idStrings[checkedId]?.let { categoriesList.add(it) }
                        }
                    }
                    else categoriesList.remove(idStrings[checkedId])
                }

                btn_ConfirmEditGoalDetails.setOnClickListener {
                    // Converting deadline from dd-MM-yyyy to JDBC timestamp format
                    val strs_deadline = textView_deadline.text.toString().split("/").toTypedArray()
                    // There will be 3 elements in the strs_deadline ArrayList (internal conversion)
                    var deadline_JDBC_string = textView_deadline.text.toString()
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
        btn_deadline_edit.setOnClickListener {
            // Building Normal Dialog Fragment
            val dialogFragment = EditDeadlinePickerFragment()

            // Add in bundle to pass current deadline to Dialog Fragment (calculation occurs in next Fragment)
            bundle.putString("goal_id", tv_goalID_edit_gone.text.toString())
            Log.d("gid to PickerFragment: ", tv_goalID_edit_gone.text.toString())
            bundle.putString("final_date", "default")

            dialogFragment.arguments = bundle

            // FragmentUtils does not support normal dialog
            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            ).replace(dialogFragment)
        }


        btn_DeleteGoal.setOnClickListener {
            Goal(gid = gid).deleteByGid()

            // Redirect user to ShowGoalsFragment
            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            ).replace(ShowGoalsFragment())

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

}