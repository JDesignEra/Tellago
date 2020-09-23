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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.menu_header.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val accountFragment = AccountFragment()
    private val privacyPolicyFragment = PrivacyPolicyScrollingFragment()
    private val termsAndConditionsFragment = TermsConditionsScrollingFragment()
    private val aboutUsFragment = AboutUsScrollingFragment()
//    private val securityFragment = SecurityFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

        accountClick.setOnClickListener {
            addFragmentFromFragment(accountFragment)
        }

        configureToolbar()
        //configureNavigationDrawer()


        setting_privacy_layout.setOnClickListener {
            addFragmentFromFragment(privacyPolicyFragment)
        }

        setting_terms_conditions_layout.setOnClickListener {
            addFragmentFromFragment(termsAndConditionsFragment)
        }

        setting_about_layout.setOnClickListener {
            addFragmentFromFragment(aboutUsFragment)
        }

    }

    private fun configureToolbar() {
        toolbar_Setting.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_Setting.setNavigationOnClickListener {
            //finish AccountSettingsActivity
            activity?.finish()
        }
    }

    private fun addFragmentFromFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.accountSettings_fragment_container, fragment)
        }
        if (transaction != null) {
            transaction.addToBackStack(null)
        };
        if (transaction != null) {
            transaction.commit()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}