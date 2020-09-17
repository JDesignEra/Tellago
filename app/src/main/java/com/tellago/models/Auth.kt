package com.tellago.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class Auth {
    init {
        user = FirebaseAuth.getInstance().currentUser

        if (user != null && profile == null && !user!!.isAnonymous) {
            User(user!!.uid, user!!.email, user!!.displayName, null).getUserWithUid {
                profile = it ?: User(user!!.uid, user!!.email, user!!.displayName, null).update()
            }
        }
    }

    fun update(email: String? = null, displayName: String, bio: String?) {
        if (user != null) {
            if (!displayName.isNullOrEmpty()) {
                user?.updateProfile(UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(displayName)
                    .build())
            }

            if (!email.isNullOrEmpty()) {
                user?.updateEmail(email)
            }

            profile = User(user!!.uid, user!!.email, displayName, bio).update()
        }
    }

    fun getUser(): FirebaseUser? {
        return user
    }

    companion object {
        var user: FirebaseUser? = null
        var profile: User? = null
    }
}