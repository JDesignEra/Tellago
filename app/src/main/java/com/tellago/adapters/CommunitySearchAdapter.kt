package com.tellago.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.tellago.R
import com.tellago.activities.DisplayCommunityActivity
import com.tellago.models.Communities
import kotlinx.android.synthetic.main.layout_community_search.view.*

class CommunitySearchAdapter(options: FirestoreRecyclerOptions<Communities>) :
    FirestoreRecyclerAdapter<Communities, CommunitySearchAdapter.CommunityViewHolder>(options) {
    class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.community_mcv
        val imageView: ImageView = itemView.community_iv
        val nameTxtView: TextView = itemView.communityName_tv
        val membersNoTxtView: TextView = itemView.communityMembersNo_tv
        val descTxtView: TextView = itemView.communityDesc_tv
    }

    private var searchTxt: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_community_search,
            parent,
            false
        )

        return CommunityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int, model: Communities) {
        holder.nameTxtView.text = model.name
        holder.membersNoTxtView.text = "${model.uids.size} ${if (model.uids.isNotEmpty()) "members" else "member"}"
        holder.descTxtView.text = model.summary

        if (searchTxt == null || model.name!!.contains(searchTxt!!, true)) {
            model.displayImageByCid(holder.itemView.context, holder.imageView)
            holder.cardView.visibility = View.VISIBLE
        }
        else {
            holder.cardView.visibility = View.GONE
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DisplayCommunityActivity::class.java)
            intent.putExtra("communityID", model.cid)

            holder.itemView.context.startActivity(intent)
        }
    }

    fun search(text: String) {
        searchTxt = text
        notifyDataSetChanged()
    }

    fun resetSearch() {
        searchTxt = null
        notifyDataSetChanged()
    }
}