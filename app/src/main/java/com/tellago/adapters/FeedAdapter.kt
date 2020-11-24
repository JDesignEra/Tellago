package com.tellago.adapters

import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.ProgressIndicator
import com.tellago.R
import com.tellago.activities.DisplayOtherUserActivity
import com.tellago.models.Auth
import com.tellago.models.Post
import com.tellago.models.User
import com.tellago.utilities.CustomToast
import kotlinx.android.synthetic.main.layout_user_post_list_item.view.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class FeedAdapter(private var posts: ArrayList<Post>) : RecyclerView.Adapter<FeedAdapter.FeedHolder>() {
    class FeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTxtView = itemView.user_post_author
        val postTitleTxtView = itemView.user_post_title
        val post_duration = itemView.post_duration
        val linearLayoutPollOptions = itemView.linearLayout_pollOptions_community
        val post_image = itemView.user_post_image
        val user_profilePic = itemView.user_post_profile_pic
        val like_count = itemView.likes
        val comment_count = itemView.comments
    }

    override fun onViewAttachedToWindow(holder: FeedHolder) {
        super.onViewAttachedToWindow(holder)
        for (v in holder.linearLayoutPollOptions.children) {
            if (v is LinearLayout) {
                for (l in v.children) {
                    if (l is ProgressIndicator) l.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        return FeedHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_user_post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        posts[position].uid?.let {
            User(uid = it).getUserWithUid {
                if (it != null) {
                    holder.userNameTxtView.text = it.displayName
                    it.displayProfilePicture(holder.itemView.context, holder.user_profilePic)

                    val userID = it.uid


                    // Navigate to user profile by clicking on their Profile Picture
                    holder.user_profilePic.setOnClickListener {
                        val toast: CustomToast
                        if (Auth.user?.isAnonymous!!) {
                            toast = CustomToast(it.context)
                            toast.warning("Please sign in or register to view this profile")
                        }
                        else {
                            val intent = Intent(it.context, DisplayOtherUserActivity::class.java)
                            // use intent.putExtra to pass the unique user ID to be displayed
                            val intendedUserID = userID
                            intent.putExtra("userID", intendedUserID)

                            it.context.startActivity(intent)

                        }
                    }

                    // Navigate to user profile by clicking on their Display Name
                    holder.userNameTxtView.setOnClickListener {
                        val toast: CustomToast
                        if (Auth.user?.isAnonymous!!) {
                            toast = CustomToast(it.context)
                            toast.warning("Please sign in or register to view this profile")
                        }
                        else {
                            val intent = Intent(it.context, DisplayOtherUserActivity::class.java)
                            // use intent.putExtra to pass the unique user ID to be displayed
                            val intendedUserID = userID
                            intent.putExtra("userID", intendedUserID)

                            it.context.startActivity(intent)

                        }
                    }
                    
                }
            }
        }

        holder.like_count.text = posts[position].likes.size.toString()
        holder.comment_count.text = posts[position].comment.size.toString()
        holder.postTitleTxtView.text = posts[position].messageBody

        when (posts[position].postType) {
            "poll" -> {
                holder.linearLayoutPollOptions.visibility = View.VISIBLE

                if (posts[position].poll.isNotEmpty()) {
                    val totalVotes = posts[position].poll.toMutableMap().flatMap { it.value }.count()

                    for ((k, v) in posts[position].poll) {
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

        val today = LocalDateTime.now()
        val createdDateTime = Instant.ofEpochMilli(posts[position].createDate.time).atZone(ZoneId.systemDefault()).toLocalDateTime()

        when {
            createdDateTime.until(today, ChronoUnit.YEARS) > 0 -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.YEARS)} years ago"
            }
            createdDateTime.until(today, ChronoUnit.MONTHS) > 0 -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.MONTHS)} months ago"
            }
            createdDateTime.until(today, ChronoUnit.WEEKS) > 0 -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.WEEKS)} weeks ago"
            }
            createdDateTime.until(today, ChronoUnit.DAYS) > 0 -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.DAYS)} days ago"
            }
            createdDateTime.until(today, ChronoUnit.HOURS) > 0 -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.HOURS)} hours ago"
            }
            createdDateTime.until(today, ChronoUnit.MINUTES) > 0 -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.MINUTES)} mins ago"
            }
            else -> {
                holder.post_duration.text = "${createdDateTime.until(today, ChronoUnit.SECONDS)} secs ago"
            }
        }


    }

    override fun getItemCount(): Int {
        return posts.size
    }
}