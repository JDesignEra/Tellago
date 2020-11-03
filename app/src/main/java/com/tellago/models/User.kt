package com.tellago.models

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.tellago.GlideApp
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

    fun displayProfilePicture(
        context: Context,
        imageView: ImageView,
        vararg transforms: Transformation<Bitmap>
    ) {
        GlideApp.with(context)
            .load(storageRef.child("uploads/dp/$uid"))
            .apply {
                if (transforms.isNotEmpty()) transform(*transforms)
                else transform(CircleCrop(), CenterInside())

                error(R.drawable.ic_android_photo)
                diskCacheStrategy(DiskCacheStrategy.NONE)
                skipMemoryCache(true)
            }.into(imageView)
    }

    fun uploadDp(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/dp/$uid").putFile(file)
    }
}