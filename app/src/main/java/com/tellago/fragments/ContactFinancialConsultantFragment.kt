package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.UserContact
import com.tellago.utilities.CustomToast
import kotlinx.android.synthetic.main.fragment_contact_financial_consultant.*

class ContactFinancialConsultantFragment : Fragment() {
    private lateinit var toast: CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_financial_consultant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        btn_contact_financial_consultant.setOnClickListener {
            btn_contact_financial_consultant.setOnClickListener {
                var errors: MutableMap<String, String> = mutableMapOf()

                if (firstName_et.text.isNullOrBlank()) errors["name"] = "Field is required"
                if (email_et.text.isNullOrBlank()) errors["email"] = "Field is required"
                if (contact_et.text.isNullOrBlank()) errors["contact"] = "Field is required"
                else if (contact_et.text.toString().length != 8) errors["contact"] = "Contact Number requires 8 characters"
                if (!agree_cb.isChecked) errors["agree"] = "Your consent is required"

                if (errors.isEmpty()) {
                    UserContact(
                        name = firstName_et.text.toString(),
                        email = email_et.text.toString(),
                        contactNo = contact_et.text.toString()
                    ).add {
                        if (it != null) {
                            toast.success("A financial consultant will get back to you soon.")
                            activity?.finish()
                        }
                        else toast.error("Please try again, fail to submit your details")
                    }
                }
                else {
                    errors["name"]?.let { firstName_et.error = it }
                    errors["email"]?.let { email_et.error = it }
                    errors["contact"]?.let { contact_et.error = it }
                    errors["agree"]?.let { toast.error(it) }
                }
            }
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