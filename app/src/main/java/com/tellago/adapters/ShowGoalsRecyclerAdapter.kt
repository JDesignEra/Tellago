package com.tellago.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.fragments.ShowGoalDetailsFragment
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
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
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("en", "SG"))
        dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Singapore")

        holder.model = model
        holder.tvTitle.text = model.title
        holder.tvCreationDate.text = dateFormatter.format(model.createDate)
        holder.tvFullAmt.text = DecimalFormat("$#,###.##").format(model.targetAmt)
    }

    class GoalViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var model: Goal? = null
        val tvTitle = itemView.tv_gTitle
        val tvCreationDate = itemView.tv_gCreationDate
        val tvFullAmt = itemView.tv_gFullAmount
        val tvIcon = itemView.iv_gIcon

        init {
            val activity : AppCompatActivity = itemView.context as AppCompatActivity

            itemView.btn_ShowGoalDetails.setOnClickListener { v: View? ->
                val showGoalDetailsFragment = ShowGoalDetailsFragment()

                showGoalDetailsFragment.arguments = Bundle().apply {
                    putParcelable(Goal::class.java.name, model)
                }

                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(showGoalDetailsFragment, false)
            }
        }
    }
}