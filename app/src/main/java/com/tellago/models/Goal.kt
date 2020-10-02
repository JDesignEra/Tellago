package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Goal(
    @DocumentId var gid: String? = null,
    val uid: String? = null,
    val jid: String? = null,
    var title: String? = null,
    var category: ArrayList<String>? = null,
    var targetAmt: Int? = 0,
    var currentAmt: Int? = 0,
    val bucketList: ArrayList<String>? = null,
    val deadline: Date? = null,
    val lastReminder: Calendar? = null,
    val reminderMonthsFreq: Int? = null,
    var completed: Boolean? = null,
    val createDate: Date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"), Locale("en", "SG")).time
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
                Log.e("Goal", "Failed to get Goal by GID.")
                onComplete?.invoke(null)
            }
        }
        else Log.e("Goal", "GID is required for getByGid().")
    }

    fun add(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (completed == null) completed = false

        collection.add(this).addOnSuccessListener {
            gid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e("Goal", "Failed to add Goal.")
            onComplete?.invoke(null)
        }
    }

    fun setByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (!gid.isNullOrBlank()) {
            val mergeFields = ArrayList<String>().apply {
                if (!title.isNullOrBlank()) add(title!!::class.java.name)
                if (!category.isNullOrEmpty()) add(category!!::class.java.name)
                if (targetAmt != null) add(targetAmt!!::class.java.name)
                if (currentAmt != null) add(currentAmt!!::class.java.name)
                if (!bucketList.isNullOrEmpty()) add(bucketList::class.java.name)
                if (deadline != null) add(deadline::class.java.name)
                if (lastReminder != null) add(lastReminder::class.java.name)
                if (completed != null) add(completed!!::class.java.name)
            }

            collection.document(gid!!).set(this, SetOptions.mergeFields(mergeFields)).addOnSuccessListener {
                onComplete?.invoke(this)
            }.addOnFailureListener {
                Log.e("Goal", "Failed to set Goal.")
                onComplete?.invoke(null)
            }
        }
        else Log.e("Goal", "GID is required for updateByGid().")
    }

    fun updateByGid(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (gid != null) {
            val data = hashMapOf<String, Any?>()
            if (uid != null) data["uid"] = uid
            if (jid != null) data["jid"] = jid
            if (title != null) data["title"] = title
            if (category != null) data["category"] = category!!.distinct()
            if (bucketList != null) data["bucketList"] = bucketList
            if (deadline != null) data["deadline"] = deadline
            if (reminderMonthsFreq != null) data["reminderMonthsFreq"] = reminderMonthsFreq

            if (currentAmt != null || targetAmt != null || completed != null) {
                collection.document(gid!!).get().addOnSuccessListener {
                    val oldGoal = it.toObject<Goal>()

                    if (currentAmt == null) currentAmt = oldGoal?.currentAmt
                    if (targetAmt == null) targetAmt = oldGoal?.targetAmt
                    data["complete"] = currentAmt!! >= targetAmt!!

                    // Assign values to 'currentAmt' and 'targetAmt' fields in Firestore document
                    data["currentAmt"] = currentAmt!!
                    data["targetAmt"] = targetAmt!!

                    collection.document(gid!!).update(data).addOnSuccessListener {
                        onComplete?.invoke(this)
                    }.addOnFailureListener {
                        Log.e("Goal", "Failed to update Goal.")
                        onComplete?.invoke(null)
                    }
                }
            }
            else {
                collection.document(gid!!).update(data).addOnSuccessListener {
                    onComplete?.invoke(this)
                }.addOnFailureListener {
                    Log.e("Goal", "Failed to update Goal.")
                    onComplete?.invoke(null)
                }
            }
        }
        else Log.e("Goal", "GID is required for updateByGid().")
    }

    fun deleteByGid() {
        if (gid != null) collection.document(gid!!).delete()
        else Log.e("Goal", "GID is required for deleteByGid().")
    }
}
