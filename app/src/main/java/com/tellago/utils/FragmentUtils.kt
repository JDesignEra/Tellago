package com.tellago.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.tellago.R

class FragmentUtils(
    private val fragmentManager: FragmentManager,
    private val fragmentContainer: Int
) {
    fun add(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        transaction.replace(fragmentContainer, fragment)
        transaction.addToBackStack(null);
        transaction.commit()
    }

    fun replace(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        transaction.setCustomAnimations(
            R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_right_enter,
            R.anim.fragment_slide_right_exit
        )

        transaction.replace(fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun addFragmentToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        transaction.replace(fragmentContainer, fragment)
        transaction.addToBackStack("fragmentStack1")
        transaction.commit()
    }


}