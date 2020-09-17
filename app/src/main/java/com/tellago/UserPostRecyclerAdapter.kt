package com.tellago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tellago.models.UserPost
import kotlinx.android.synthetic.main.layout_user_post_list_item.view.*

class UserPostRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<UserPost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserPostViewHolder(
            // Layout inflator to inflate FROM layout_user_post_list_item
            LayoutInflater.from(parent.context).inflate(R.layout.layout_user_post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // possible to have multiple viewholders (different displays for different scenarios)
        when(holder){
            is UserPostViewHolder -> {
                // bind items (see private var above) to UserPostViewHolder based on the position (index)
                // the data association is only for information/data that the user will see on their screen
                // since RecyclerView only loads data that is about to be displayed into storage
                holder.bind(items.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    // function to submit list of UserPost data to Recyclerview Adapter
    // aka assign a List<UserPost> to private variable named as items
    fun submitList(userPostList : List<UserPost>) {
        items = userPostList
    }


    // Constructor for UserPost ViewHolder
    class UserPostViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        // Setting properties to the View (reference to layout_user_post_list_item item ID)
        val post_image : ImageView = itemView.user_post_image
        val post_title : TextView = itemView.user_post_title
        val post_author : TextView = itemView.user_post_author

        // bind method takes user post objects & bind to respective views in the layout
        fun bind(userPost: UserPost) {
            post_title.text = userPost.title
            post_author.text = userPost.displayName

            // use Glide to set image to post_image
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            // allow glide to LOAD (image URI from Firebase storage?) INTO post_image
            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(userPost.image)
                .into(post_image)

        }

    }


}
