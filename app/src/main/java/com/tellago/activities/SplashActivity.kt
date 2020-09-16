package com.tellago.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tellago.MainActivity
import com.tellago.R
import com.tellago.models.Auth

class SplashActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        if (Auth().getUser() == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}