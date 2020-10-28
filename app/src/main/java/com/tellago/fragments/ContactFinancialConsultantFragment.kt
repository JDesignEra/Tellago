package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_contact_financial_consultant.*

class ContactFinancialConsultantFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_financial_consultant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        btn_contact_financial_consultant.setOnClickListener {
            // replace the following activity finish() with actual backend code LATER
            activity?.finish()

        }

    }

    private fun configureToolbar() {
        toolbar_contactFinancialConsultant.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_contactFinancialConsultant.title = "Call To Action"
        toolbar_contactFinancialConsultant.setNavigationOnClickListener {
            activity?.apply {
                this.finish()
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }
}