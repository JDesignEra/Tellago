package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.tellago.R
import com.tellago.fragments.CommunitySearchFragment
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.layout_communities_category.view.*

class CommunitiesCategoryAdapter(
    private var categories: ArrayList<String>
) : RecyclerView.Adapter<CommunitiesCategoryAdapter.CategoriesViewHolder>() {
    class CategoriesViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.communityCategory_mcv
        val categoryTextView: TextView = itemView.category_tv
        val categoryImageView: ImageView = itemView.category_iv

        init {
            val activity: AppCompatActivity = itemView.context as AppCompatActivity

            cardView.setOnClickListener {
                FragmentUtils(
                    activity.supportFragmentManager,
                    R.id.coord_layout_community
                ).replace(
                    CommunitySearchFragment()
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_communities_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.categoryTextView.text = categories[position]

        when (categories[position]) {
            "career" -> holder.categoryImageView.setImageResource(R.drawable.job_invisi_bg)
            "leisure" -> holder.categoryImageView.setImageResource(R.drawable.travel_invisi_bg)
            "family" -> holder.categoryImageView.setImageResource(R.drawable.family_invisi_bg)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}