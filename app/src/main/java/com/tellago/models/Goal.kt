package com.tellago.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*

data class Goal(
    @DocumentId var gid: String? = null,
    val uid: String? = null,
    val jid: String? = null,
    val title: String? = null,
    val category: List<String>? = null,
    val targetAmt: Int? = null,
    val currentAmt: Int? = null,
    val bucketList: List<String>? = null,
    val deadline: Date? = null,
    val lastReminder: Calendar? = null,
    val reminderMonthsFreq: Int? = null,
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

    fun getUserGoals(onComplete: ((goals: List<Goal>?) -> Unit)? = null) {
        collection.whereEqualTo("uid", uid).get().addOnSuccessListener {
            onComplete?.invoke(it.toObjects(Goal::class.java))
        }
    }

    fun add(onComplete: ((goal: Goal?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            gid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            onComplete?.invoke(null)
        }
    }

    fun update(onComplete: ((goal: Goal) -> Unit)? = null) {
        val data = hashMapOf<String, Any?>()
        if (uid != null) data["uid"] = uid
        if (jid != null) data["jid"] = jid
        if (title != null) data["title"] = title
        if (category != null) data["category"] = category
        if (targetAmt != null) data["targetAmt"] = targetAmt
        if (currentAmt != null) data["currentAmt"] = currentAmt
        if (bucketList != null) data["bucketList"] = bucketList
        if (deadline != null) data["category"] = deadline
        if (lastReminder != null) data["targetAmt"] = lastReminder
        if (reminderMonthsFreq != null) data["reminderMonthsFreq"] = reminderMonthsFreq

        if (gid != null) {
            collection.document(gid!!).update(data).addOnSuccessListener {
                onComplete?.invoke(this)
            }
        }
    }

    fun delete() {
        if (gid != null) collection.document(gid!!).delete()
    }
}