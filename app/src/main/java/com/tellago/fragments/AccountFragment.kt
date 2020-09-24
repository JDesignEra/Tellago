package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
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
            note_msg.visibility = View.GONE
            linear_layout_password_provider.visibility = View.GONE
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
                            editText_currentPassword.error = it["currPassword"]
                        }

                        if (!it["password"].isNullOrEmpty()) {
                            editText_newPassword.error = it["password"]
                        }

                        if (!it["cfmPassword"].isNullOrEmpty()) {
                            editText_cfmPassword.error = it["cfmPassword"]
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