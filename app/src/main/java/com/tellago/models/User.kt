package com.tellago.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val bio: String? = null
) {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")

    fun getUserWithUid(onComplete: (user: User?) -> Unit) {
        collection.document(uid).get().addOnSuccessListener {
            onComplete(it.toObject<User>())
        }
    }

    fun update(): User? {
        val user = User(uid, email, displayName, bio)

        collection.document(uid).set(user)
        return user
    }
}