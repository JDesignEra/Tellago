package com.tellago.models

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.tellago.R
import java.io.File
import java.net.URI

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val bio: String? = null
) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")
    private val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")
    private val storageRef = storage.reference

    fun getUserWithUid(onComplete: (user: User?) -> Unit) {
        collection.document(uid).get().addOnSuccessListener {
            onComplete(it.toObject<User>())
        }
    }

    fun update(): User? {
        collection.document(uid).set(this)
        return this
    }

    fun displayProfilePicture(context: Context, imageView: ImageView) {
        Glide.with(context)
            .load(storageRef.child("uploads/dp/$uid"))
            .error(R.drawable.ic_android_photo)
            .circleCrop()
            .into(imageView)
    }

    fun uploadDp(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/dp/$uid").putFile(file)
    }
}