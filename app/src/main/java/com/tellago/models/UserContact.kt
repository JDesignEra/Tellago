package com.tellago.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserContact(
    @DocumentId var ucid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var contactNo: String? = null
) : Parcelable {
    @IgnoredOnParcel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    @IgnoredOnParcel
    private val collection = db.collection("userContacts")

    fun add(onComplete: ((userContact: UserContact?) -> Unit)? = null) {
        collection.add(this).addOnSuccessListener {
            ucid = it.id
            onComplete?.invoke(this)
        }.addOnFailureListener {
            Log.e(this::class.java.name, "Failed to add UserContact.")
            onComplete?.invoke(null)
        }
    }
}