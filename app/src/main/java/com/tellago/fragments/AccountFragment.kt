package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.tellago.R
import com.tellago.activities.AuthActivity
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {
    private val gSignInRc = 1820
    private lateinit var gsc: GoogleSignInClient
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private val fbCbManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gsc = GoogleSignIn.getClient(requireContext(), gso)

        LoginManager.getInstance().registerCallback(
            fbCbManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    user?.linkWithCredential(getFacebookCredential(loginResult.accessToken))
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                CustomToast(requireContext(), "Facebook account linked successfully")
                            }
                        }
                }

                override fun onCancel() {
                    // TODO
                }

                override fun onError(error: FacebookException) {
                    // TODO
                }
            }
        )

        GoogleSignIn.getClient(requireContext(), gso)
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
            note_msg.visibility = View.GONE
            linear_layout_password_provider.visibility = View.GONE
        }

        btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
        }

        btnGoogle.setOnClickListener {
            startActivityForResult(gsc.signInIntent, gSignInRc)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == gSignInRc) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)!!
                user?.linkWithCredential(getGoogleCredential(account.idToken!!))
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            CustomToast(requireContext(), "Google account linked successfully")
                        }
                    }
            }
            catch (e: ApiException) {
                Log.w("AccountFragment", "Google sign in failed", e)
            }
        }
        else {
            fbCbManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getGoogleCredential(idToken: String): AuthCredential {
        return GoogleAuthProvider.getCredential(idToken, null)
    }

    private fun getFacebookCredential(token: AccessToken): AuthCredential {
        return FacebookAuthProvider.getCredential(token.token)
    }

    private fun configureToolbar() {
        toolbar_Account.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_Account.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}