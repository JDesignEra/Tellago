package com.tellago.models

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.tellago.R
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.File
import java.net.URI
import java.util.*


private val todayCalendar = Calendar.getInstance()

@Parcelize
data class Post(


    @DocumentId var pid: String? = null,
    var uid: String? = null,
    var messageBody: String? = null,
    val createDate: Date = todayCalendar.time,
    var postType: String? = null,
    var multimediaURI: String? = null,
    var journeyArray: ArrayList<String> = ArrayList(),
    var poll: ArrayList<MutableMap<String, Int>> = ArrayList(),
    var pollQuestion: String? = null,
    var comment: ArrayList<String> = ArrayList(),
    var likes: ArrayList<String> = ArrayList()


) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    @IgnoredOnParcel
    private val collection = db.collection("posts")
    @IgnoredOnParcel
    private val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")
    @IgnoredOnParcel
    private val storageRef = storage.reference


    fun getByPid(onComplete: ((post : Post?) -> Unit)? = null) {
        if (pid != null) {
            collection.document(pid!!).get().addOnSuccessListener {
                onComplete?.invoke(it.toObject<Post>())
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Failed to get Post by PID.")
                onComplete?.invoke(null)
            }
        }
        else Log.e(this::class.java.name, "PID is required for getByPid().")
    }


    fun add(onComplete: ((post : Post?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            pid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to add Post.")
            onComplete?.invoke(null)
        }
    }

    fun deleteByPid() {
        if (pid != null) collection.document(pid!!).delete()
        else Log.e(this::class.java.name, "PID is required for deleteByPid().")
    }


    fun displayPostMedia(context: Context, imageView: ImageView) {
        Glide.with(context)
            .load(storageRef.child("uploads/postMedia/$pid"))
            .error(R.drawable.ic_android_photo)
            .circleCrop()
            .into(imageView)
    }

    fun uploadPostMedia(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/postMedia/$pid").putFile(file)
    }


}