package com.tellago.fragments


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.tellago.R
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_deadline_picker.*
import kotlinx.android.synthetic.main.fragment_edit_deadline_picker.view.*
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EditDeadlinePickerFragment : DialogFragment() {
    private lateinit var fragmentUtils: FragmentUtils
    lateinit var numPicker_year : NumberPicker
    lateinit var numPicker_month : NumberPicker
    lateinit var numPicker_day : NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    private fun wireWidgets() {
        numPicker_year = numPick_year
        numPicker_year.minValue = 0
        numPicker_year.maxValue = 35

        numPicker_month = numPick_month
        numPicker_month.minValue = 0
        numPicker_month.maxValue = 11

        numPicker_day = numPick_day
        numPicker_day.minValue = 0
        numPicker_day.maxValue = 28 // max value is 28 to protect DateTimeFormat conversion logic

        // Perform calculations from bundle arguments & then display values to relevant Pickers
        //numPicker_year.value =
        //numPicker_month.value =
        //numPicker_day.value =
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_deadline_picker, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        configureToolbar()
        wireWidgets()

        val bundle = this.arguments
        if (bundle != null) {

            tv_datechangedisplay_gone.text = bundle.getString("goal_id").toString()

            // Perform calculations based on input values from Pickers
            btn_confirm_deadline_edit.setOnClickListener {
                val year_increase : Int = view.numPick_year.value
                val month_increase : Int = view.numPick_month.value
                val day_increase : Int = view.numPick_day.value
                Log.d("year_increase", year_increase.toString())

                // Conversion does not involve Calendar
                // DateTime --> DateTimeFormatter --> Int --> Increment --> Int --> String
                // Pass string using bundle to EditGoalDetailsFragment.kt

                val new_date = LocalDateTime.now()
                val dateFormatter_year = DateTimeFormatter.ofPattern("yyyy")
                val dateFormatter_month = DateTimeFormatter.ofPattern("MM")
                val dateFormatter_day = DateTimeFormatter.ofPattern("dd")


                var new_day = new_date.format(dateFormatter_day).toInt() + day_increase

                var day_offset : Int

                if (new_day > 31) {
                    new_day = new_day - 31
                    day_offset = 1
                    // there is spillover of day into the next month
                }
                else {
                    day_offset = 0
                }

                var month_offset : Int

                var new_month = new_date.format(dateFormatter_month).toInt() + month_increase + day_offset
                if (new_month > 12) {
                    new_month = new_month - 12
                    month_offset = 1
                    // there is spillover of month into the next year
                }
                else
                {
                    month_offset = 0
                }

                val new_year = new_date.format(dateFormatter_year).toInt() + year_increase + month_offset

                Log.d("New Year", (new_year).toString())
                Log.d("New Month", (new_month).toString())
                Log.d("New Day", (new_day).toString())

                var final_date = (new_day).toString() + "/" + (new_month).toString() + "/" + (new_year).toString()


                bundle.putString("goal_id", tv_datechangedisplay_gone.text.toString())
                bundle.putString("update Categories", "no change")
                bundle.putString("final_date", final_date)

                val editGoalDetailsFragment = EditGoalDetailsFragment()

                editGoalDetailsFragment.arguments = bundle

                FragmentUtils(
                    requireActivity().supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(editGoalDetailsFragment)
            }
        }
    }

//    private fun configureToolbar() {
//        toolbar_edit_deadline_picker.setNavigationIcon(R.drawable.ic_cancel_grey_48)
//        toolbar_edit_deadline_picker.setNavigationOnClickListener {
//            // Allow user to return to previous fragment in the Stack (which is not 'secondaryStack')
//            fragmentUtils.popBackStack()
//            //FragmentUtils().popBackStack(backStackName = "secondaryStack")
//        }
//    }

}