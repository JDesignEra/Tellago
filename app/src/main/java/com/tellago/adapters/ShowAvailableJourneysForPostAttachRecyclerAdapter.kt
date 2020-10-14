package com.tellago.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Journey
import kotlinx.android.synthetic.main.layout_available_journey_item_for_post_attach.view.*


class ShowAvailableJourneysForPostAttachRecyclerAdapter(options: FirestoreRecyclerOptions<Journey>) :
    FirestoreRecyclerAdapter<Journey, ShowAvailableJourneysForPostAttachRecyclerAdapter.AvailableJourneyViewHolder>(
        options
    ) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvailableJourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_available_journey_item_for_post_attach, parent, false)
        return AvailableJourneyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AvailableJourneyViewHolder,
        position: Int,
        model: Journey
    ) {
        holder.model = model
        holder.tvJourneyTitle.text = model.title


        val selectedJourneyList = ArrayList<String>()

        holder.journeyCardView.setOnClickListener {
            holder.journeyCardView.toggle()
        }

        holder.journeyCardView.setOnCheckedChangeListener { card, isChecked ->

            if (card.isChecked) {
                Log.d("Adding jid", "FIRED")
                // use LocalBroadcast to send JID (Add) to ShowJourneyPostsFragment.kt
                model.jid?.let { jid -> broadcastAddJID(card.context, jid) }

            } else {
                Log.d("Removing jid", "FIRED")
                // use LocalBroadcast to send JID (Remove) to ShowJourneyPostsFragment.kt
                model.jid?.let { jid -> broadcastRemoveJID(card.context, jid) }

            }



        }

    }

    private fun broadcastRemoveJID(context : Context, jid : String) {
        val intent = Intent("chooseJourney")
        intent.putExtra("journey remove", jid)
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(intent)
        Log.d("Broadcast Sent", "FIRED")
    }

    private fun broadcastAddJID(context : Context, jid : String) {
        val intent = Intent("chooseJourney")
        intent.putExtra("journey add", jid)
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(intent)
        Log.d("Broadcast Sent", "FIRED")
    }


    class AvailableJourneyViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var model: Journey? = null
        val tvJourneyTitle = itemView.tv_availableJourneyTitle
        val journeyCardView = itemView.cardview_availableJourney_list_item


        init {
            val activity: AppCompatActivity = itemView.context as AppCompatActivity



        }


    }

}