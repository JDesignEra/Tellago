package com.tellago.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.tellago.MainActivity

class AuthExitService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        MainActivity.user = FirebaseAuth.getInstance().currentUser
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
        if (MainActivity.user != null && MainActivity.user!!.isAnonymous) {
            MainActivity.user?.delete()
            FirebaseAuth.getInstance().signOut()
        }
    }
}