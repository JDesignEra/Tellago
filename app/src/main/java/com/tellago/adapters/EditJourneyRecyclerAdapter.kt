package com.tellago.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_edit_journey_post.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditJourneyRecyclerAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, EditJourneyRecyclerAdapter.PostViewHolder>(options) {
    class PostViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.post_mvc
        val postImageView = itemView.post_imageView
        val msgTextView = itemView.message_textView
        val layout_poll_option_parent = itemView.linear_layout_post_poll_option_parent_edit_journey
        val layout_poll_options = itemView.linear_layout_post_poll_options_edit_journey
        val layout_poll_option_progressbar =
            itemView.linear_layout_post_poll_progressbar_edit_journey
        val createDateTextView = itemView.createDate_textView
    }

    private var pids = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_edit_journey_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val imgRoundedCorner = 5 * holder.itemView.resources.displayMetrics.density

        // Adjust layout to display based on postType
        when (model.postType) {
            "text post" -> {
                holder.cardView.isChecked = pids.find { it == model.pid } != null
                holder.cardView.setOnClickListener {
                    holder.cardView.toggle()
                }

                holder.postImageView.visibility = View.GONE
                holder.msgTextView.text = model.messageBody
                holder.createDateTextView.text = dateFormatter.format(model.createDate)

                holder.cardView.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) model.pid?.let { pid -> pids.add(pid) }
                    else pids.remove(model.pid)
                }
            }

            "poll" -> {

                holder.cardView.isChecked = pids.find { it == model.pid } != null
                holder.cardView.setOnClickListener {
                    holder.cardView.toggle()
                }

//                holder.msgTextView.text = "Poll below"
                holder.msgTextView.visibility = View.GONE
                holder.postImageView.visibility = View.GONE

                // Adjust layouts to display poll options and votes
                holder.layout_poll_option_parent.visibility = View.VISIBLE
                holder.layout_poll_options.visibility = View.VISIBLE
                holder.layout_poll_option_progressbar.visibility = View.VISIBLE

                //Start of logic to display Poll options with votes

                // check model.poll for number of elements
                if (model.poll.isNotEmpty()) {
                    // to store element values
                    val pollElementsList: ArrayList<String> = ArrayList()
                    val pollVotesList: ArrayList<Int> = ArrayList()
                    var totalVoteCount = 0
                    // iterating
                    for (stuffLayer1 in model.poll) {
                        Log.d("printing Pt1: ", stuffLayer1.toString())

                        for (Layer2 in stuffLayer1) {
                            Log.d("printing Pt2: ", Layer2.toString())
                            val splitElements = Layer2.toString().split("=")
                            pollElementsList.add(splitElements[0])
                            Log.d("index 1: ", splitElements[1])
                            if (splitElements[1] == "[]") {
                                pollVotesList.add(0)
                            } else {

                                val splitUserVotes = splitElements[1].split(",")
                                Log.d("splitUserVotes is: ", splitUserVotes.toString())
                                var noOfUserID = 0
                                for (userID in splitUserVotes) {
                                    noOfUserID += 1
                                    Log.d("userID is: ", userID)
                                }
                                pollVotesList.add(noOfUserID)
                                totalVoteCount += noOfUserID
                            }
                            Log.d("totalVoteCount: ", totalVoteCount.toString())
                            Log.d("printing splitEle: ", splitElements.toString())
                        }

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

                        if (totalVoteCount == 0) {
                            holder.layout_poll_option_progressbar.addView(
                                populateNewPollVotesAsText(
                                    holder.itemView.context,
                                    pollVotesList[iterateVal],
                                    totalVoteCount
                                )
                            )
                        } else {
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

                //End of logic to display Poll options with votes

                holder.createDateTextView.text = dateFormatter.format(model.createDate)

                holder.cardView.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) model.pid?.let { pid -> pids.add(pid) }
                    else pids.remove(model.pid)
                }
            }

            "multimedia" -> {
                model.displayPostMedia(
                    holder.itemView,
                    holder.postImageView,
                    GranularRoundedCorners(imgRoundedCorner, 0.0F, 0.0F, imgRoundedCorner),
                    CenterInside()
                ) {
                    holder.postImageView.visibility = it
                }

                holder.cardView.isChecked = pids.find { it == model.pid } != null
                holder.cardView.setOnClickListener {
                    holder.cardView.toggle()
                }

                holder.msgTextView.text = model.messageBody
                holder.createDateTextView.text = dateFormatter.format(model.createDate)

                holder.cardView.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) model.pid?.let { pid -> pids.add(pid) }
                    else pids.remove(model.pid)
                }
            }

        }

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
        progressBarOptionVote.setBackgroundResource(R.color.colorWhiteBackground)
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


    fun getPids(): ArrayList<String> {
        return pids
    }

    fun setPids(pids: ArrayList<String>) {
        this.pids = pids
        notifyDataSetChanged()
    }
}