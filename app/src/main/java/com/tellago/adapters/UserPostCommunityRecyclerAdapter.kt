package com.tellago.adapters

import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.progressindicator.ProgressIndicator
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.models.Post
import com.tellago.models.User
import kotlinx.android.synthetic.main.layout_user_post_list_item.view.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

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


    override fun onViewAttachedToWindow(holder: CommunityPostViewHolder) {
        super.onViewAttachedToWindow(holder)
        for (v in holder.linearLayoutPollOptions.children) {
            if (v is LinearLayout) {
                for (l in v.children) {
                    if (l is ProgressIndicator) l.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onBindViewHolder(holder: CommunityPostViewHolder, position: Int, model: Post) {

        holder.model = model
        holder.like_count.text = model.likes.size.toString()
        holder.comment_count.text = model.comment.size.toString()


        // use this function to display images using Glide (one for profile pic of poster & one for any multimedia belonging to Post)
        holder.bind(model)

        // change display of 'like_btn' if current user (viewer) has 'liked' this post before
        val viewingUserUid = Auth.user?.uid
        Log.d("The likes Array: ", model.likes.toString())
        if (viewingUserUid in model.likes)
        {
            holder.like_btn.visibility = View.GONE
            holder.like_btn_filled.visibility = View.VISIBLE

        }


        // change display layout based on post type
        holder.post_title.text = model.messageBody

        when (model.postType)
        {
            // nothing special when postType = "text post"
            "poll" -> {
                holder.linearLayoutPollOptions.visibility = View.VISIBLE

                if (model.poll.isNotEmpty()) {
                    val totalVotes = model.poll.toMutableMap().flatMap { it.value }.count()

                    for ((k, v) in model.poll) {
                        val pollOptionTextView = TextView(holder.itemView.context).apply {
                            text = "$k"
                            textSize = 16 * TypedValue.COMPLEX_UNIT_DIP.toFloat()
                            setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTextDarkGray))
                        }

                        val pollOptionHorizontalLinearLayout = LinearLayout(holder.itemView.context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                setMargins(0, 0, 0, 24 * TypedValue.COMPLEX_UNIT_DIP)
                                gravity = Gravity.CENTER_VERTICAL
                            }

                            orientation = LinearLayout.HORIZONTAL
                        }

                        val progressIndicator = ProgressIndicator(holder.itemView.context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                                gravity = Gravity.CENTER_VERTICAL
                            }
                            isIndeterminate = false
                            progress = if (v.size > 0) (v.size.toDouble() / totalVotes.toDouble() * 100).roundToInt() else 0
                            indicatorSize = 24 * TypedValue.COMPLEX_UNIT_DIP
                            indicatorCornerRadius = 24 * TypedValue.COMPLEX_UNIT_DIP
                            trackColor = ContextCompat.getColor(holder.itemView.context, R.color.colorDefaultBackground)
                            indicatorColors = intArrayOf(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                            visibility = View.VISIBLE
                            setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTransparent))
                        }

                        val percentTextView = TextView(holder.itemView.context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                setMargins(0, 0, 24 * TypedValue.COMPLEX_UNIT_DIP, 0)
                            }
                            text = "${ if (v.size > 0) (v.size.toDouble() / totalVotes.toDouble() * 100).roundToInt() else 0 }%"
                            textSize = 16 * TypedValue.COMPLEX_UNIT_DIP.toFloat()
                            setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTextLightGray))
                        }

                        pollOptionHorizontalLinearLayout.addView(percentTextView)
                        pollOptionHorizontalLinearLayout.addView(progressIndicator)

                        holder.linearLayoutPollOptions.addView(pollOptionTextView)
                        holder.linearLayoutPollOptions.addView(pollOptionHorizontalLinearLayout)
                    }
                }

            }

            "multimedia" -> {
                holder.post_image.visibility = View.VISIBLE
            }

        }


        // Change 'like_btn' uids composition based on
        // current user's clicks on the 'like_btn' & 'like_btn_filled'
        holder.like_btn.setOnClickListener {
            if (viewingUserUid != null) {
                model.addUidToLikes(viewingUserUid)
            }
            holder.like_btn.visibility = View.GONE
            holder.like_btn_filled.visibility = View.VISIBLE
            val originalLikeCount = holder.like_count.text.toString().toInt()
            holder.like_count.text = "${originalLikeCount + 1}"
        }


        holder.like_btn_filled.setOnClickListener {
            if (viewingUserUid != null) {
                model.removeUidFromLikes(viewingUserUid)
            }
            holder.like_btn_filled.visibility = View.GONE
            holder.like_btn.visibility = View.VISIBLE
            val originalLikeCount = holder.like_count.text.toString().toInt()
            holder.like_count.text = "${originalLikeCount - 1}"
        }


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
        val like_btn = itemView.like_btn
        val like_btn_filled = itemView.like_btn_filled
        val comment_btn = itemView.comment_btn
        val like_count = itemView.likes
        val comment_count = itemView.comments
        val linearLayoutPollOptions = itemView.linearLayout_pollOptions_community


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