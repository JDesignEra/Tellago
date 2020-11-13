package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    class AvailableJourneyViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var model: Journey? = null
        val tvJourneyTitle = itemView.tv_availableJourneyTitle
        val journeyCardView = itemView.cardview_availableJourney_list_item
    }
    private var selectedJourneyTitles: ArrayList<String> = ArrayList()
    private var selectedJids: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableJourneyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_available_journey_item_for_post_attach,
            parent,
            false
        )

        return AvailableJourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableJourneyViewHolder, position: Int, model: Journey) {
        holder.model = model
        holder.tvJourneyTitle.text = model.title

        holder.journeyCardView.setOnClickListener {
            holder.journeyCardView.toggle()
        }

        holder.journeyCardView.setOnCheckedChangeListener { card, _ ->
            if (card.isChecked) {
                if (!selectedJids.contains(model.jid)) {
                    model.jid?.let {
                        selectedJids.add(it)
                    }
                }

                selectedJourneyTitles.add(model.title)
            }
            else {
                model.jid?.let {
                    selectedJids.remove(it)
                    selectedJourneyTitles.remove(model.title)
                }
            }
        }

        if (selectedJids.contains(model.jid)) holder.journeyCardView.isChecked = true
    }

    fun setSelectedJids (selectedJids : ArrayList<String>) {
        this.selectedJids = selectedJids
        notifyDataSetChanged()
    }

    fun getSelectedJids(): ArrayList<String> {
        return this.selectedJids
    }

    fun getSelectedJourneyTitles(): ArrayList<String> {
        return this.selectedJourneyTitles
    }
}