package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Goal
import kotlinx.android.synthetic.main.layout_goal_list_item.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ShowGoalsRecyclerAdapter(options: FirestoreRecyclerOptions<Goal>) : FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_goal_list_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int, model: Goal) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")
        holder.tvTitle.text = model.title
        holder.tvCreationDate.text = dateFormatter.format(model.createDate)
        holder.tvFullAmt.text = DecimalFormat("$#,###").format(model.targetAmt)
    }

    class GoalViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.tv_gTitle
        val tvCreationDate = itemView.tv_gCreationDate
        val tvFullAmt = itemView.tv_gFullAmount
        val tvIcon = itemView.iv_gIcon

        init {
            itemView.btn_ShowGoalDetails.setOnClickListener {
                val pos = adapterPosition

                Toast.makeText(
                    itemView.context,
                    "You clicked on Goal item # ${pos + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}