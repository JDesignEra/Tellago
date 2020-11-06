package com.tellago.models

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.tellago.GlideApp
import com.tellago.R
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.net.URI

@Parcelize
data class Communities(
    @DocumentId var cid: String? = null,
    var name: String? = null,
    var summary: String? = null,
    var category: ArrayList<String> = ArrayList(),
    // Key as uid, and value as type [admin, user]
    var uids: MutableMap<String, String> = mutableMapOf()
) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    @IgnoredOnParcel
    private val collection = db.collection("Communities")
    @IgnoredOnParcel
    private val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")
    @IgnoredOnParcel
    private val storageRef = storage.reference

    fun add(onComplete: ((communities: Communities?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            cid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e(this::class.java.name, it.message.toString())
            onComplete?.invoke(null)
        }
    }

    fun getAll(onComplete: ((communities: ArrayList<Communities>?) -> Unit)? = null) {
        collection.get().addOnSuccessListener {
            onComplete?.invoke(ArrayList(it.toObjects()))
        }.addOnFailureListener {
            Log.e(this::class.java.name, it.message.toString())
        }
    }

    fun getByCid(onComplete: ((community: Communities?) -> Unit)? = null) {
        if (cid != null) {
            collection.document(cid!!).get().addOnSuccessListener {
                onComplete?.invoke(it.toObject<Communities>())
            }.addOnFailureListener {
                Log.e(this::class.java.name, it.message.toString())
                onComplete?.invoke(null)
            }
        } else Log.e(this::class.java.name, "CID is reqquired for getByCid().")
    }

    fun searchByName(searchString: String, onComplete: ((communities: ArrayList<Communities>?) -> Unit)? = null) {
        collection.whereGreaterThanOrEqualTo("name", searchString).get().addOnSuccessListener {
            onComplete?.invoke(ArrayList(it.toObjects()))
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to get Community by CID")
            onComplete?.invoke(null)
        }
    }

    fun uploadImageByCid(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/dp/$cid").putFile(file)
    }

    fun displayImageByCid(
        context: Context,
        imageView: ImageView,
        vararg transforms: Transformation<Bitmap>
    ) {
        GlideApp.with(context)
            .load(storageRef.child("uploads/communities/$cid"))
            .apply {
                if (transforms.isNotEmpty()) transform(*transforms)
                else transform(CenterInside())

                error(R.drawable.ic_android_photo)
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            }.into(imageView)
    }

    companion object {
        val collection = Communities().collection
    }
}