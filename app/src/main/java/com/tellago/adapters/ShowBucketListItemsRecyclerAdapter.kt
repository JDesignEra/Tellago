package com.tellago.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.tellago.R
import com.tellago.fragments.EditBucketListItemFragment
import com.tellago.models.BucketListItem
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_show_bucket_list_items.view.*
import kotlinx.android.synthetic.main.layout_bucket_list_item.view.*


class ShowBucketListItemsRecyclerAdapter(private var itemNames: List<String>) :
    RecyclerView.Adapter<ShowBucketListItemsRecyclerAdapter.BucketListItemViewHolder>() {

    var listOfBucketItems = mutableListOf<String>()

    init {
        this.listOfBucketItems = itemNames as MutableList<String>
    }

    private var bundle: Bundle? = null
    private var deletedItemName: String = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BucketListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_bucket_list_item, parent, false)


        return BucketListItemViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: BucketListItemViewHolder,
        position: Int
    ) {
        //holder.model = model
        //holder.blItemName.text = model.blitemname
        holder.blItemName.text = itemNames[position]
    }

    override fun getItemCount(): Int {
        return itemNames.size
    }


    // function to remove/delete a bucket list item (given its position/index)
    fun delete(position : Int) {
        listOfBucketItems.removeAt(position)
        notifyDataSetChanged()
    }

    // function to add a bucket list item (given its position/index)
    fun add(position : Int, string : String) {
        listOfBucketItems.add(position, string)
        Log.d("new item added", "FIRED")
        notifyDataSetChanged()
    }

    // function to add a bucket list item (given its position/index)
    fun retrieve(position : Int) {
        listOfBucketItems[position]
        notifyDataSetChanged()
    }


    class BucketListItemViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var model: BucketListItem? = null
        val blItemName: TextView = itemView.tv_bucketList_Title
        var goal = Goal()


//        if (this.arguments != null) bundle = requireArguments()
//        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!


        init {
            val activity: AppCompatActivity = itemView.context as AppCompatActivity


            itemView.btn_ShowBucketItemDetails.setOnClickListener { v: View? ->
                val position: Int = adapterPosition
                val editBucketListItemFragment = EditBucketListItemFragment()

//                editBucketListItemFragment.arguments = Bundle().apply {
//                    putParcelable(Goal::class.java.name, model)
//                }

                editBucketListItemFragment.arguments = Bundle().apply {
                    putParcelable(goal::class.java.name, goal)
                }


                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(editBucketListItemFragment)

            }


            itemView.btn_CompleteBucketItem.setOnClickListener {
                Log.d("Completed Bucket Item", "FIRED")
            }

        }


    }



}