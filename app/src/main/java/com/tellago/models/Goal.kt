package com.tellago.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.util.*

data class Goal(
    @DocumentId var gid: String? = null,
    val uid: String? = null,
    val jid: String? = null,
    var title: String? = null,
    var category: List<String>? = null,
    var targetAmt: Int? = 0,
    var currentAmt: Int? = 0,
    val bucketList: List<String>? = null,
    val deadline: Date? = null,
    val lastReminder: Calendar? = null,
    val reminderMonthsFreq: Int? = null,
    var completed: Boolean? = null,
    val createDate: Date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"), Locale("en", "SG")).time
) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("goals")

    fun getGoal(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (gid != null) {
            collection.document(gid!!).get().addOnSuccessListener {
                onComplete?.invoke(it.toObject<Goal>())
            }
        }
    }

    fun add(onComplete: ((goal: Goal?) -> Unit)? = null) {
        if (completed == null) completed = false

        collection.add(this).addOnSuccessListener {
            gid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            onComplete?.invoke(null)
        }
    }

    fun update(onComplete: ((goal: Goal) -> Unit)? = null) {
        if (gid != null) {
            val data = hashMapOf<String, Any?>()
            if (uid != null) data["uid"] = uid
            if (jid != null) data["jid"] = jid
            if (title != null) data["title"] = title
            if (category != null) data["category"] = category
            if (bucketList != null) data["bucketList"] = bucketList
            if (deadline != null) data["category"] = deadline
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
                    }
                }
            }
            else {
                collection.document(gid!!).update(data).addOnSuccessListener {
                    onComplete?.invoke(this)
                }
            }
        }
    }

    fun delete() {
        if (gid != null) collection.document(gid!!).delete()
    }
}
