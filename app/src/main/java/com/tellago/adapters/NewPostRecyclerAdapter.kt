package com.tellago.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.progressindicator.ProgressIndicator
import com.tellago.R.color
import com.tellago.R.layout
import com.tellago.models.Auth
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_new_post_list_item.view.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt


class NewPostRecyclerAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, NewPostRecyclerAdapter.NewPostViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPostViewHolder {
        return NewPostViewHolder(
            // Layout inflator to inflate FROM layout_new_post_list_item
            LayoutInflater.from(parent.context).inflate(
                layout.layout_new_post_list_item,
                parent,
                false
            )
        )
    }

    override fun onViewAttachedToWindow(holder: NewPostViewHolder) {
        super.onViewAttachedToWindow(holder)
        for (v in holder.linearLayoutPollOptions.children) {
            if (v is LinearLayout) {
                for (l in v.children) {
                    if (l is ProgressIndicator) l.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onBindViewHolder(holder: NewPostViewHolder, position: Int, model: Post) {
        holder.model = model
        holder.like_count.text = model.likes.size.toString()
        holder.comment_count.text = model.comment.size.toString()


        // change display of 'like_btn' if current user (viewer) has 'liked' this post before
        val viewingUserUid = Auth.user?.uid
        if (viewingUserUid in model.likes)
        {
            holder.like_btn.visibility = View.GONE
            holder.like_btn_filled.visibility = View.VISIBLE

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


        // Use NewPostRecyclerAdapter to handle conditional for displaying Posts based on PostType
        when (model.postType) {
            "text post" -> {
                holder.post_title.visibility = View.VISIBLE
                holder.post_title.text = model.messageBody
            }
            "poll" -> {
                holder.post_title.visibility = View.VISIBLE
                val pollQn = model.messageBody
                holder.post_title.text = "$pollQn"
                holder.linearLayoutPollOptions.visibility = View.VISIBLE

                if (model.poll.isNotEmpty()) {
                    val totalVotes = model.poll.toMutableMap().flatMap { it.value }.count()

                    for ((k, v) in model.poll) {
                        val pollOptionTextView = TextView(holder.itemView.context).apply {
                            text = "$k"
                            textSize = 16 * TypedValue.COMPLEX_UNIT_DIP.toFloat()
                            setTextColor(ContextCompat.getColor(holder.itemView.context, color.colorTextDarkGray))
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
                            trackColor = ContextCompat.getColor(holder.itemView.context, color.colorDefaultBackground)
                            indicatorColors = intArrayOf(ContextCompat.getColor(holder.itemView.context, color.colorPrimary))
                            visibility = View.VISIBLE
                            setBackgroundColor(ContextCompat.getColor(holder.itemView.context, color.colorTransparent))
                        }

                        val percentTextView = TextView(holder.itemView.context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                setMargins(0, 0, 24 * TypedValue.COMPLEX_UNIT_DIP, 0)
                            }
                            text = "${ if (v.size > 0) (v.size.toDouble() / totalVotes.toDouble() * 100).roundToInt() else 0 }%"
                            textSize = 16 * TypedValue.COMPLEX_UNIT_DIP.toFloat()
                            setTextColor(ContextCompat.getColor(holder.itemView.context, color.colorTextLightGray))
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
                holder.post_title.visibility = View.VISIBLE
                holder.post_title.text = model.messageBody
            }
        }

        val today = LocalDateTime.now()
        val createdDateTime =
            Instant.ofEpochMilli(model.createDate.time).atZone(ZoneId.systemDefault())
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

        holder.post_duration.text = durationStr
        holder.like_count.text = model.likes.size.toString()
        // need to display comments individually instead of as an entire ArrayList<String>
        holder.comment_count.text = model.comment.size.toString()

        // use this function to display images using Glide (one for profile pic of poster & one for any multimedia belonging to Post)
        holder.bind(model)

    }


    private fun populateNewPollOptionTextView(context: Context, userOptionInput: String): TextView {
        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        val textViewPollOption = TextView(context)
        textViewPollOption.text = userOptionInput
        textViewPollOption.maxLines = 3

        textViewPollOption.layoutParams = lparams

        return textViewPollOption
    }

    @SuppressLint("ResourceAsColor")
    private fun populateNewPollVotesProgressBar(
        context: Context,
        optionVote: Int,
        totalVoteCount: Int
    ): ProgressBar {

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        val progressBarOptionVote =
            ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressBarOptionVote.setBackgroundResource(color.colorWhiteBackground)
        progressBarOptionVote.isIndeterminate = false
        progressBarOptionVote.scaleY = 1.toFloat()
        progressBarOptionVote.max = totalVoteCount
        progressBarOptionVote.progress = optionVote
        progressBarOptionVote.layoutParams = lparams

        return progressBarOptionVote
    }

    @SuppressLint("SetTextI18n")
    private fun populateNewPollVotesAsText(
        context: Context,
        optionVote: Int,
        totalVoteCount: Int
    ): TextView {
        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        val textViewPollOption = TextView(context)
        textViewPollOption.text = "$optionVote out of $totalVoteCount votes"

        textViewPollOption.layoutParams = lparams

        return textViewPollOption
    }

    // Constructor for Post ViewHolder
    class NewPostViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var model: Post? = null

        // Setting properties to the View (reference to layout_new_post_list_item item ID)
        val post_image: ImageView = itemView.new_post_image
        val post_title: TextView = itemView.new_post_title
        val post_duration: TextView = itemView.new_post_duration
        val like_btn: ImageView = itemView.new_post_like_btn
        val like_btn_filled: ImageView = itemView.new_post_like_btn_filled
        val comment_btn: ImageView = itemView.new_post_comment_btn
        val like_count: TextView = itemView.new_post_likes
        val comment_count: TextView = itemView.new_post_comments
        val linearLayoutPollOptions: LinearLayout = itemView.linearLayout_pollOptions

        val activity: AppCompatActivity = itemView.context as AppCompatActivity

        fun bind(post: Post) {
            if (post.postType == "multimedia") post.displayPostMedia(activity.application.baseContext, post_image)
        }
    }
}
