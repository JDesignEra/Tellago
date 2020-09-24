package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.activities.AuthActivity
import com.tellago.activities.FacebookActivity
import com.tellago.activities.GoogleActivity
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        editText_email.setText(user?.email ?: "")

        if (!Auth().checkProvider()) {
            linear_layout_password_provider.visibility = View.GONE
        }

        if (Auth().checkProvider("facebook.com")) {
            btnFacebook.text = "Linked with Facebook"
            btnFacebook.isEnabled = false
        }

        if (Auth().checkProvider("google.com")) {
            btnGoogle.text = "Linked with Google"
            btnGoogle.isEnabled = false
        }

        btnFacebook.setOnClickListener {
            initFacebookLink()
        }

        btnGoogle.setOnClickListener {
            initGoogleLink()
        }

        updateBtn.setOnClickListener {
            if (editText_email.text.isNullOrBlank()) {
                editText_email.error = "Email address is needed"
            }
            else {
                Auth().update(
                    editText_email.text.toString(),
                    editText_currentPassword.text.toString(),
                    editText_newPassword.text.toString(),
                    editText_cfmPassword.text.toString()
                ) {
                    if (it.isNotEmpty()) {
                        if (!it["email"].isNullOrEmpty()) {
                            editText_email.error = it["email"]
                        }

                        if (!it["currPassword"].isNullOrEmpty()) {
                            currentPassword_input_layout.error = it["currPassword"]
                            currentPassword_input_layout.isErrorEnabled = true
                        }
                        else {
                            currentPassword_input_layout.isErrorEnabled = false
                        }

                        if (!it["password"].isNullOrEmpty()) {
                            newPassword_input_layout.error = it["password"]
                            newPassword_input_layout.isErrorEnabled = true
                        }
                        else {
                            newPassword_input_layout.isErrorEnabled = false
                        }

                        if (!it["cfmPassword"].isNullOrEmpty()) {
                            cfmPassword_input_layout.error = it["cfmPassword"]
                            cfmPassword_input_layout.isErrorEnabled = true
                        }
                        else {
                            cfmPassword_input_layout.isErrorEnabled = false
                        }
                    }
                    else {
                        editText_currentPassword.text?.clear()
                        editText_newPassword.text?.clear()
                        editText_cfmPassword.text?.clear()

                        CustomToast(requireContext(), "Account settings updated successfully").success()

                        Handler().postDelayed({
                            startActivity(Intent(requireContext(), AuthActivity::class.java))
                        }, 1500)
                    }
                }
            }
        }
    }

    private fun configureToolbar() {
        toolbar_Account.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_Account.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun initFacebookLink() {
        startActivity(
            Intent(requireContext(), FacebookActivity::class.java)
                .putExtra("linkFlag", true)
        )
    }

    private fun initGoogleLink() {
        startActivity(
            Intent(requireContext(), GoogleActivity::class.java)
                .putExtra("linkFlag", true)
        )
    }
}