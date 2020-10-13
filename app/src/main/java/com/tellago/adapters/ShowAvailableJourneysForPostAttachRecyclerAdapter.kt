package com.tellago.adapters

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


class ShowAvailableJourneysForPostAttachRecyclerAdapter (options: FirestoreRecyclerOptions<Journey>) :
    FirestoreRecyclerAdapter<Journey, ShowAvailableJourneysForPostAttachRecyclerAdapter.AvailableJourneyViewHolder>(options) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvailableJourneyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_available_journey_item_for_post_attach, parent, false)
        return AvailableJourneyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AvailableJourneyViewHolder,
        position: Int,
        model: Journey
    ) {
        holder.model = model
        holder.tvJourneyTitle.text = model.title
//        holder.checkboxAvailableItem = model..
    }


    class AvailableJourneyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var model: Journey? = null
        val tvJourneyTitle = itemView.tv_availableJourneyTitle
        val checkboxAvailableItem = itemView.checkbox_available_journey_item


        init {
            val activity: AppCompatActivity = itemView.context as AppCompatActivity


            val selectedJourneyList = ArrayList<String>()

            itemView.cardview_availableJourney_list_item.setOnClickListener {
                Log.d("Card Clicked", "FIRED")

                val availableJourneyList = ArrayList<String>()
                // populating availableJourneyList
                model?.jid?.let { it1 -> availableJourneyList.add(it1) }

                availableJourneyList.filterIndexed {
                    index: Int, s: String ->

                    if (selectedJourneyList.contains(s))
                    {
                        checkboxAvailableItem.isChecked = false
                        selectedJourneyList.remove(s)
                    }
                    else
                    {
                        checkboxAvailableItem.isChecked = true
                        selectedJourneyList.add(s)
                    }


                }
                Log.d("selectedJourneyList", selectedJourneyList.toString())

                // use LocalBroadcast to send selectedJourneyList to ShowJourneyPostsFragment.kt
                val intent = Intent("chooseJourney")

                var iterate = 0
                for (jid in selectedJourneyList)
                {
                    iterate += 1
                    intent.putExtra("journey #$iterate", jid)
                    Log.d("jid = ", jid)
                }
                intent.putExtra("journey count", iterate)
                Log.d("journey count ", "$iterate")

                LocalBroadcastManager.getInstance(activity.application.baseContext).sendBroadcast(intent)
                Log.d("Broadcast Sent", "FIRED")


            }





        }


    }

}