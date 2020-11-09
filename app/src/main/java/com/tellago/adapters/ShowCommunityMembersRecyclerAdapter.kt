package com.tellago.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tellago.R
import com.tellago.activities.DisplayOtherUserActivity
import com.tellago.models.Auth
import com.tellago.models.User
import kotlinx.android.synthetic.main.layout_community_member_item.view.*


class ShowCommunityMembersRecyclerAdapter(currentUserUid: String, private var users: List<User>) :
    RecyclerView.Adapter<ShowCommunityMembersRecyclerAdapter.UserViewHolder>() {

    val currentUid = currentUserUid

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_community_member_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        holder.bind(currentUid, users[position])

//        holder.itemView.setOnClickListener {
//            Log.d("holder itemview click", "FIRED")
//        }
    }

    override fun getItemCount(): Int {
        return users.size
    }


    // Constructor for User ViewHolder
    class UserViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        // Setting properties to the View (reference to layout_community_member_item)
        val cardView : CardView = itemView.cardView_community_member_item
        val user_profile_image : ImageView = itemView.iv_community_member_icon
        val user_uid : TextView = itemView.tv_community_member_uid
        val user_display_name : TextView = itemView.tv_community_member_name
        val user_total_followers : TextView = itemView.tv_community_member_follower_count
        val follow_layout : LinearLayout = itemView.linear_layout_follow_member
        val unfollow_layout : LinearLayout = itemView.linear_layout_unfollow_member


        // bind method takes user objects & bind to respective views in layout_community_member_item
        fun bind(currentUid : String, user : User) {
            user.displayProfilePicture(context = itemView.context, imageView = user_profile_image)
            user_uid.text = user.uid
            user_display_name.text = user.displayName
            user_total_followers.text = "${user.followerUids.size} followers"
            // adjust follow_layout background depending on whether the viewing user is a follower
            // if follower is viewing, then display unfollow_layout
            if (Auth.user!!.uid in user.followerUids)
            {
                unfollow_layout.visibility = View.VISIBLE
                follow_layout.visibility = View.GONE
            }

            // if non-follower is viewing, then display follow_layout
            else {
                follow_layout.visibility = View.VISIBLE
                unfollow_layout.visibility = View.GONE
            }

            // launch 'follow' or 'unfollow' action when user clicks on relevant layout
            follow_layout.setOnClickListener {
                user.userFollowUser(currentUid, user_uid.text.toString())
                unfollow_layout.visibility = View.VISIBLE
                follow_layout.visibility = View.GONE
            }

            unfollow_layout.setOnClickListener {
                user.userFollowUser(currentUid, user_uid.text.toString())
                follow_layout.visibility = View.VISIBLE
                unfollow_layout.visibility = View.GONE
            }


        }


        init {
            val activity : AppCompatActivity = itemView.context as AppCompatActivity


            // when a card view is clicked, open new activity to display that profile
            cardView.setOnClickListener {

                Log.d("cardView clicked..", "FIRED")

                val intent = Intent(it.context, DisplayOtherUserActivity::class.java)
                // use intent.putExtra to pass the unique user ID to be displayed
                val intendedUserID = user_uid.text
                intent.putExtra("userID", intendedUserID)

                activity.startActivity(intent)

            }

        }


    }

}