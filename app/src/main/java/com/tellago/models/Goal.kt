package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
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
    val jid: String? = null,
    var title: String = "Your Goal",
    var categories: ArrayList<String> = ArrayList(),
    var targetAmt: Double = 0.0,
    var currentAmt: Double = 0.0,
    var bucketList: ArrayList<MutableMap<String, @RawValue Any>?> = ArrayList(),
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

    fun getByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (gid != null) {
            collection.document(gid!!).get().addOnSuccessListener {
                onComplete?.invoke(it.toObject<Goal>())
            }.addOnFailureListener {
                Log.e(this::class.java.name, "Failed to get Goal by GID.")
                onComplete?.invoke(null)
            }
        }
        else Log.e(this::class.java.name, "GID is required for getByGid().")
    }

    fun add(onComplete: ((goal: Goal?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            gid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to add Goal.")
            onComplete?.invoke(null)
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

//    fun updateByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
//        if (gid != null) {
//            val data = hashMapOf<String, Any?>()
//            if (uid != null) data["uid"] = uid
//            if (jid != null) data["jid"] = jid
//            if (title != null) data["title"] = title
//            if (category != null) data["category"] = category!!.distinct()
//            if (bucketList != null) data["bucketList"] = bucketList
//            if (deadline != null) data["deadline"] = deadline
//            if (reminderMonthsFreq != null) data["reminderMonthsFreq"] = reminderMonthsFreq
//
//            if (currentAmt != null || targetAmt != null || completed != null) {
//                collection.document(gid!!).get().addOnSuccessListener {
//                    val oldGoal = it.toObject<Goal>()
//
//                    if (currentAmt == null) currentAmt = oldGoal?.currentAmt
//                    if (targetAmt == null) targetAmt = oldGoal?.targetAmt
//                    data["complete"] = currentAmt!! >= targetAmt!!
//
//                    // Assign values to 'currentAmt' and 'targetAmt' fields in Firestore document
//                    data["currentAmt"] = currentAmt!!
//                    data["targetAmt"] = targetAmt!!
//
//                    collection.document(gid!!).update(data).addOnSuccessListener {
//                        onComplete?.invoke(this)
//                    }.addOnFailureListener {
//                        Log.e("Goal", "Failed to update Goal.")
//                        onComplete?.invoke(null)
//                    }
//                }
//            }
//            else {
//                collection.document(gid!!).update(data).addOnSuccessListener {
//                    onComplete?.invoke(this)
//                }.addOnFailureListener {
//                    Log.e("Goal", "Failed to update Goal.")
//                    onComplete?.invoke(null)
//                }
//            }
//        }
//        else Log.e("Goal", "GID is required for updateByGid().")
//    }

    fun deleteByGid() {
        if (gid != null) collection.document(gid!!).delete()
        else Log.e(this::class.java.name, "GID is required for deleteByGid().")
    }
}
