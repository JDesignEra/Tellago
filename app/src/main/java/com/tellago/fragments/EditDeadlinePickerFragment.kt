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
import com.google.type.DateTime
import com.tellago.R
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_deadline_picker.*
import kotlinx.android.synthetic.main.fragment_edit_deadline_picker.view.*
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class EditDeadlinePickerFragment : DialogFragment() {
    private lateinit var fragmentUtils: FragmentUtils
    lateinit var numPicker_year: NumberPicker
    lateinit var numPicker_month: NumberPicker
    lateinit var numPicker_day: NumberPicker

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

            val today_date = LocalDateTime.now()
            Log.d("today_date is: ", today_date.toString())
            val dateFormatter_year = DateTimeFormatter.ofPattern("yyyy")
            val dateFormatter_month = DateTimeFormatter.ofPattern("MM")
            val dateFormatter_day = DateTimeFormatter.ofPattern("dd")

            // Perform calculations from bundle arguments & then display values to relevant Pickers
            var currentDL_day = bundle.getString("day_toEdit")
            currentDL_day = singleDigitStringCheck(currentDL_day)

            var currentDL_month = bundle.getString("month_toEdit")
            currentDL_month = singleDigitStringCheck(currentDL_month)


            val currentDL_year = bundle.getString("year_toEdit")

            // Constructing deadline (future date) year-month-day-hour-minute-second format
            val futureDate = currentDL_year + "-" +
                    currentDL_month + "-" +
                    currentDL_day + "T00:01:02.987"

            Log.d("futureDate is: ", futureDate)

            val futureDate_LocalDateTime = LocalDateTime.parse(futureDate)
            Log.d("futureDate parsed: ", futureDate_LocalDateTime.toString())

            // Compare future date to today_date
            val diffInEpochSeconds =
                futureDate_LocalDateTime.toEpochSecond(ZoneOffset.UTC) - today_date.toEpochSecond(
                    ZoneOffset.UTC
                )
            Log.d("diffInEpochSeconds: ", diffInEpochSeconds.toString())

            // Divide by 31556926 seconds for 1 year = 365.24 days
            val secondsPerYear = 31556926
            val year_quotient = diffInEpochSeconds / secondsPerYear
            var remainder = diffInEpochSeconds % secondsPerYear

            view.numPick_year.value = year_quotient.toInt()

            // Divide by 2629743 seconds for 1 month = 30.44 days
            val secondsPerMonth = 2629743
            val month_quotient = remainder / secondsPerMonth
            remainder %= secondsPerMonth
            //remainder = remainder % secondsPerMonth

            view.numPick_month.value = month_quotient.toInt()

            // Divide by 86400 seconds for 1 day
            val secondsPerDay = 86400
            val day_quotient = remainder / secondsPerDay

            view.numPick_day.value = day_quotient.toInt()

            Log.d(
                "numPick values: d/m/y",
                view.numPick_day.value.toString() + "/" + view.numPick_month.value.toString() + "/" + view.numPick_year.value.toString()
            )

            // End of calculations. Displayed values to relevant Pickers


            // Perform calculations based on input values from Pickers
            btn_confirm_deadline_edit.setOnClickListener {
                val year_increase: Int = view.numPick_year.value
                val month_increase: Int = view.numPick_month.value
                val day_increase: Int = view.numPick_day.value
                Log.d("year_increase", year_increase.toString())

                // Conversion does not involve Calendar
                // DateTime --> DateTimeFormatter --> Int --> Increment --> Int --> String
                // Pass string using bundle to EditGoalDetailsFragment.kt


                var new_day = today_date.format(dateFormatter_day).toInt() + day_increase

                val day_offset: Int

                if (new_day > 31) {
                    new_day = new_day - 31
                    day_offset = 1
                    // there is spillover of day into the next month
                } else {
                    day_offset = 0
                }

                val month_offset: Int

                var new_month =
                    today_date.format(dateFormatter_month).toInt() + month_increase + day_offset
                if (new_month > 12) {
                    new_month = new_month - 12
                    month_offset = 1
                    // there is spillover of month into the next year
                } else {
                    month_offset = 0
                }

                val new_year =
                    today_date.format(dateFormatter_year).toInt() + year_increase + month_offset

                // Leap year check (based on value of new_year)
                val leap = when {
                    new_year % 4 == 0 -> {
                        when {
                            new_year % 100 == 0 -> new_year % 400 == 0
                            else -> true
                        }
                    }
                    else -> false
                }

                if (leap) {
                    if (new_month == 2) {
                        // check if output is 30, 31. If so, convert to equivalent date in March
                        val pair = FebToMarchDateLeap(new_month, new_day)
                        new_day = pair.first
                        new_month = pair.second
                        Log.d("adjust for LEAP FEB", "FIRED")
                    } else {
                        val pair = MonthCheck30Days(new_month, new_day)
                        new_day = pair.first
                        new_month = pair.second
                        Log.d("adjust for 30Day months", "FIRED")
                    }
                } else {
                    if (new_month == 2) {
                        // check if output is 29, 30, 31. If so, convert to equivalent date in March
                        val pair = FebToMarchDateNonLeap(new_month, new_day)
                        new_day = pair.first
                        new_month = pair.second
                        Log.d("adjust for non-LEAP Feb", "FIRED")
                    } else {
                        val pair = MonthCheck30Days(new_month, new_day)
                        new_day = pair.first
                        new_month = pair.second
                        Log.d("adjust for 30Day months", "FIRED")
                    }
                }


                Log.d("New Year", (new_year).toString())
                Log.d("New Month", (new_month).toString())
                Log.d("New Day", (new_day).toString())

                val final_date =
                    (new_day).toString() + "/" + (new_month).toString() + "/" + (new_year).toString()


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

    private fun MonthCheck30Days(
        new_month: Int,
        new_day: Int
    ): Pair<Int, Int> {
        var new_month1 = new_month
        var new_day1 = new_day
        if (new_month1 == 4 || new_month1 == 6 || new_month1 == 9 || new_month1 == 11) {
            if (new_day1 == 31) {
                new_month1 += 1
                new_day1 = 1
            } else if (new_day1 == 32) {
                new_month1 += 1
                new_day1 = 2
            }

        }

        return Pair(new_day1, new_month1)
    }


    private fun FebToMarchDateNonLeap(
        new_month: Int,
        new_day: Int
    ): Pair<Int, Int> {
        var new_month1 = new_month
        var new_day1 = new_day
        if (new_month1 == 2) {
            if (new_day1 == 29) {
                new_month1 = 3
                new_day1 = 1
            } else if (new_day1 == 30) {
                new_month1 = 3
                new_day1 = 2
            } else if (new_day1 == 31) {
                new_month1 = 3
                new_day1 = 3
            }
        }
        return Pair(new_day1, new_month1)
    }


    private fun FebToMarchDateLeap(
        new_month: Int,
        new_day: Int
    ): Pair<Int, Int> {
        var new_month1 = new_month
        var new_day1 = new_day
        if (new_month1 == 2) {
            if (new_day1 == 30) {
                new_month1 = 3
                new_day1 = 1
            } else if (new_day1 == 31) {
                new_month1 = 3
                new_day1 = 2
            }
        }
        return Pair(new_day1, new_month1)
    }

    private fun singleDigitStringCheck(currentDL_dayOrMonth: String?): String? {
        // this function assigns prefix of '0' to single-digit months

        var currentDL_value1 = currentDL_dayOrMonth
        when (currentDL_dayOrMonth) {
            "1" -> currentDL_value1 = "01"
            "2" -> currentDL_value1 = "02"
            "3" -> currentDL_value1 = "03"
            "4" -> currentDL_value1 = "04"
            "5" -> currentDL_value1 = "05"
            "6" -> currentDL_value1 = "06"
            "7" -> currentDL_value1 = "07"
            "8" -> currentDL_value1 = "08"
            "9" -> currentDL_value1 = "09"
            else -> {
                Log.d("input no change", "FIRED")
            }
        }
        return currentDL_value1
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