package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.activities.AuthActivity
import com.tellago.activities.FacebookActivity
import com.tellago.activities.GoogleActivity
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.utilities.CustomToast
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {
    private lateinit var toast: CustomToast
    private val fbLinkRc = 1084
    private val gLinkRc = 1085

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
    }

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
            fbBtnLinkState(true)
        }

        if (Auth().checkProvider("google.com")) {
            googleBtnLinkState(true)
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
                            email_text_input_layout.error = it["email"]
                            email_text_input_layout.isErrorEnabled = true
                        }
                        else {
                            email_text_input_layout.isErrorEnabled = false
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

                        toast.success("Account settings updated successfully")

                        Handler().postDelayed({
                            startActivity(Intent(requireContext(), AuthActivity::class.java))
                        }, 1500)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == fbLinkRc || requestCode == gLinkRc) {
            lateinit var providerName: String

            when (requestCode) {
                fbLinkRc -> providerName = "Facebook"
                gLinkRc -> providerName = "Google"
            }

            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getBooleanExtra("linked", false)!!

                when (requestCode) {
                    fbLinkRc -> fbBtnLinkState(result)
                    gLinkRc -> googleBtnLinkState(result)
                }

                if (result) {
                    toast.success("$providerName linked successfully")
                }
                else {
                    toast.success("$providerName unlinked successfully")
                }
            }
            else {
                val msg = data?.getStringExtra("msg")

                if (!msg.isNullOrEmpty()) toast.error(msg)
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
        startActivityForResult(
            Intent(requireContext(), FacebookActivity::class.java)
                .putExtra("linkFlag", true),
            fbLinkRc
        )
    }

    private fun initGoogleLink() {
        startActivityForResult(
            Intent(requireContext(), GoogleActivity::class.java)
                .putExtra("linkFlag", true),
            gLinkRc
        )
    }

    private fun fbBtnLinkState(flag: Boolean) {
        if (flag) {
            btnFacebook.text = "Unlink Facebook"
            btnFacebook.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#9e9e9e")
            )
        }
        else {
            btnFacebook.text = "Link with Facebook"
            btnFacebook.backgroundTintList = ColorStateList.valueOf(
                getColor(requireContext(), R.color.facebook_material_button)
            )
        }
    }

    private fun googleBtnLinkState(flag: Boolean) {
        if (flag) {
            btnGoogle.text = "Unlink Google"
            btnGoogle.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#9e9e9e")
            )
        }
        else {
            btnGoogle.text = "Link with Google"
            btnGoogle.backgroundTintList = ColorStateList.valueOf(
                getColor(requireContext(), R.color.google_material_button)
            )
        }
    }
}