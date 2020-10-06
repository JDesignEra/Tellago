package com.tellago.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.tellago.R
import com.tellago.fragments.EditBucketListItemFragment
import com.tellago.models.BucketListItem
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.layout_bucket_list_item.view.*

class ShowBucketListItemsRecyclerAdapter(private val goal: Goal) : RecyclerView.Adapter<ShowBucketListItemsRecyclerAdapter.BucketListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bucket_list_item, parent, false)
        return BucketListItemViewHolder(view, goal)
    }

    override fun onBindViewHolder(holder: BucketListItemViewHolder, position: Int) {
        holder.tvTitle.text = goal.bucketList[position]?.get("name") as String
    }

    override fun getItemCount(): Int {
        return goal.bucketList.size
    }

    // function to remove/delete a bucket list item (given its position/index)
    fun delete(position : Int) {
        goal.bucketList.removeAt(position)
        notifyDataSetChanged()
    }

    // function to add a bucket list item (given its position/index)
    fun add(position : Int, item : MutableMap<String, Any>?) {
        goal.bucketList.add(position, item)
        notifyDataSetChanged()
    }

    // function to retrieve & return bucket list item's name value as a String
    fun retrieve(position : Int): String {

        return goal.bucketList[position]?.get("name") as String
    }

    class BucketListItemViewHolder constructor(itemView: View, model: Goal) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.tv_bucketList_Title
        private val activity: AppCompatActivity = itemView.context as AppCompatActivity

        init {
            itemView.btn_ShowBucketItemDetails.setOnClickListener { v: View? ->
                val editBucketListItemFragment = EditBucketListItemFragment()
                editBucketListItemFragment.arguments = Bundle().apply {
                    putParcelable(model::class.java.name, model)
                    putInt("bid", adapterPosition)
                }

                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(editBucketListItemFragment)
            }
        }
    }
}