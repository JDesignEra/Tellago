package com.tellago.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tellago.R
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_user_post_list_item.view.*

class FeedAdapter(private var posts: ArrayList<Post>) : RecyclerView.Adapter<FeedAdapter.FeedHolder>() {
    class FeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTxtView = itemView.user_post_author
        val postTitleTxtView = itemView.user_post_title
        val durationTxtView = itemView.post_duration
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        Log.e(this::class.java.name, "Adapter FIred")
        return FeedHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_user_post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        holder.userNameTxtView.text = posts[position].messageBody
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}