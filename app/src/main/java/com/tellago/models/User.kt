package com.tellago.models

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.tellago.GlideApp
import com.tellago.R
import java.io.File
import java.net.URI

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val bio: String? = null,
    val followerUids : ArrayList<String> = ArrayList(),
    val followingUids : ArrayList<String> = ArrayList()
) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")
    private val storage = FirebaseStorage.getInstance("gs://tellago.appspot.com")
    private val storageRef = storage.reference

    fun getUserWithUid(onComplete: (user: User?) -> Unit) {
        collection.document(uid).get().addOnSuccessListener {
            onComplete(it.toObject<User>())
        }
    }

    fun update(): User? {
        collection.document(uid).set(this)
        return this
    }

    // followerUid refers to the uid of the user who initiated 'Follow' action
    // followedUserUid refers to the uid of the user who is being 'followed'
    // both will have an update in a record field after 'Follow'/'Unfollow' action
    fun userFollowUser(followerUid: String, followedUserUid: String, onComplete: ((user: User?) -> Unit)? = null) {
        // Step 1: Update the followerUids of user being 'followed'
        collection.document(followedUserUid).get().addOnSuccessListener {
            if (it != null) {
                var followerUids = it.toObject<User>()?.followerUids?.toMutableList()

                if (followerUids != null) {
                    // check for 'unfollow' & the corresponding code
                    if (followerUids.contains(followerUid))
                    {
                        // Now remove uid of the user who initiated 'Unfollow'
                        followerUids.remove(followerUid)
                    }

                    // code for 'follow'
                    else {
                        // Add the uid of the user who initiated 'Follow'
                        if (!followerUids.isNullOrEmpty()) {
                            followerUids.add(followerUid)
                            followerUids.distinct().toList()
                        } else {
                            followerUids = mutableListOf(followerUid)
                        }
                    }
                }

                collection.document(uid).update("followerUids", followerUids).addOnSuccessListener {
                    onComplete?.invoke(this)
                }.addOnFailureListener {
                    Log.e(this::class.java.name, "Failed to update list of Followers (Uids) for followed User")
                    onComplete?.invoke(null)
                }
            }
        }

        // Step 2: Update the followingUids of user who initiated 'Follow'
        collection.document(followerUid).get().addOnSuccessListener {
            if (it != null) {
                var followingUids = it.toObject<User>()?.followingUids?.toMutableList()

                if (followingUids != null) {
                    // check for 'unfollow' & the corresponding code
                    if (followingUids.contains(followedUserUid))
                    {
                        // Now remove uid of the user who initiated 'Unfollow'
                        followingUids.remove(followedUserUid)
                    }

                    // code for 'follow'
                    else {
                        // Add the uid to follow to the 'followingUids' for the user who initiated 'Follow'
                        if (!followingUids.isNullOrEmpty()) {
                            followingUids.add(followedUserUid)
                            followingUids.distinct().toList()
                        } else {
                            followingUids = mutableListOf(followedUserUid)
                        }
                    }
                }

                collection.document(followerUid).update("followingUids", followingUids).addOnSuccessListener {
                    onComplete?.invoke(this)
                }.addOnFailureListener {
                    Log.e(this::class.java.name, "Failed to update list of Following (Uids) by initiating User")
                    onComplete?.invoke(null)
                }
            }
        }

    }




    fun displayProfilePicture(
        context: Context,
        imageView: ImageView,
        vararg transforms: Transformation<Bitmap>
    ) {
        GlideApp.with(context)
            .load(storageRef.child("uploads/dp/$uid"))
            .apply {
                if (transforms.isNotEmpty()) transform(*transforms)
                else transform(CircleCrop(), CenterInside())

                error(R.drawable.ic_android_photo)
                diskCacheStrategy(DiskCacheStrategy.NONE)
                skipMemoryCache(true)
            }.into(imageView)
    }

    fun uploadDp(uri: Uri): UploadTask {
        val file = Uri.fromFile((File(URI.create(uri.toString()))))

        return storageRef.child("uploads/dp/$uid").putFile(file)
    }
}