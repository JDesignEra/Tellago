package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import kotlinx.android.synthetic.main.fragment_terms_conditions_scrolling.*

class TermsConditionsScrollingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_conditions_scrolling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()
    }

    private fun configureToolbar() {
        toolbar_TermsConditions.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_TermsConditions.setNavigationOnClickListener{
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }

    }
}