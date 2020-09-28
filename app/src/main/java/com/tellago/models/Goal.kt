package com.tellago.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

data class Goal(
    val uid: String? = null,
    @DocumentId val gid: String,
    val jid: String? = null,
    val title: String? = null,
    val category: List<String>? = null,
    val targetAmt: Number? = null,
    val currentAmt: Number? = null,
    val bucketList: List<String>? = null,
    val deadline: Timestamp? = null,
    val lastReminder: Timestamp? = null,
    val reminderFreq: Timestamp? = null,
    val createDate: Timestamp = Timestamp(Date())
) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("goals")

    fun add(onComplete: ((goal: Goal) -> Unit?)) {
        collection.add(this).addOnSuccessListener {
            onComplete(this)
        }
    }

    fun update(onComplete: (goal: Goal) -> Unit?) {
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
        if (reminderFreq != null) data["currentAmt"] = reminderFreq

        collection.document(gid).update(data).addOnSuccessListener {
            onComplete(this)
        }
    }

    fun delete() {
        collection.document(gid).delete()
    }
}