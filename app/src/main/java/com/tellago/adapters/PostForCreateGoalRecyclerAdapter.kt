package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_post_for_create_goal_item.view.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class PostForCreateGoalRecyclerAdapter (options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, PostForCreateGoalRecyclerAdapter.PostViewHolder>(options) {
    private val pids = ArrayList<String>()

    class PostViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPostDuration = itemView.tv_post_duration
        val tvMsg = itemView.tv_msg
        val ivImage = itemView.iv_image
        val vvVideo = itemView.vv_video
        val cardView = itemView.cardView_post
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post_for_create_goal_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        val today = LocalDateTime.now()
        val createdDateTime = Instant.ofEpochMilli(model.createDate.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        var durationStr = "0 mins ago"

        when {
            createdDateTime.until(today, ChronoUnit.YEARS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.YEARS)} years ago"
            }
            createdDateTime.until(today, ChronoUnit.MONTHS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MONTHS)} months ago"
            }
            createdDateTime.until(today, ChronoUnit.WEEKS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MONTHS)} weeks ago"
            }
            createdDateTime.until(today, ChronoUnit.DAYS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.DAYS)} days ago"
            }
            createdDateTime.until(today, ChronoUnit.HOURS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.HOURS)} hours ago"
            }
            else -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MINUTES)} mins ago"
            }
        }

        holder.tvPostDuration.text = durationStr
        holder.tvMsg.text = model.messageBody

        if (!model.messageBody.isNullOrBlank()) holder.tvMsg.visibility = View.VISIBLE

        if (pids.find { it == model.pid } == null) {
            holder.cardView.isChecked = false
        }

        holder.cardView.setOnClickListener {
            holder.cardView.toggle()

            model.pid?.let { pid -> pids.add(pid) }
        }

        // TODO: implement multimedia
        if (model.multimediaURI != null) {
            holder.ivImage.visibility = View.VISIBLE
        }

        // TODO: implement video
        if (model.multimediaURI != null) {
            holder.vvVideo.visibility = View.VISIBLE
        }
    }

    fun getPids(): ArrayList<String> {
        return pids
    }

    fun clearSelections() {
        pids.clear()
        notifyDataSetChanged()
    }
}