package com.tellago.models

import android.content.Context
import android.util.Log
import android.util.Patterns
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase

class Auth {
    init {
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser

        if (user != null && profile == null && !user!!.isAnonymous) {
            User(user!!.uid, user!!.email, user!!.displayName, null).getUserWithUid {
                profile = it ?: User(user!!.uid, user!!.email, user!!.displayName, null).update()
            }
        }
    }

    fun update(displayName: String, bio: String?) {
        if (user != null) {
            if (!displayName.isNullOrEmpty()) {
                user?.updateProfile(UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(displayName)
                    .build())
            }

            profile = User(user!!.uid, user!!.email, displayName, bio).update()
        }
    }

    fun update(email: String, currPassword: String?, password: String?, cfmPassword: String?, onComplete: (errors: MutableMap<String, String>) -> Unit) {
        val errors = mutableMapOf<String, String>()

        if (user != null && profile != null) {
            val regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+\$).{6,}".toRegex()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errors["email"] = "Invalid email address format"
            }

            if (!currPassword.isNullOrEmpty() || !password.isNullOrEmpty() || !cfmPassword.isNullOrEmpty()) {
                if (currPassword.isNullOrEmpty()) {
                    errors["currPassword"] = "Current password is needed"
                }

                if (password.isNullOrEmpty() || !password!!.matches(regex)) {
                    errors["password"] = "Use at least 6 characters and a mix of letters and numbers"
                }

                if (cfmPassword != password) {
                    errors["cfmPassword"] = "Confirm Password does not match password"
                }
            }

            if (email == user!!.email) {
                errors["email"] = "Your email is the same as your current email"
            }
        }
        else {
            errors["user"] = "You are not logged in"
        }

        if (errors.isEmpty()) {
            if (!currPassword.isNullOrEmpty()) {
                user!!.reauthenticate(
                    EmailAuthProvider.getCredential(user!!.email!!, currPassword!!)
                ).addOnSuccessListener {
                    user!!.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (password != null) user!!.updatePassword(password)

                            profile = User(user!!.uid, email, profile!!.displayName, profile!!.bio).update()
                            signOut()

                            onComplete(errors)
                        }
                        else {
                            errors["email"] = "Email address is already registered"

                            onComplete(errors)
                        }
                    }
                }.addOnFailureListener {
                    errors["currPassword"] = "Incorrect current password"

                    onComplete(errors)
                }
            }
            else {
                user!!.updateEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        profile = User(user!!.uid, email, profile!!.displayName, profile!!.bio).update()
                        signOut()

                        onComplete(errors)
                    }
                    else {
                        errors["email"] = "Email address is already registered"

                        onComplete(errors)
                    }
                }
            }
        }
        else {
            onComplete(errors)
        }
    }

    fun checkProvider(providerName: String? = null): Boolean {
        var providerData = user?.providerData

        if (!providerData.isNullOrEmpty()) {
            providerData.forEach {
                if (it.providerId == providerName ?: "password") return true
            }
        }

        return false
    }

    fun getProviderCount(): Int {
        var providerData = user?.providerData
        var count: Int = 0

        if (!providerData.isNullOrEmpty()) {
            providerData.forEach {
                Log.e("Auth", it.providerId)
                if (it.providerId != "firebase") count++
            }
        }

        return count
    }

    fun getUser(): FirebaseUser? {
        return user
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        if (user != null && user!!.isAnonymous) {
            user!!.delete()
        }

        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                auth = null
                user = null
                profile = null
                onComplete()
            }
    }

    private fun signOut() {
        if (user != null && user!!.isAnonymous) {
            user!!.delete()
        }

        FirebaseAuth.getInstance().signOut()
        auth = null
        user = null
        profile = null
    }

    companion object {
        var auth: FirebaseAuth? = null
        var user: FirebaseUser? = null
        var profile: User? = null
    }
}