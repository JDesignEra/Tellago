package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*
import kotlin.collections.ArrayList


private val todayCalendar = Calendar.getInstance()

@Parcelize
data class Goal(
    @DocumentId var gid: String? = null,
    var uid: String? = null,
    var jid: ArrayList<String> = ArrayList(),
    var title: String = "Your Goal",
    var categories: ArrayList<String> = ArrayList(),
    var targetAmt: Double = 0.0,
    var currentAmt: Double = 0.0,
    var bucketList: ArrayList<MutableMap<String, @RawValue Any>> = ArrayList(),
    var deadline: Date = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.HOUR, 0)
    }.time,
    val lastReminder: Date? = null,
    var reminderMonthsFreq: Int = 0,
    var completed: Boolean = false,
    val createDate: Date = todayCalendar.time
) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    @IgnoredOnParcel
    private val collection = db.collection("goals")

    fun add(onComplete: ((goal: Goal?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            gid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to add Goal.")
            onComplete?.invoke(null)
        }
    }

    fun addWithJid(pids: ArrayList<String>, onComplete: ((goal: Goal?) -> Unit)?) {
        Journey(uid = uid, pids = pids).add { journey ->
            journey?.jid?.let { id -> jid.add(id) }

            add {
                onComplete?.invoke(it)
            }
        }
    }

    fun setByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (!gid.isNullOrBlank()) {
            val mergeFields = arrayListOf<String>(
                "title", "categories", "targetAmt",
                "currentAmt", "deadline", "lastReminder", "reminderMonthsFreq", "completed"
            )

            collection.document(gid!!).set(this, SetOptions.mergeFields(mergeFields)).addOnSuccessListener {
                onComplete?.invoke(this)
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Failed to set Goal.")
                onComplete?.invoke(null)
            }
        }
        else Log.e(this::class.java.name, "GID is required for setByGid().")
    }

    fun updateBucketListByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (!gid.isNullOrBlank()) {
            collection.document(gid!!).update("bucketList", bucketList).addOnSuccessListener {
                onComplete?.invoke(this)
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Failed to update Goal's bucketList.")
                onComplete?.invoke(null)
            }
        }
        else Log.e(this::class.java.name, "GID is required for updateBucketListByGid().")
    }

    fun updateCompleteByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (!gid.isNullOrBlank()) {
            collection.document(gid!!).update("completed", completed).addOnSuccessListener {
                onComplete?.invoke(this)
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Failed to update Goal's bucketList.")
                onComplete?.invoke(null)
            }
        }
        else Log.e(this::class.java.name, "GID is required for updateCompleteByGid().")
    }

    fun deleteByGid() {
        if (gid != null) collection.document(gid!!).delete()
        else Log.e(this::class.java.name, "GID is required for deleteByGid().")
    }

    companion object {
        val collection: CollectionReference = Goal().collection
    }
}
