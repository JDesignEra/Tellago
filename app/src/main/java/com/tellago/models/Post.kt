package com.tellago.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
import java.util.*
import kotlin.collections.ArrayList

private val todayCalendar = Calendar.getInstance()

@Parcelize
data class Post(
    @DocumentId var pid: String? = null,
    var uid: String? = null,
    var cids: ArrayList<String> = ArrayList(),
    var messageBody: String? = null,
    val createDate: Date = todayCalendar.time,
    var postType: String? = null,
    var jid: String? = null,
    var poll: MutableMap<String, ArrayList<String>> = mutableMapOf(),
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

    fun add(onComplete: ((post : Post?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            pid = it.id
            onComplete?.invoke(this)
            
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to add Post.")
            onComplete?.invoke(null)
        }
    }

    fun addWithCids(selected_cids: ArrayList<String>, onComplete: ((post: Post?) -> Unit)?) {
        cids = selected_cids

        add {
            onComplete?.invoke(null)
        }
    }

    fun addUidToLikes(uid : String) {
        likes.add(uid)

        // update {onComplete?.invoke(null)}
        setByPid()
    }

    fun getPostsByUids(uids: ArrayList<String>, onComplete: ((posts: ArrayList<Post>?) -> Unit)? = null) {
        if (uids.isNotEmpty()) {
            collection.whereArrayContains("uid", uids).get().addOnSuccessListener {
                onComplete?.invoke(ArrayList(it.toObjects()))
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Fail to get posts")
            }
        }
        else {
            Log.e(this::class.java.name, "UIDS is required for getPostsByUid().")
        }
    }

    fun getPostsByCids(onComplete: ((posts: ArrayList<Post>?) -> Unit)? = null) {
        if (cids.isNotEmpty()) {
            collection.whereIn("cids", cids).get().addOnSuccessListener {
                onComplete?.invoke(ArrayList(it.toObjects()))
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Fail to get posts")
            }
        }
        else {
            Log.e(this::class.java.name, "CIDS is required for getPostsByCids().")
        }
    }

    fun removeUidFromLikes(uid : String) {
        if (uid in likes)
        {
            likes.remove(uid)
        }

        // update
        setByPid()
    }

    fun setByPid(onComplete: ((post: Post?) -> Unit)? = null) {
        if (!pid.isNullOrBlank()) {
            collection.document(pid!!).set(this).addOnSuccessListener {
                onComplete?.invoke(this)
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Failed to update Post.")
                onComplete?.invoke(null)
            }
        }
    }

    fun displayPostMedia(
        context: Context,
        imageView: ImageView,
        vararg transforms: Transformation<Bitmap>
    ) {
        GlideApp.with(context)
            .load(storageRef.child("uploads/postMedia/$pid"))
            .apply {
                if (transforms.isNotEmpty()) transform(*transforms)
                else transform(CenterInside())

                error(R.drawable.ic_android_photo)
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            }.into(imageView)
    }

    fun displayPostMedia(
        view: View,
        imageView: ImageView,
        vararg transforms: Transformation<Bitmap>,
        onLoadFailed: ((visibility: Int) -> Unit)? = null
    ) {
        GlideApp.with(view)
            .load(storageRef.child("uploads/postMedia/$pid"))
            .apply {
                if (transforms.isNotEmpty()) transform(*transforms)
                else {
                    transform(CenterInside(), RoundedCorners(15))
                }

                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        onLoadFailed?.invoke(View.GONE)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        onLoadFailed?.invoke(View.VISIBLE)
                        return false
                    }
                })
            }.into(imageView)
    }

    fun uploadPostMedia(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/postMedia/$pid").putFile(file)
    }

    companion object {
        val collection = Post().collection
    }
}