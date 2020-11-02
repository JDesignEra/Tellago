package com.tellago.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R.color
import com.tellago.R.layout
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_new_post_list_item.view.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


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

    override fun onBindViewHolder(holder: NewPostViewHolder, position: Int, model: Post) {
        holder.model = model

//        holder.bind(items[position])

        // Use NewPostRecyclerAdapter to handle conditional for displaying Posts based on PostType
        when (model.postType) {
            "text post" -> {
                holder.post_title.visibility = View.VISIBLE
                holder.post_title.text = model.messageBody
            }

            "poll" -> {
                holder.post_title.visibility = View.VISIBLE
                val pollQn = model.messageBody
                holder.post_title.text = "Poll Question: $pollQn"
                holder.post_pollOption.visibility = View.VISIBLE
//                holder.post_pollOption.text = model.poll.toString()
                holder.post_pollOption.text = ""
                holder.layout_poll_options.visibility = View.VISIBLE
                holder.layout_poll_option_progressbar.visibility = View.VISIBLE

                // check model.poll for number of elements
                if (model.poll.isNotEmpty()) {
                    // to store element values
                    val pollElementsList: ArrayList<String> = ArrayList()
                    val pollVotesList: ArrayList<Int> = ArrayList()
                    var totalVoteCount = 0
                    // iterating
                    for (m in model.poll) {
                        totalVoteCount += m.value.size
                        pollVotesList.add(m.value.size)
                    }
                    Log.d("print elements", pollElementsList.toString())

                    var iterateVal = 0
                    // Iterate through elements in pollElementsList to dynamically populate linear_layout_poll_options
                    for (element in pollElementsList) {
                        holder.layout_poll_options.addView(
                            populateNewPollOptionTextView(
                                holder.itemView.context,
                                element
                            )
                        )
                        
                        if (totalVoteCount == 0)
                        {
                            holder.layout_poll_option_progressbar.addView(
                                populateNewPollVotesAsText(
                                    holder.itemView.context,
                                    pollVotesList[iterateVal],
                                    totalVoteCount
                                )
                            )
                        } else
                        {
                            holder.layout_poll_option_progressbar.addView(
                                populateNewPollVotesProgressBar(
                                    holder.itemView.context,
                                    pollVotesList[iterateVal],
                                    totalVoteCount
                                )
                            )
                        }

                        iterateVal += 1
                        Log.d("new layout created", "FIRED")
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
        holder.likes.text = model.likes.size.toString()
        // need to display comments individually instead of as an entire ArrayList<String>
        holder.comments.text = model.comment.toString()

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
//        progressBarOptionVote.setBackgroundColor(color.colorWarning)
//        progressBarOptionVote.background = drawable.gradient_background.toDrawable()
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
        val post_pollOption: TextView = itemView.new_post_pollOption
        val post_duration: TextView = itemView.new_post_duration
        val layout_poll_options = itemView.linear_layout_post_poll_options
        val layout_poll_option_progressbar = itemView.linear_layout_post_poll_progressbar
        val likes: TextView = itemView.new_post_likes
        val comments: TextView = itemView.new_post_comments


        val activity: AppCompatActivity = itemView.context as AppCompatActivity


        fun bind(post: Post) {
            if (post.postType == "multimedia") post.displayPostMedia(activity.application.baseContext, post_image)
        }
    }
}
