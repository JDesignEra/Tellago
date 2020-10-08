package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*


private val todayCalendar = Calendar.getInstance()

@Parcelize
data class Post(


    @DocumentId var pid: String? = null,
    var uid: String? = null,
    var messageBody: String? = null,
    val createDate: Date = todayCalendar.time,
    var multimediaArray: ArrayList<String> = ArrayList(),
    var journeyArray: ArrayList<String> = ArrayList(),
    var poll: ArrayList<MutableMap<String, Int>> = ArrayList()


) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    @IgnoredOnParcel
    private val collection = db.collection("posts")



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


}