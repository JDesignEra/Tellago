package com.tellago.interfaces

import androidx.fragment.app.Fragment

interface GoalsCommunicator {
    var requestKey: String
    val titleKey: String
    val careerKey: String
    val familyKey: String
    val leisureKey: String
    val durationKey: String
    val reminderKey: String

    fun replaceFragment(fragment: Fragment)
    fun popBackFragment()
}