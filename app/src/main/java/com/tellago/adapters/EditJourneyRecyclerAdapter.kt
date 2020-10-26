package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class EditJourneyRecyclerAdapter(options: FirestoreRecyclerOptions<Post>)
    : FirestoreRecyclerAdapter<Post, EditJourneyRecyclerAdapter.PostViewHolder>(options) {
    class PostViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.post_mvc
        val postImageView = itemView.post_imageView
        val msgTextView = itemView.message_textView
        val createDateTextView = itemView.createDate_textView
    }

    private var pids = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_edit_journey_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post ) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val imgRoundedCorner = 5 * holder.itemView.resources.displayMetrics.density

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

    fun getPids(): ArrayList<String> {
        return pids
    }

    fun setPids(pids: ArrayList<String>) {
        this.pids = pids
        notifyDataSetChanged()
    }
}