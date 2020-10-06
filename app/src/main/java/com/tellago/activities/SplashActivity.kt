package com.tellago.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tellago.MainActivity
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()

        if (Auth().getUser() == null) {
            startAuthActivity()
        }
        else {
            if (user?.isAnonymous!!) {
                startGuestActivity()
            }
            else {
                startMainActivity()
            }
        }
    }

    private fun startAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java)).apply {
            overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            finish()
        }
    }

    private fun startGuestActivity() {
        startActivity(Intent(this, GuestScrollingActivity::class.java)).apply {
            overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            finish()
        }
    }
}