package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Communities
import kotlinx.android.synthetic.main.layout_available_journey_item_for_post_attach.view.*

class ShowAvailableCommunitiesForPostAttachRecyclerAdapter(options: FirestoreRecyclerOptions<Communities>) :
    FirestoreRecyclerAdapter<Communities, ShowAvailableCommunitiesForPostAttachRecyclerAdapter.AvailableCommunityViewHolder>(
        options
    ) {
    class AvailableCommunityViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var model: Communities? = null
        // Reuse layout_available_journey_item_for_post_attach to display each Community
        val tvCommunityName = itemView.tv_availableJourneyTitle
        val communityCardView = itemView.cardview_availableJourney_list_item
    }

    private var selectedCommunityNames: ArrayList<String> = ArrayList()
    private var selectedCids: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvailableCommunityViewHolder {
        return AvailableCommunityViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_available_journey_item_for_post_attach,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: AvailableCommunityViewHolder,
        position: Int,
        model: Communities
    ) {
        holder.model = model
        holder.tvCommunityName.text = model.name

        holder.communityCardView.setOnClickListener {
            holder.communityCardView.toggle()
        }

        holder.communityCardView.setOnCheckedChangeListener { card, isChecked ->
            if (card.isChecked)
            {
                model.cid?.let {
                    selectedCids.add(it)
                    model.name?.let { it1 -> selectedCommunityNames.add(it1) }
                }
            }
            else {
                model.cid?.let {
                    selectedCids.remove(it)
                    model.name?.let { it1 -> selectedCommunityNames.remove(it1) }
                }
            }
        }

        if (selectedCids.contains(model.cid)) holder.communityCardView.isChecked = true

    }

    fun setSelectedCids (selectedCids: ArrayList<String>) {
        this.selectedCids = selectedCids
        notifyDataSetChanged()
    }

    fun getSelectedCids(): ArrayList<String> {
        return this.selectedCids
    }

    fun getSelectedCommunityNames(): ArrayList<String> {
        return this.selectedCommunityNames
    }

}