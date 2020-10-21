package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_settings.*

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accountClick: ConstraintLayout = setting_account_layout

        if (!Auth().checkProvider()) {
            setting_account_text.text = "Link Account"
        }

        accountClick.setOnClickListener {
            fragmentUtils.replace(accountFragment)
        }

        configureToolbar()
        //configureNavigationDrawer()

        setting_privacy_layout.setOnClickListener {
            fragmentUtils.replace(privacyPolicyFragment)
        }

        setting_terms_conditions_layout.setOnClickListener {
            fragmentUtils.replace(termsAndConditionsFragment)
        }

        setting_about_layout.setOnClickListener {
            fragmentUtils.replace(aboutUsFragment)
        }

    }

    private fun configureToolbar() {
        toolbar_Setting.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_Setting.setNavigationOnClickListener {
            activity?.apply {
                this.finish()
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }
}