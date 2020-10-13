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
        holder.tvPostDuration.text = model.createDate.toString()
        holder.tvMsg.text = model.messageBody
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
}