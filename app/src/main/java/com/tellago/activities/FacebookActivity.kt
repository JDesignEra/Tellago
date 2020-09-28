package com.tellago.activities

import android.app.Activity
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
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import kotlin.properties.Delegates

class FacebookActivity : AppCompatActivity() {
    private var linkFlag by Delegates.notNull<Boolean>()
    private val AuthInstance = Auth()
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
                        if (AuthInstance.checkProvider("facebook.com")) {
                            if (AuthInstance.getProviderCount() > 1) {
                                user?.unlink("facebook.com")

                                setResult(
                                    Activity.RESULT_OK,
                                    Intent().putExtra("linked", false)
                                )
                            }
                            else {
                                setResult(
                                    Activity.RESULT_CANCELED,
                                    Intent().putExtra("msg", "Facebook can't be unlinked, as it's the only account")
                                )
                            }

                            this@FacebookActivity.finish()
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
                                        Intent().putExtra("msg", "Facebook account is already registered or linked.")
                                    )
                                }
                            }

                            this@FacebookActivity.finish()
                        }
                    }
                }

                override fun onCancel() {
                    Log.e("FacebookActivity", "User canceled the action")

                    setResult(Activity.RESULT_CANCELED, Intent())
                    this@FacebookActivity.finish()
                }

                override fun onError(e: FacebookException) {
                    Log.e("FacebookActivity", e.message.toString())

                    setResult(Activity.RESULT_CANCELED, Intent())
                    this@FacebookActivity.finish()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )
    }

    override fun onRestart() {
        super.onRestart()

        if (linkFlag) {
            setResult(Activity.RESULT_CANCELED, Intent())
        }

        this@FacebookActivity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fbCbManager.onActivityResult(requestCode, resultCode, data)
    }
}