package com.tellago.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*

data class Goal(
    @DocumentId val gid: String?,
    val uid: String? = null,
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

    fun getGoal(onComplete: (goal: Goal) -> Unit?) {
        collection.whereEqualTo("gid", gid).whereEqualTo("uid", uid).get().addOnSuccessListener {
            onComplete(it.first().toObject<Goal>())
        }
    }

    fun getUserGoals(onComplete: (goals: List<Goal>?) -> Unit?) {
        collection.whereEqualTo("uid", uid).get().addOnSuccessListener { snapshot ->
            onComplete(snapshot.toObjects(Goal::class.java))
        }
    }

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