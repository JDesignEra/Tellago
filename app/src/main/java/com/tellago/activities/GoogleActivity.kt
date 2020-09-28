package com.tellago.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.utils.CustomToast
import kotlin.properties.Delegates

class GoogleActivity : AppCompatActivity() {
    private var linkFlag by Delegates.notNull<Boolean>()
    private val AuthInstance = Auth()
    private val signInRc = 1820

    private lateinit var toast: CustomToast
    private lateinit var gsc: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(baseContext)
        linkFlag = intent.getBooleanExtra("linkFlag", false)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        startActivityForResult(gsc.signInIntent, signInRc)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == signInRc) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                if (linkFlag) {
                    if (AuthInstance.checkProvider("google.com")) {
                        if (AuthInstance.getProviderCount() > 1) {
                            user?.unlink("google.com")

                            setResult(
                                Activity.RESULT_OK,
                                Intent().putExtra("linked", false)
                            )
                        }
                        else {
                            setResult(
                                Activity.RESULT_CANCELED,
                                Intent().putExtra("msg", "Google can't be unlinked, as it's the only account")
                            )
                        }

                        finish()
                    }
                    else {
                        user?.linkWithCredential(credential)?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                setResult(
                                    Activity.RESULT_OK,
                                    Intent().putExtra("linked", true)
                                )
                            }
                            else {
                                setResult(
                                    Activity.RESULT_CANCELED,
                                    Intent().putExtra("msg", "Google account is already registered or linked.")
                                )
                            }

                            finish()
                        }
                    }
                }
            }
            catch (e: ApiException) {
                Log.e("GoogleActivity", "Google sign in failed", e)
                finish()
            }
        }
    }
}