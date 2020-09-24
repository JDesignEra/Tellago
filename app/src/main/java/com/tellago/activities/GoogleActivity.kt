package com.tellago.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.tellago.R.string.default_web_client_id
import com.tellago.models.Auth.Companion.user
import com.tellago.utils.CustomToast
import kotlin.properties.Delegates

class GoogleActivity : AppCompatActivity() {
    private var linkFlag by Delegates.notNull<Boolean>()
    private val signInRc = 1820
    private lateinit var gsc: GoogleSignInClient
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(default_web_client_id))
        .requestEmail()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.getBoolean("linkFlag", false)
        gsc = GoogleSignIn.getClient(this, gso)
        GoogleSignIn.getClient(this, gso)
    }

    override fun onResume() {
        super.onResume()

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
                    user?.linkWithCredential(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                CustomToast(this, "Google account linked successfully")
                            }
                        }
                }
            }
            catch (e: ApiException) {
                Log.e("AccountFragment", "Google sign in failed", e)
            }
        }
    }
}