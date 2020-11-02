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
import com.google.firebase.firestore.CollectionReference
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

@Parcelize
data class Journey(
    @DocumentId var jid: String? = null,
    val uid: String? = null,
    var title: String = "My Journey",
    var pids: ArrayList<String> = ArrayList()
) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    @IgnoredOnParcel
    private val collection = db.collection("journeys")
    @IgnoredOnParcel
    private val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")
    @IgnoredOnParcel
    private val storageRef = storage.reference


    fun add(onComplete: ((journey: Journey?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            jid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Fail to add journey.")
            onComplete?.invoke(null)
        }
    }

    fun getByJid(onComplete: ((journey: Journey?) -> Unit)? = null) {
        if (jid != null) {
            collection.document(jid!!).get().addOnSuccessListener {
                onComplete?.invoke(it.toObject<Journey>())
            }.addOnFailureListener {
                onComplete?.invoke(null)
                Log.e(this::class.java.name, "Failed to get Journey by JID.")
            }
        }
        else Log.e(this::class.java.name, "JID is required for getByJid().")
    }

    fun updateByJid(onComplete: ((journey: Journey?) -> Unit)? = null) {
        if (jid != null) {
            collection.document(jid!!).get().addOnSuccessListener {
                if (it != null) {
                    collection.document(jid!!).update("pids", pids, "title", title).addOnSuccessListener {
                        onComplete?.invoke(this)
                    }.addOnFailureListener {
                        Log.e(this::class.java.name, "Failed to update Journey")
                        onComplete?.invoke(null)
                    }
                }
            }
        }
        else Log.e(this::class.java.name, "JID is required for updateByJid().")
    }

    fun addPidsByJid(addOnList: List<String>, onComplete: ((journey: Journey?) -> Unit)? = null) {
        if (jid != null) {
            collection.document(jid!!).get().addOnSuccessListener {
                if (it != null) {
                    var pids = it.toObject<Journey>()?.pids?.toMutableList()

                    if (pids.isNullOrEmpty()) {
                        pids?.addAll(addOnList)
                        pids?.distinct()?.toList()
                    }
                    else {
                        pids = addOnList.toMutableList()
                    }

                    collection.document(jid!!).update("pids", pids).addOnSuccessListener {
                        onComplete?.invoke(this)
                    }.addOnFailureListener {
                        Log.e(this::class.java.name, "Failed to update Journey")
                        onComplete?.invoke(null)
                    }
                }
            }
        }
        else Log.e(this::class.java.name, "JID is required for updateByJid().")
    }

    fun addPidByJid(addPid: String, onComplete: ((journey: Journey?) -> Unit)? = null) {
        if (jid != null) {
            collection.document(jid!!).get().addOnSuccessListener {
                if (it != null) {
                    var pids = it.toObject<Journey>()?.pids?.toMutableList()

                    if (!pids.isNullOrEmpty()) {
                        pids.add(addPid)
                        pids.distinct().toList()
                    }
                    else {
                        pids = mutableListOf(addPid)
                    }

                    collection.document(jid!!).update("pids", pids).addOnSuccessListener {
                        onComplete?.invoke(this)
                    }.addOnFailureListener {
                        Log.e(this::class.java.name, "Failed to update Journey")
                        onComplete?.invoke(null)
                    }
                }
            }
        }
        else Log.e(this::class.java.name, "JID is required for updateByJid().")
    }

    fun displayJourneyImage(context: Context, imageView: ImageView, vararg transforms: Transformation<Bitmap>) {
        GlideApp.with(context)
            .load(storageRef.child("uploads/journeyImages/$jid"))
            .apply {
                if (transforms.isNotEmpty()) transform(*transforms)
                else transform(CenterInside())

                diskCacheStrategy(DiskCacheStrategy.NONE)
                skipMemoryCache(true)
                error(R.drawable.tellsquarelogo2)
            }
            .into(imageView)
    }

    fun uploadJourneyImage(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/journeyImages/$jid").putFile(file)
    }

    fun deleteByJid() {
        if (jid != null) collection.document(jid!!).delete()
        else Log.e(this::class.java.name, "JID is required for deleteByJid().")
    }

    companion object {
        val collection: CollectionReference = Journey().collection
    }
}