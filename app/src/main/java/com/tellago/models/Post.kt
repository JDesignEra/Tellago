package com.tellago.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.tellago.GlideApp
import com.tellago.R
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

private val todayCalendar = Calendar.getInstance()

@Parcelize
data class Post(
    @DocumentId var pid: String? = null,
    var uid: String? = null,
    var messageBody: String? = null,
    val createDate: Date = todayCalendar.time,
    var postType: String? = null,
    var multimediaURI: String? = null,
    var jid: String? = null,
    var poll: ArrayList<MutableMap<String, ArrayList<String>>> = ArrayList(),
    var pollQuestion: String? = null,
    var comment: ArrayList<MutableMap<String, String>> = ArrayList(),
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
        GlideApp.with(context)
            .load(storageRef.child("uploads/postMedia/$pid"))
            .error(R.drawable.ic_android_photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    fun displayPostMedia(view: View, imageView: ImageView, onLoadFailed: ((visibility: Int) -> Unit)? = null) {
        GlideApp.with(view)
            .load(storageRef.child("uploads/postMedia/$pid"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    onLoadFailed?.invoke(View.GONE)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    onLoadFailed?.invoke(View.VISIBLE)
                    return false
                }
            })
            .into(imageView)
    }

    fun uploadPostMedia(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/postMedia/$pid").putFile(file)
    }

    companion object {
        val collection = Post().collection
    }
}