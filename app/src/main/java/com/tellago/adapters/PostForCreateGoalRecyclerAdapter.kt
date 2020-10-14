package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Post
import kotlinx.android.synthetic.main.fragment_create_goal_3.*
import kotlinx.android.synthetic.main.layout_post_for_create_goal_item.view.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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
        val durationStr: String

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
            createdDateTime.until(today, ChronoUnit.MINUTES) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MINUTES)} mins ago"
            }
            else -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.SECONDS)} secs ago"
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
        }

        model.displayPostMedia(holder.itemView, holder.ivImage) {
            holder.ivImage.visibility = it
        }

        holder.cardView.setOnCheckedChangeListener { _, isChecked ->
            val btnClearSelection = (holder.itemView.context as AppCompatActivity).btn_clearSelection ?: null

            if (isChecked) {
                model.pid?.let { pid -> pids.add(pid) }
            }
            else {
                pids.remove(model.pid)
            }

            if (btnClearSelection != null) {
                if (pids.isNotEmpty()) {
                    btnClearSelection.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.colorPrimary)
                    btnClearSelection.iconTint = null
                }
                else {
                    btnClearSelection.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.superlightgray)
                    btnClearSelection.iconTint = ContextCompat.getColorStateList(holder.itemView.context, R.color.colorTextDarkGray)
                }
            }
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