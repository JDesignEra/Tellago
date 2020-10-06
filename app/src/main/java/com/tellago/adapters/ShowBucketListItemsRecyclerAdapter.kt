package com.tellago.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.tellago.R
import com.tellago.fragments.EditBucketListItemFragment
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.parcel.RawValue
import kotlinx.android.synthetic.main.layout_bucket_list_item.view.*

class ShowBucketListItemsRecyclerAdapter(
    private var model: Goal,
    private val completed: Boolean = false
) : RecyclerView.Adapter<ShowBucketListItemsRecyclerAdapter.BucketListItemViewHolder>() {
    private val filteredList: MutableList<MutableMap<String, @RawValue Any>> = model.bucketList
        .toMutableList()
        .filterIndexed() { idx, bucketItem ->
            if (completed == bucketItem["completed"]) bucketItem["idx"] = idx
            completed == bucketItem["completed"]
        }.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bucket_list_item, parent, false)
        return BucketListItemViewHolder(view, model, completed)
    }

    override fun onBindViewHolder(holder: BucketListItemViewHolder, position: Int) {
        holder.tvTitle.text = filteredList[position]["name"] as String
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun insert(position: Int, item: Map<String, Any>, goal: Goal) {
//        this.goal = goal
        filteredList.add(item.toMutableMap())

        notifyDataSetChanged()
    }

    fun remove(position: Int, item: Map<String, Any>, goal: Goal) {
//        this.goal = goal
        filteredList.removeAt(position)

        notifyDataSetChanged()
    }

    fun change(position: Int, item: MutableMap<String, Any>, goal: Goal) {
        if (position < filteredList.size) {
            Log.e("Check", item["idx"].toString())
            Log.e("pos", position.toString())
            filteredList[position] = item
        }
//        this.goal = goal

        notifyItemChanged(position)
    }

//    // function to remove/delete a bucket list item (given its position/index)
//    fun delete(position : Int) {
//        goal.bucketList.removeAt(position)
//        notifyDataSetChanged()
//    }
//
//    // function to add a bucket list item (given its position/index)
//    fun add(position : Int, item : MutableMap<String, Any>?) {
//        goal.bucketList.add(position, item)
//        notifyDataSetChanged()
//    }
//
//    // function to retrieve & return bucket list item's name value as a String
//    fun retrieve(position : Int): String {
//        return goal.bucketList[position]?.get("name") as String
//    }

    class BucketListItemViewHolder constructor(
        itemView: View,
        model: Goal,
        completed: Boolean
    ) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.tv_bucketList_Title

        init {
            itemView.btn_ShowBucketItemDetails.setOnClickListener {
                val filteredList: MutableList<MutableMap<String, @RawValue Any>> = model.bucketList
                    .toMutableList()
                    .filterIndexed() { idx, bucketItem ->
                        if (completed == bucketItem["completed"]) bucketItem["idx"] = idx
                        completed == bucketItem["completed"]
                    }.toMutableList()

                val activity: AppCompatActivity = itemView.context as AppCompatActivity

                val editBucketListItemFragment = EditBucketListItemFragment()
                Log.e("Holder", filteredList[adapterPosition]["idx"].toString())

                editBucketListItemFragment.arguments = Bundle().apply {
                    putParcelable(model::class.java.name, model)
                    putInt("originalBid", filteredList[adapterPosition]["idx"] as Int)
                    putInt("filteredBid", adapterPosition)
                }

                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(
                    editBucketListItemFragment,
                    enter = R.anim.fragment_open_enter,
                    exit = R.anim.fragment_open_exit,
                    popEnter = R.anim.fragment_close_enter,
                    popExit = R.anim.fragment_close_exit
                )
            }
        }
    }
}