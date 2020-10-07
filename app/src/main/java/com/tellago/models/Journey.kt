package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Journey(
    @DocumentId var jid: String? = null,
    var title: String = "My Journey",
    var pids: ArrayList<String>? = ArrayList()

) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    @IgnoredOnParcel
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
                Log.e(this::class.java.name, "Failed to get Journey by JID.")
            }
        }
        else Log.e(this::class.java.name, "JID is required for getByJid().")
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
                        Log.e(this::class.java.name, "Failed to update Journey")
                        onComplete?.invoke(null)
                    }
                }
            }
        }
        else Log.e(this::class.java.name, "JID is required for updateByJid().")
    }

    fun deleteByJid() {
        if (jid != null) collection.document(jid!!).delete()
        else Log.e(this::class.java.name, "JID is required for deleteByJid().")
    }
}