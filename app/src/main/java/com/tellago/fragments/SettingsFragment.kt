package com.tellago.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.menu_header.*

class SettingsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private val accountFragment = AccountFragment()
    private val privacyPolicyFragment = PrivacyPolicyScrollingFragment()
    private val termsAndConditionsFragment = TermsConditionsScrollingFragment()
    private val aboutUsFragment = AboutUsScrollingFragment()
//    private val securityFragment = SecurityFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.accountSettings_fragment_container
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accountClick: ConstraintLayout = setting_account_layout

        if (!Auth().checkProvider()) {
            setting_account_text.text = "Link Account"
        }

        accountClick.setOnClickListener {
            fragmentUtils.addFragmentToFragment(accountFragment)
        }

        configureToolbar()
        //configureNavigationDrawer()

        setting_privacy_layout.setOnClickListener {
            fragmentUtils.addFragmentToFragment(privacyPolicyFragment)
        }

        setting_terms_conditions_layout.setOnClickListener {
            fragmentUtils.addFragmentToFragment(termsAndConditionsFragment)
        }

        setting_about_layout.setOnClickListener {
            fragmentUtils.addFragmentToFragment(aboutUsFragment)
        }

    }

    private fun configureToolbar() {
        toolbar_Setting.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_Setting.setNavigationOnClickListener {
            //finish AccountSettingsActivity
            activity?.finish()
        }
    }
}