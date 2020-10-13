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
import com.tellago.fragments.ShowJourneyPostsFragment
import com.tellago.models.Journey
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.layout_journey_list_item.view.*


class ShowJourneysRecyclerAdapter(options: FirestoreRecyclerOptions<Journey>) :
    FirestoreRecyclerAdapter<Journey, ShowJourneysRecyclerAdapter.JourneyViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JourneyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_journey_list_item, parent, false)
        return JourneyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JourneyViewHolder,
        position: Int,
        model: Journey
    ) {
        holder.model = model
        holder.tvJourneyTitle.text = model.title
//        holder.tvJourneyID.text = model.jid

    }

    class JourneyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var model: Journey? = null
        val tvJourneyTitle = itemView.tv_journeyTitle
//        val tvJourneyID = itemView.tv_journey_ID


        init {
            val activity : AppCompatActivity = itemView.context as AppCompatActivity


            itemView.setOnClickListener {

                val showJourneyPostsFragment = ShowJourneyPostsFragment()

                showJourneyPostsFragment.arguments = Bundle().apply {
                    putParcelable(Journey::class.java.name, model)
                }

                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.fragment_container_goal_activity
                ).replace(
                    showJourneyPostsFragment,
                    enter = R.anim.fragment_open_enter,
                    exit = R.anim.fragment_open_exit,
                    popEnter = R.anim.fragment_slide_right_enter,
                    popExit = R.anim.fragment_slide_right_exit
                )

            }

        }

    }


}
