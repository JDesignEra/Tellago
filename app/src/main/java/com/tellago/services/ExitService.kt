package com.tellago.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.tellago.models.Auth

class ExitService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
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