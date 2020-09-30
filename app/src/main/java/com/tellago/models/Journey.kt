package com.tellago.models

import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

data class Journey(
    @DocumentId var jid: String? = null,
    var pids: List<String>? = null
) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("journeys")

    fun add(onComplete: ((journey: Journey?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            jid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            onComplete?.invoke(null)
        }
    }

    fun getByJid(onComplete: ((journey: Journey?) -> Unit)? = null) {
        if (jid != null) {
            collection.document(jid!!).get().addOnSuccessListener {
                // TODO: Retrieve posts by PID and return a list of Post class object

                onComplete?.invoke(it.toObject<Journey>())
            }.addOnFailureListener {
                onComplete?.invoke(null)
                Log.e("Journey", "Failed to get Journey by JID.")
            }
        }
        else Log.e("Journey", "JID is required for getByJid().")
    }

    fun updateByJid(addOnList: List<String>, onComplete: ((journey: Journey?) -> Unit)? = null) {
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
                        Log.e("Journey", "Failed to update Journey")
                        onComplete?.invoke(null)
                    }
                }
            }
        }
        else Log.e("Journey", "JID is required for updateByJid().")
    }

    fun deleteByJid() {
        if (jid != null) collection.document(jid!!).delete()
        else Log.e("Journey", "JID is required for deleteByJid().")
    }
}