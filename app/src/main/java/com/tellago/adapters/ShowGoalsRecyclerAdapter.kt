package com.tellago.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Goal
import kotlinx.android.synthetic.main.layout_goal_list_item.view.*

class ShowGoalsRecyclerAdapter(options: FirestoreRecyclerOptions<Goal>) :
    FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>(options) {


    // Constructor for GoalViewHolder
    class GoalViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tv_gCreationDate: TextView = itemView.tv_gCreationDate
        val tv_gCurrentAmount: TextView = itemView.tv_gCurrentAmount
        val tv_gDeadline: TextView = itemView.tv_gDeadline
        val tv_gFullAmount: TextView = itemView.tv_gFullAmount
        val tv_gIcon: TextView = itemView.tv_gIcon
        val tv_gLastReminder: TextView = itemView.tv_gLastReminder
        val tv_gOwner: TextView = itemView.tv_gOwner
        val tv_gProgressTracker: TextView = itemView.tv_gProgressTracker
        val tv_gReminderFreq: TextView = itemView.tv_gReminderFreq
        val tv_gTitle: TextView = itemView.tv_gTitle
        val tv_goalid: TextView = itemView.tv_gIcon

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShowGoalsRecyclerAdapter.GoalViewHolder {
        Log.d("GoalViewHolder", "FIRED - returned")
        return GoalViewHolder(
            // Layout Inflator to inflate FROM layout_goal_list_item.xml
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_goal_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }




    override fun onBindViewHolder(
        holder: ShowGoalsRecyclerAdapter.GoalViewHolder,
        position: Int,
        model: Goal
    ) {
        holder.tv_gCreationDate.text = model.gCreationDate.toString()
        holder.tv_gCurrentAmount.text = model.gCurrentAmount.toString()
        holder.tv_gDeadline.text = model.gDeadline.toString()
        holder.tv_gFullAmount.text = model.gFullAmount.toString()
        holder.tv_gIcon.text = model.gIcon
        holder.tv_gLastReminder.text = model.gLastReminder
        holder.tv_gOwner.text = model.gOwner
        holder.tv_gProgressTracker.text = model.gProgressTracker
        holder.tv_gReminderFreq.text = model.gReminderFreq
        holder.tv_gTitle.text = model.gTitle
        holder.tv_goalid.text = model.goalid


    }


}