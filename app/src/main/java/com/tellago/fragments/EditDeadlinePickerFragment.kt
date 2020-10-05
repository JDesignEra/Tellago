package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import com.tellago.utilities.NumPickerUtils
import kotlinx.android.synthetic.main.fragment_edit_deadline_picker.*
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*

class EditDeadlinePickerFragment : DialogFragment() {
    private lateinit var goal: Goal
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var numPickerUtils: NumPickerUtils

    private var bundle: Bundle? = null
    val today = LocalDate.now()

//    private lateinit var numPicker_year: NumberPicker
//    private lateinit var numPicker_month: NumberPicker
//    private lateinit var numPicker_day: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        numPickerUtils = NumPickerUtils(requireContext())
    }

//    private fun wireWidgets() {
//        numPicker_year = numPick_year
//        numPicker_year.minValue = 0
//        numPicker_year.maxValue = 35
//
//        numPicker_month = numPick_month
//        numPicker_month.minValue = 0
//        numPicker_month.maxValue = 11
//
//        numPicker_day = numPick_day
//        numPicker_day.minValue = 0
//        numPicker_day.maxValue = 28 // max value is 28 to protect DateTimeFormat conversion logic
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_deadline_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numPick_year.minValue = 0
        numPick_year.maxValue = 35

        numPick_month.minValue = 0
        numPick_month.maxValue = Calendar.getInstance().getMaximum(Calendar.MONTH)

        numPick_day.minValue = 0
        numPick_day.maxValue = Calendar.getInstance().getMaximum(Calendar.DAY_OF_MONTH) - 1

        val deadline = Instant.ofEpochMilli(goal.deadline.time).atZone(ZoneId.systemDefault()).toLocalDate()
        val dateDiff = Period.between(today, deadline)

        numPick_year.value = dateDiff.years
        numPick_month.value = dateDiff.months
        numPick_day.value = dateDiff.days

        btn_confirm_deadline_edit.setOnClickListener {
            var deadline = LocalDate.now()
            deadline = deadline.plusDays(numPick_day.value.toLong())
            deadline = deadline.plusMonths(numPick_month.value.toLong())
            deadline = deadline.plusYears(numPick_year.value.toLong())

            goal.deadline = Calendar.getInstance().apply {
                time = Date.from(deadline.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                set(Calendar.MILLISECOND, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.HOUR, 0)
            }.time

            val intent = Intent(requireContext(), this::class.java).apply {
                putExtra(Goal::class.java.name, goal)
            }

            targetFragment?.onActivityResult(0, Activity.RESULT_OK, intent)
            fragmentUtils.popBackStack()
        }

        numPick_year.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue) {
                if (numPick_month.value < numPick_month.maxValue) {
                    numPickerUtils.animateValueByOne(numPick_month)
                    numPick_day.value = numPick_month.minValue
                }
                else if (numPick_day.value < numPick_day.maxValue) {
                    numPickerUtils.animateValueByOne(numPick_day)
                }
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue) {
                if (numPick_month.value > numPick_month.minValue) {
                    numPickerUtils.animateValueByOne(numPick_month, false)
                    numPick_day.value = numPick_month.minValue
                }
                else if (numPick_day.value > numPick_day.minValue) {
                    numPickerUtils.animateValueByOne(numPick_day, false)
                }
            }
        }

        numPick_month.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue && numPick_year.value > numPick_year.minValue) {
                numPickerUtils.animateValueByOne(numPick_year)
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue && numPick_year.value < numPick_year.maxValue) {
                numPickerUtils.animateValueByOne(numPick_year)
            }
        }

        numPick_day.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == picker.minValue && oldVal == picker.maxValue) {
                if (numPick_month.value < numPick_month.maxValue) {
                    numPickerUtils.animateValueByOne(numPick_month)
                }
                else if (numPick_year.value < numPick_year.maxValue) {
                    numPickerUtils.animateValueByOne(numPick_year)
                    numPick_month.value = numPick_month.minValue
                }
            }

            if (newVal == picker.maxValue && oldVal == picker.minValue) {
                if (numPick_month.value > numPick_month.minValue) {
                    numPickerUtils.animateValueByOne(numPick_month, false)
                }
                else if (numPick_year.value > numPick_year.minValue) {
                    numPickerUtils.animateValueByOne(numPick_year, false)
                    numPick_month.value = numPick_month.maxValue
                }
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        configureToolbar()
//        wireWidgets()
//
//        val bundle = this.arguments
//        if (bundle != null) {
//            val today_date = LocalDateTime.now()
//            Log.d("today_date is: ", today_date.toString())
////            val dateFormatter_year = DateTimeFormatter.ofPattern("yyyy")
////            val dateFormatter_month = DateTimeFormatter.ofPattern("MM")
////            val dateFormatter_day = DateTimeFormatter.ofPattern("dd")
//
//            // Perform calculations from bundle arguments & then display values to relevant Pickers
//            var currentDL_day = bundle.getString("day_toEdit")
//            currentDL_day = singleDigitStringCheck(currentDL_day)
//
//            var currentDL_month = bundle.getString("month_toEdit")
//            currentDL_month = singleDigitStringCheck(currentDL_month)
//
//
//            val currentDL_year = bundle.getString("year_toEdit")
//
//            // Constructing deadline (future date) year-month-day-hour-minute-second format
//            val futureDate = currentDL_year + "-" +
//                    currentDL_month + "-" +
//                    currentDL_day + "T00:01:02.987"
//
//            Log.d("futureDate is: ", futureDate)
//
//            val futureDate_LocalDateTime = LocalDateTime.parse(futureDate)
//            Log.d("futureDate parsed: ", futureDate_LocalDateTime.toString())
//
//            // Compare future date to today_date
//            val diffInEpochSeconds =
//                futureDate_LocalDateTime.toEpochSecond(ZoneOffset.UTC) -
//                        today_date.toEpochSecond(ZoneOffset.UTC)
//            Log.d("diffInEpochSeconds: ", diffInEpochSeconds.toString())
//
//            // Divide by 31556926 seconds for 1 year = 365.24 days
//            val secondsPerYear = 31556926
//            val year_quotient = diffInEpochSeconds / secondsPerYear
//            var remainder = diffInEpochSeconds % secondsPerYear
//
//            view.numPick_year.value = year_quotient.toInt()
//
//            // Divide by 2629743 seconds for 1 month = 30.44 days
//            val secondsPerMonth = 2629743
//            val month_quotient = remainder / secondsPerMonth
//            remainder %= secondsPerMonth
//            //remainder = remainder % secondsPerMonth
//
//            view.numPick_month.value = month_quotient.toInt()
//
//            // Divide by 86400 seconds for 1 day
//            val secondsPerDay = 86400
//            val day_quotient = remainder / secondsPerDay
//
//            view.numPick_day.value = day_quotient.toInt()
//
//            Log.d(
//                "numPick values: d/m/y",
//                view.numPick_day.value.toString() + "/" + view.numPick_month.value.toString() + "/" + view.numPick_year.value.toString()
//            )
//
//            // End of calculations. Displayed values to relevant Pickers
//
//
//            // Perform calculations based on input values from Pickers
//            btn_confirm_deadline_edit.setOnClickListener {
//                val year_increase: Int = view.numPick_year.value
//                val month_increase: Int = view.numPick_month.value
//                val day_increase: Int = view.numPick_day.value
//
//                // Conversion does not involve Calendar
//                // DateTime --> DateTimeFormatter --> Int --> Increment --> Int --> String
//                // Pass string using bundle to EditGoalDetailsFragment.kt
//
//                // Convert days to epoch seconds
//                var epochSecondTotal = day_increase * secondsPerDay
//
//                // Convert months to epoch seconds & add to running total
//                epochSecondTotal += month_increase * secondsPerMonth
//
//                // Convert years to epoch seconds & add to running total
//                epochSecondTotal += year_increase * secondsPerYear
//
////                val updatedDeadlineInEpochSeconds =
////                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + epochSecondTotal
//
//                val updatedDeadlineInEpochSeconds =
//                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + epochSecondTotal.toLong()
//
//
//                // Obtain date in dd-MM-yyyy based on EpochSeconds
//
//                // Divide by 31556926 seconds for 1 year = 365.24 days
//                // updatedDeadlineInEpochSeconds should be LONG
//                // new_remainder should be LONG
//                // new_year_quotient & all following quotients should be INT
//                var new_year_quotient = (updatedDeadlineInEpochSeconds / secondsPerYear).toInt()
//                var new_remainder = updatedDeadlineInEpochSeconds % secondsPerYear
//
//                // Divide by 2629743 seconds for 1 month = 30.44 days
//                var new_month_quotient = (new_remainder / secondsPerMonth).toInt()
//                new_remainder %= secondsPerMonth
//
//                // Divide by 86400 seconds for 1 day
//                var new_day_quotient = (new_remainder / secondsPerDay).toInt()
//
//
//                // Use new quotients' value as day_increase OR month_increase OR year_increase
//                // The new_quotient values are in EPOCH seconds (time since 1 January 1970)
//                // add offset for year, month & day to obtain final Date
//                new_year_quotient += 1970
//                new_month_quotient += 1
//                new_day_quotient += 1
//
//
//                // Leap year check (based on value of new_year)
//                val leap = when {
//                    new_year_quotient % 4 == 0 -> {
//                        when {
//                            new_year_quotient % 100 == 0 -> new_year_quotient % 400 == 0
//                            else -> true
//                        }
//                    }
//                    else -> false
//                }
//
//                if (leap) {
//                    if (new_month_quotient == 2) {
//                        // check if output is 30, 31. If so, convert to equivalent date in March
//                        val pair = FebToMarchDateLeap(new_month_quotient, new_day_quotient)
//                        new_day_quotient = pair.first
//                        new_month_quotient = pair.second
//                        Log.d("adjust for LEAP FEB", "FIRED")
//                    } else {
//                        val pair = MonthCheck30Days(new_month_quotient, new_day_quotient)
//                        new_day_quotient = pair.first
//                        new_month_quotient = pair.second
//                        Log.d("adjust for 30Day months", "FIRED")
//                    }
//                } else {
//                    if (new_month_quotient == 2) {
//                        // check if output is 29, 30, 31. If so, convert to equivalent date in March
//                        val pair = FebToMarchDateNonLeap(new_month_quotient, new_day_quotient)
//                        new_day_quotient = pair.first
//                        new_month_quotient = pair.second
//                        Log.d("adjust for non-LEAP Feb", "FIRED")
//                    } else {
//                        val pair = MonthCheck30Days(new_month_quotient, new_day_quotient)
//                        new_day_quotient = pair.first
//                        new_month_quotient = pair.second
//                        Log.d("adjust for 30Day months", "FIRED")
//                    }
//                }
//
////                Log.d("New Year", (new_year_quotient).toString())
////                Log.d("New Month", (new_month_quotient).toString())
////                Log.d("New Day", (new_day_quotient).toString())
//
//                val finalDate = (new_day_quotient).toString() + "/" + (new_month_quotient).toString() + "/" + (new_year_quotient).toString()
//
//                val intent = Intent(requireContext(), this::class.java).apply {
//                    putExtra("final_date", finalDate)
//                }
//
//                targetFragment?.onActivityResult(0, Activity.RESULT_OK, intent)
//
//                fragmentUtils.popBackStack()
//            }
//        }
//    }

    private fun MonthCheck30Days(
        new_month: Int,
        new_day: Int
    ): Pair<Int, Int> {
        var new_month1 = new_month
        var new_day1 = new_day

        if (new_month1 == 4 || new_month1 == 6|| new_month1 == 9 || new_month1 == 11) {
            if (new_day1 == 31) {
                new_month1 += 1
                new_day1 = 1
            } else if (new_day1 == 32) {
                new_month1 += 1
                new_day1 = 2
            }

        }

        return Pair(new_day1.toInt(), new_month1.toInt())
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
        return Pair(new_day1.toInt(), new_month1.toInt())
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
}