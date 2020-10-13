package com.tellago.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

            checkboxAvailableItem.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked)
                {
                    true -> Log.d("Button checked", "FIRED")
                    false -> Log.d("Button NOT checked", "FIRED")
                }
            }

            itemView.cardview_availableJourney_list_item.setOnClickListener {
                Log.d("Card Clicked", "FIRED")
            }

        }


    }

}