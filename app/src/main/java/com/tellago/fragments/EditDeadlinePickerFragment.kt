package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import com.tellago.utilities.NumPickerUtils
import kotlinx.android.synthetic.main.fragment_edit_deadline_picker.*
import java.time.*
import java.util.*

class EditDeadlinePickerFragment : DialogFragment() {
    private lateinit var goal: Goal
    lateinit var localDeadline: LocalDate
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var numPickerUtils: NumPickerUtils
    private lateinit var toast: CustomToast

    private var bundle: Bundle? = null
    private val today = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        localDeadline = Instant.ofEpochMilli(goal.deadline.time).atZone(ZoneId.systemDefault()).toLocalDate()
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        numPickerUtils = NumPickerUtils(requireContext())
        toast = CustomToast(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_deadline_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numPick_year.minValue = 0
        numPick_year.maxValue = 35

        numPick_month.minValue = 0
        numPick_month.maxValue = 11

        numPick_day.minValue = 0
        numPick_day.maxValue = YearMonth.of(localDeadline.year, localDeadline.month).lengthOfMonth() - 1

        val dateDiff = Period.between(today, localDeadline)

        numPick_year.value = dateDiff.years
        numPick_month.value = dateDiff.months
        numPick_day.value = dateDiff.days

        btn_confirm_deadline_edit.setOnClickListener {
            var deadline = LocalDate.now()
            deadline = deadline.plusYears(numPick_year.value.toLong())
            deadline = deadline.plusMonths(numPick_month.value.toLong())
            deadline = deadline.plusDays(numPick_day.value.toLong())

            goal.deadline = Calendar.getInstance().apply {
                time = Date.from(deadline.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                set(Calendar.MILLISECOND, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.HOUR, 0)
            }.time

            goal.setByGid {
                if (it != null) {
                    toast.success("Deadline updated successfully")

                    val intent = Intent(requireContext(), this::class.java).apply {
                        putExtra(goal::class.java.name, goal)
                    }

                    targetFragment?.onActivityResult(0, Activity.RESULT_OK, intent)
                    fragmentUtils.popBackStack()
                }
                else toast.error("Please try again, failed to update deadline")
            }
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
}