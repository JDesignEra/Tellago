package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.GlideApp
import com.tellago.GlideApp.init
import com.tellago.R
import com.tellago.models.Post
import com.tellago.models.User
import com.tellago.models.UserPost
import kotlinx.android.synthetic.main.layout_user_post_list_item.view.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class UserPostCommunityRecyclerAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, UserPostCommunityRecyclerAdapter.CommunityPostViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityPostViewHolder {

        return CommunityPostViewHolder(
            // Layout inflator to inflate FROM layout_user_post_list_item
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_user_post_list_item,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CommunityPostViewHolder, position: Int, model: Post) {

        holder.model = model
        holder.post_title.text = model.messageBody
        holder.like_count.text = model.likes.size.toString()
        holder.comment_count.text = model.comment.size.toString()

        // use this function to display images using Glide (one for profile pic of poster & one for any multimedia belonging to Post)
        holder.bind(model)

    }



    class CommunityPostViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var model: Post? = null

        // Setting properties to the View (reference to layout_user_post_list_item item ID)
        val user_profilePic = itemView.user_post_profile_pic
        val user_displayName = itemView.user_post_author
        val post_duration = itemView.post_duration
        val post_title = itemView.user_post_title
        val post_image = itemView.user_post_image
        val post_options = itemView.post_option_menu
        val like_count = itemView.likes
        val comment_count = itemView.comments


        val activity: AppCompatActivity = itemView.context as AppCompatActivity


        fun bind(post: Post) {
            // Assign post media
            if (post.postType == "multimedia") post.displayPostMedia(activity.application.baseContext, post_image)


            // Assign post duration
            val today = LocalDateTime.now()
            val createdDateTime =
                Instant.ofEpochMilli(model!!.createDate.time).atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            val durationStr: String

            when {
                createdDateTime.until(today, ChronoUnit.YEARS) > 0 -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.YEARS)} years ago"
                }
                createdDateTime.until(today, ChronoUnit.MONTHS) > 0 -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.MONTHS)} months ago"
                }
                createdDateTime.until(today, ChronoUnit.WEEKS) > 0 -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.WEEKS)} weeks ago"
                }
                createdDateTime.until(today, ChronoUnit.DAYS) > 0 -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.DAYS)} days ago"
                }
                createdDateTime.until(today, ChronoUnit.HOURS) > 0 -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.HOURS)} hours ago"
                }
                createdDateTime.until(today, ChronoUnit.MINUTES) > 0 -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.MINUTES)} mins ago"
                }
                else -> {
                    durationStr = "${createdDateTime.until(today, ChronoUnit.SECONDS)} secs ago"
                }
            }

            post_duration.text = durationStr


            // Assign post author and profile picture
            post.uid?.let {
                User(uid = it).getUserWithUid {
                    if (it != null) {
                        user_displayName.text = it.displayName
                        it.displayProfilePicture(itemView.context, user_profilePic)
                    }

                }
            }

        }



    }



}