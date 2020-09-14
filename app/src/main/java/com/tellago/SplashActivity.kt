package com.tellago

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tellago.model.Auth

class SplashActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        if (Auth.user == null) {
            Auth().initSignInInstance(this)
        }
        else {
            startMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Auth.rcSignIn && resultCode == Activity.RESULT_OK) {
            Auth.user = FirebaseAuth.getInstance().currentUser

            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}