package com.tellago.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
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

        LoginManager.getInstance().registerCallback(
            fbCbManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val token = loginResult.accessToken.token
                    val credential = FacebookAuthProvider.getCredential(token)

                    if (linkFlag) {
                        user?.linkWithCredential(credential)
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    CustomToast(
                                        baseContext,
                                        "Facebook account linked successfully"
                                    ).success()
                                }
                                else {
                                    CustomToast(
                                        baseContext,
                                        "Facebook account is already registered or linked."
                                    ).primary()
                                }
                            }
                    }
                }

                override fun onCancel() {
                    Log.e("FacebookActivity", "User canceled the action")
                }

                override fun onError(e: FacebookException) {
                    Log.e("FacebookActivity", e.message.toString())
                }
            }
        )

        Log.e("FacebookActivity", "onCreate")
    }

    override fun onStart() {
        super.onStart()

        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )

        Log.e("FacebookActivity", "onResume")
    }

    override fun onRestart() {
        super.onRestart()

        Log.e("FacebookActivity", "onRestart")
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fbCbManager.onActivityResult(requestCode, resultCode, data)
        finish()
    }
}