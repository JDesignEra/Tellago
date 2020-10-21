package com.tellago.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.fragments.ShowGoalDetailsFragment
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.layout_goal_list_item.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ShowGoalsRecyclerAdapter(options: FirestoreRecyclerOptions<Goal>) :
    FirestoreRecyclerAdapter<Goal, ShowGoalsRecyclerAdapter.GoalViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_goal_list_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int, model: Goal) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.model = model
        holder.tvTitle.text = model.title
        holder.tvCreationDate.text = dateFormatter.format(model.createDate)
        val decim = DecimalFormat("#,###.##")

        holder.tvFullAmt.text = String.format("$${decim.format(model.targetAmt)}")

        // save values as variables to reduce loading time when swiping through recycler view items
        val currentAmt = model.currentAmt
        val targetAmt = model.targetAmt

        val progressAmtPercentFloat = ((currentAmt / targetAmt) * 100).toFloat()
        val totalProgress: Float

        if (model.bucketList.count() != 0) {
            // include bucket list for progress calculation
            var blItem_InProgress = 0
            var blItem_Completed = 0

            for (item in 0 until model.bucketList.count()) {
                if (model.bucketList[item].containsValue(true)) blItem_Completed += 1
                else blItem_InProgress += 1
            }

            // bucketListProgress is 50% of totalProgress
            val bucketListProgress = ((blItem_Completed * 50 / model.bucketList.count()).toFloat())
            // amountSavedProgress is 50% of totalProgress
            val amountSavedProgress = progressAmtPercentFloat / 2
            // adding bucketListProgress to amountSavedProgress to obtain new totalProgress
            totalProgress = bucketListProgress + amountSavedProgress
        } else {
            // do not include bucket list for progress calculation
            // amountSavedProgress is 100% of totalProgress
            totalProgress = progressAmtPercentFloat
        }

        // Initial load
        holder.progressBar.progress = totalProgress.toInt()
        Log.d("progressBar initial", "${holder.progressBar.progress} // ${model.title}")

        when {
            "leisure" in model.categories -> {
                holder.tvIcon.setImageResource(R.drawable.travel_white_bg)
            }
            "family" in model.categories -> {
                holder.tvIcon.setImageResource(R.drawable.family_white_bg)
            }
            "career" in model.categories -> {
                holder.tvIcon.setImageResource(R.drawable.job_white_bg)
            }
        }
    }

    class GoalViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var model: Goal? = null
        val tvTitle = itemView.tv_gTitle
        val tvCreationDate = itemView.tv_gCreationDate
        val tvFullAmt = itemView.tv_gFullAmount
        val progressBar = itemView.progress_bar_progressAmt_layout_goal
        val tvIcon = itemView.iv_gIcon

        init {
            val activity: AppCompatActivity = itemView.context as AppCompatActivity

            itemView.cardView_goal_item.setOnClickListener { v: View? ->
                val showGoalDetailsFragment = ShowGoalDetailsFragment()

                showGoalDetailsFragment.arguments = Bundle().apply {
                    putParcelable(Goal::class.java.name, model)
                }

                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(
                    showGoalDetailsFragment,
                    enter = R.anim.fragment_close_enter,
                    exit = R.anim.fragment_open_exit,
                    popEnter = R.anim.fragment_slide_right_enter,
                    popExit = R.anim.fragment_slide_right_exit
                )
            }
        }
    }
}