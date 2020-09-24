package com.tellago.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.tellago.models.Auth.Companion.user
import com.tellago.utils.CustomToast
import kotlin.properties.Delegates

class FacebookActivity : AppCompatActivity() {
    private var linkFlag by Delegates.notNull<Boolean>()
    private val fbCbManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linkFlag = intent.getBooleanExtra("linkFlag", false)

        Log.e("FacebookActivity", linkFlag.toString())

        LoginManager.getInstance().registerCallback(
            fbCbManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    if (linkFlag) {
                        user?.linkWithCredential(getFacebookCredential(loginResult.accessToken))
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    CustomToast(this@FacebookActivity, "Facebook account linked successfully")
                                }
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
    }

    override fun onResume() {
        super.onResume()

        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (fbCbManager.onActivityResult(requestCode, resultCode, data)) {
            finish()
        }
    }

    private fun getFacebookCredential(token: AccessToken): AuthCredential {
        return FacebookAuthProvider.getCredential(token.token)
    }
}