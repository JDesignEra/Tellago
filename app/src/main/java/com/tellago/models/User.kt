package com.tellago.models

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.request.target.ViewTarget
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.net.URI

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val bio: String? = null
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
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

    fun getDpUri(onComplete: (Uri?) -> ViewTarget<ImageView, Drawable>) {
        storage.reference.child("uploads/dp/$uid").downloadUrl.addOnSuccessListener {
            onComplete(it)
        }
    }

    fun uploadDp(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storage.reference.child("uploads/dp/$uid").putFile(file)
    }
}