package com.tellago.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_categories_dialog.*
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.text.SimpleDateFormat
import java.util.*


class EditGoalDetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        var goalID: String = ""

        val bundle = this.arguments
        val categoriesList = mutableListOf<String>()
        if (bundle != null) {
            goalID = bundle.getString("goal_id").toString()

            if (bundle.getBoolean("career")) {
                categoriesList.add("career")
            }
            if (bundle.getBoolean("family")) {
                categoriesList.add("family")
            }
            if (bundle.getBoolean("leisure")) {
                categoriesList.add("leisure")
            }


            Goal(gid = goalID).getByGid {
                if (it != null) {
                    // Assign to relevant edit text elements below
                    tv_goalID_edit_gone.text = goalID
                    et_title.setText(it.title.toString())

                    // Reassigning value to tv_categories_edit if bundle with key 'update Categories' is not empty
                    if (bundle.getString("update Categories") == "updated") {
                        tv_categories_edit.text = categoriesList.toString()
                        Log.d("Reassigned cat edit", "FIRED")
                    } else {
                        Log.d("cat edit not assigned", "FIRED")
                        tv_categories_edit.text = it.category.toString()
                    }

                    et_targetAmt.setText(it.targetAmt.toString())
                    et_currentAmt.setText(it.currentAmt.toString())
                    // JID can be modified, but it will not be using Edit Text
                    // MaterialButton for JID?
                    et_bucketList.setText(it.bucketList.toString())
                    // Displaying deadline as DateTime rather than TimeStamp for user viewing
                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")
                    et_deadline.setText(dateFormatter.format(it.deadline).toString())
                    et_reminderMonthsFreq.setText(it.reminderMonthsFreq.toString())

                }
            }

        }

        // Open Categories List Dialog
        showEditCategoriesListDialog()

        btn_ConfirmEditGoalDetails.setOnClickListener {
            Log.d("Confirm Edit", "FIRED")
            // Assign "null" to categoriesList if it does not contain any elements
            if (categoriesList.count() == 0) {
                categoriesList.add("null")
                Log.d("catList was null", "FIRED")
            }

            // Proceed to update fields of the current document in Firestore
            if (goalID != "") {
                Goal(
                    gid = goalID,
                    title = et_title.text.toString(),
                    category = categoriesList,
                    targetAmt = et_targetAmt.text.toString().toInt(),
                    currentAmt = et_currentAmt.text.toString().toInt()

                ).updateByGid()
            }

        }

        btn_DeleteGoal.setOnClickListener {
            Log.d("Delete Goal", "FIRED")
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


    private fun showEditCategoriesListDialog() {
        btn_categories_edit.setOnClickListener {

            // Building Normal Dialog Fragment
            val dialogFragment = CategoriesDialogFragment()
            val bundle = Bundle()

            bundle.putString("goal_id", tv_goalID_edit_gone.text.toString())
            dialogFragment.arguments = bundle

            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            )
                .replace(dialogFragment)

        }
    }
}