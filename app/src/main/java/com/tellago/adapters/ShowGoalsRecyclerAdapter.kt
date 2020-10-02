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
    lateinit var goal_id : String

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
        // val goal ID to pass to next activity/fragment
        holder.tv_GoalID.text = model.gid.toString()
    }

    class GoalViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.tv_gTitle
        val tvCreationDate = itemView.tv_gCreationDate
        val tvFullAmt = itemView.tv_gFullAmount
        val tvIcon = itemView.iv_gIcon
        val tv_GoalID = itemView.tv_g_ID

        val showGoalDetailsFragment = ShowGoalDetailsFragment()

        init {
            val activity : AppCompatActivity = itemView.context as AppCompatActivity
            val fragmentUtils = FragmentUtils(activity.supportFragmentManager, R.id.fragment_container_goal_activity)

            itemView.btn_ShowGoalDetails.setOnClickListener { v: View? ->
//              Use bundle to pass goal_id Data to showGoalDetailsFragment
                val bundle = Bundle()
                bundle.putString("gid", tv_GoalID.text as String?)
                showGoalDetailsFragment.arguments = bundle
                fragmentUtils.replace(showGoalDetailsFragment, false)
            }
        }
    }
}