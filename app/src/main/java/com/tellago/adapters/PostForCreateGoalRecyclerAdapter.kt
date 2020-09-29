package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tellago.R
import kotlinx.android.synthetic.main.layout_post_for_create_goal_item.view.*

class PostForCreateGoalRecyclerAdapter (private var titles: List<String>, private var descriptions: List<String>, private var owners: List<String>, private var images:List<Int>) :
RecyclerView.Adapter<PostForCreateGoalRecyclerAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle : TextView = itemView.textView_title
        val itemDesc : TextView = itemView.textView_description
        val itemOwner : TextView = itemView.textView_owner
        val itemProfilePicture : ImageView = itemView.imageView_post_image

        init {
            itemView.setOnClickListener { v: View? ->
                val position: Int = adapterPosition
                Toast.makeText(
                    itemView.context,
                    "You clicked on Post item # ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_post_for_create_goal_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemDesc.text = descriptions[position]
        holder.itemOwner.text = owners[position]
        holder.itemProfilePicture.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

}