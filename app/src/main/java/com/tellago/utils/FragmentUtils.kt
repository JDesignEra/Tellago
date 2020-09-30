package com.tellago.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.tellago.R

/**
 * Instantiate once and use through out that Activity or Fragment, instead of
 * re-typing or re-copying same functions.
 * @param fragmentManager pass FragmnetManager
 * @param fragmentContainer container for fragment to be place into
 */
class FragmentUtils(
    private val fragmentManager: FragmentManager,
    private val fragmentContainer: Int
) {
    /**
     * Recommended to does not require fragment to be able to return to previous fragment,
     * if requires fragment to be bale to return to previous fragment use **.replace()** instead.
     * @param fragment add targeted fragment
     */
    fun add(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        transaction.replace(fragmentContainer, fragment)
        transaction.commit()
    }

    /**
     * Recommended if requires fragment to be able to return to previous fragment,
     * uses **addToBackStack("mainStack:)**.
     * @param fragment replace with targeted Fragment.
     * @param animate Enable or disable animation with *true* or *false*, defaults to **true**.
     * @param enter enter animation, defaults to **R.anim.fragment_slide_left_enter**.
     * @param exit exit animation, defaults to **R.anim.fragment_slide_left_exit**.
     * @param popEnter popEnter animation, defaults to **R.anim.fragment_slide_right_enter**.
     * @param popExit popExit animation, defaults to **R.anim.fragment_slide_right_exit**.
     */
    fun replace(fragment: Fragment,  animate: Boolean = true, enter: Int? = null, exit: Int? = null, popEnter: Int? = null, popExit: Int? = null) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        if (animate) {
            transaction.setCustomAnimations(
                enter ?: R.anim.fragment_slide_left_enter,
                exit ?: R.anim.fragment_slide_left_exit,
                popEnter ?: R.anim.fragment_slide_right_enter,
                popExit ?: R.anim.fragment_slide_right_exit
            )
        }

        transaction.replace(fragmentContainer, fragment)
        transaction.addToBackStack("mainStack")
        transaction.commit()
    }

    /**
     * Used to popBackStack
     */
    fun popBackStack() {
        fragmentManager.popBackStack()
    }
}