package com.tellago.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
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
     * @param fragment replace with targeted Fragment.
     * @param backStackName set to *null* to disable addToBackStack, defaults to **"mainStack"**.
     * @param animate Enable or disable animation with *true* or *false*, defaults to **true**.
     * @param enter enter animation, defaults to **R.anim.fragment_slide_left_enter**.
     * @param exit exit animation, defaults to **R.anim.fragment_slide_left_exit**.
     * @param popEnter popEnter animation, defaults to **R.anim.fragment_slide_right_enter**.
     * @param popExit popExit animation, defaults to **R.anim.fragment_slide_right_exit**.
     * @param setTargetFragment set target fragment and **requestCode** param to enable setTargetFragment, defaults to **null**.
     * @param requestCode **setTargetFragment** has to be set to enable setTargetFragment, defaults to **null**.
     */
    fun replace(fragment: Fragment,
                backStackName: String? = "mainStack",
                animate: Boolean = true,
                enter: Int? = null,
                exit: Int? = null,
                popEnter: Int? = null,
                popExit: Int? = null,
                setTargetFragment: Fragment? = null,
                requestCode: Int? = null
    ) {
        fragmentManager.beginTransaction().apply {
            if (backStackName != null) addToBackStack(backStackName)

            if (animate) {
                setCustomAnimations(
                    enter ?: R.anim.fragment_slide_left_enter,
                    exit ?: R.anim.fragment_slide_left_exit,
                    popEnter ?: R.anim.fragment_slide_right_enter,
                    popExit ?: R.anim.fragment_slide_right_exit
                )
            }

            if (setTargetFragment != null && requestCode != null) {
                fragment.setTargetFragment(setTargetFragment, requestCode)
            }

            replace(fragmentContainer, fragment, fragment::class.java.name)
            commit()
        }
    }

    /**
     * Used to popBackStack (backStackName refers to the stacks within which all entries to be consumed)
     */
    fun popBackStack(backStackName: String? = null) {
        if (backStackName != null) fragmentManager.popBackStack(backStackName, POP_BACK_STACK_INCLUSIVE)
        else fragmentManager.popBackStack()
    }
}