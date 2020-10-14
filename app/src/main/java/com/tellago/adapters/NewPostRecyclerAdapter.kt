package com.tellago.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_new_post_list_item.view.*
import java.time.*
import java.time.temporal.ChronoUnit


class NewPostRecyclerAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, NewPostRecyclerAdapter.NewPostViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPostViewHolder {
        return NewPostViewHolder(
            // Layout inflator to inflate FROM layout_new_post_list_item
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_new_post_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewPostViewHolder, position: Int, model: Post) {
        holder.model = model

//        holder.bind(items[position])

        // Use NewPostRecyclerAdapter to handle conditional for displaying Posts based on PostType
        when(model.postType)
        {
            "text post" ->
            {
                holder.post_title.visibility = View.VISIBLE
                holder.post_title.text = model.messageBody
            }

            "poll" ->
            {
                holder.post_title.visibility = View.VISIBLE
                val pollQn = model.pollQuestion
                holder.post_title.text = "Poll Question: $pollQn"
                holder.post_pollOption.visibility = View.VISIBLE
                holder.post_pollOption.text = model.poll.toString()
            }

            "multimedia" ->
            {
                holder.post_image.visibility = View.VISIBLE
            }
        }

        val today = LocalDateTime.now()
        val createdDateTime = Instant.ofEpochMilli(model.createDate.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val durationStr: String

        when {
            createdDateTime.until(today, ChronoUnit.YEARS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.YEARS)} years ago"
            }
            createdDateTime.until(today, ChronoUnit.MONTHS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MONTHS)} months ago"
            }
            createdDateTime.until(today, ChronoUnit.WEEKS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MONTHS)} weeks ago"
            }
            createdDateTime.until(today, ChronoUnit.DAYS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.DAYS)} days ago"
            }
            createdDateTime.until(today, ChronoUnit.HOURS) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.HOURS)} hours ago"
            }
            createdDateTime.until(today, ChronoUnit.MINUTES) > 0 -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.MINUTES)} mins ago"
            }
            else -> {
                durationStr = "${createdDateTime.until(today, ChronoUnit.SECONDS)} secs ago"
            }
        }

        holder.post_duration.text = durationStr

        holder.likes.text = model.likes.size.toString()
        // need to display comments individually instead of as an entire ArrayList<String>
        holder.comments.text = model.comment.toString()

        // use this function to display images using Glide (one for profile pic of poster & one for any multimedia belonging to Post)
        holder.bind(model)


    }



    // Constructor for Post ViewHolder
    class NewPostViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var model: Post? = null

        // Setting properties to the View (reference to layout_new_post_list_item item ID)
        val post_image: ImageView = itemView.new_post_image
        val post_title: TextView = itemView.new_post_title
        val post_pollOption: TextView = itemView.new_post_pollOption
        val post_duration: TextView = itemView.new_post_duration
        val likes: TextView = itemView.new_post_likes
        val comments: TextView = itemView.new_post_comments


        val activity: AppCompatActivity = itemView.context as AppCompatActivity


        fun bind(post: Post) {
            // use Glide to set image to post_image
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)


            val imageURI = model?.multimediaURI?.toUri()
            // allow glide to LOAD (image URI from Firebase Storage) INTO post_image
            if (imageURI == null) {
                Log.d("No multimedia display", "FIRED")
                post_image.maxHeight = 0;
                post_image.maxWidth = 0;
                GlideApp.with(activity.application.baseContext)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(R.drawable.ic_launcher_background)
                    .into(post_image)

            }
            // Display image of post
            post.displayPostMedia(activity.application.baseContext, post_image)


        }

//        init {
//
//
        // insert onclicklisteners below
        //
        //
//        }


    }


}
