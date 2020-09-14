package com.tellago.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.tellago.MainActivity
import com.tellago.model.Auth

class AuthExitService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        Auth.user = FirebaseAuth.getInstance().currentUser
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteAnonymous()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        deleteAnonymous()
    }

    fun deleteAnonymous() {
        if (Auth.user != null && Auth.user!!.isAnonymous) {
            Auth.user?.delete()
            FirebaseAuth.getInstance().signOut()
        }
    }
}