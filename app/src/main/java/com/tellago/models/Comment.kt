package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

private val todayCalendar = Calendar.getInstance()

@Parcelize
data class Comment(
    @DocumentId var cid: String? = null,
    var uid: String? = null,
    var pid: String? = null,
    var comment: String? = null,
    var createdDate: Date = todayCalendar.time
) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    @IgnoredOnParcel
    private val collection = db.collection("comments")

    fun add(onComplete: ((comment : Comment?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            cid = it.id
            onComplete?.invoke(this)

        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to add Comment.")
            onComplete?.invoke(null)
        }
    }

    fun getCommentByPid(onComplete: ((comments : ArrayList<Comment>) -> Unit)? = null) {
        if (!pid.isNullOrBlank()) {
            collection.whereEqualTo("pid", pid).get().addOnSuccessListener {
                onComplete?.invoke(ArrayList(it.toObjects()))
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Fail to get posts")
            }
        }
        else Log.e(this::class.java.name, "PID is required for getCommentByPid().")
    }

    companion object {
        val collection = Comment().collection
    }
}