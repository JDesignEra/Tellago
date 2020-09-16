package com.tellago.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val bio: String? = null
) {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")
    private val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")

    fun getUserWithUid(onComplete: (user: User?) -> Unit) {
        collection.document(uid).get().addOnSuccessListener {
            onComplete(it.toObject<User>())
        }
    }

    fun update(): User? {
        collection.document(uid).set(this)
        return this
    }
}