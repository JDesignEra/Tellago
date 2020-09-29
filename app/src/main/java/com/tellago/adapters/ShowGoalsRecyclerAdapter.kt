package com.tellago.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Goal
import kotlinx.android.synthetic.main.layout_goal_list_item.view.*
import java.text.DecimalFormat
import java.time.Month


class ShowGoalsRecyclerAdapter(options: FirestoreRecyclerOptions<Goal>) :
    FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>(options) {

    // Constructor for GoalViewHolder (inner class)
    class GoalViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){
        val tv_gCreationDate: TextView = itemView.tv_gCreationDate

        //        val tv_gCurrentAmount: TextView = itemView.tv_gCurrentAmount
//        val tv_gDeadline: TextView = itemView.tv_gDeadline
        val tv_gFullAmount: TextView = itemView.tv_gFullAmount
        val iv_gIcon: ImageView = itemView.iv_gIcon

        //        val tv_gLastReminder: TextView = itemView.tv_gLastReminder
//        val tv_gOwner: TextView = itemView.tv_gOwner
//        val tv_gProgressTracker: TextView = itemView.tv_gProgressTracker
//        val tv_gReminderFreq: TextView = itemView.tv_gReminderFreq
        val tv_gTitle: TextView = itemView.tv_gTitle
//        val tv_goalid: TextView = itemView.tv_gIcon


//        val btnShowGoalDetails : Button = itemView.btn_ShowGoalDetails

        init {
            itemView.btn_ShowGoalDetails.setOnClickListener { v: View? ->
                val position: Int = adapterPosition
                Toast.makeText(
                    itemView.context,
                    "You clicked on Goal item # ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ShowGoalsRecyclerAdapter.GoalViewHolder,
        position: Int,
        model: Goal
    ) {


        // Display as Mon Sep 28 17:08:48 GMT 2020
        val timestamp_firebase = model.createDate

        // Displays as 28-8-120 without offset consideration
        val local_date: String = timestamp_firebase.date.toString() +
                "-" + Month.of(timestamp_firebase.month + 1) +
                "-" + (timestamp_firebase.year + 1900).toString()


        holder.tv_gCreationDate.setText(local_date)


//        holder.tv_gCurrentAmount.text = model.gCurrentAmount.toString()
//        holder.tv_gDeadline.text = model.gDeadline.toString()


        holder.tv_gFullAmount.setText(DecimalFormat("$#,###").format(model.targetAmt))
        holder.iv_gIcon.setImageResource(R.drawable.ic_account_box_48_primary)
//        holder.tv_gLastReminder.text = model.gLastReminder


//        holder.tv_gOwner.text = model.gOwner

//        holder.tv_gReminderFreq.text = model.gReminderFreq

        holder.tv_gTitle.text = model.title

//        holder.tv_goalid.text = model.goalid


    }

}