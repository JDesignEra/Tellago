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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Post
import kotlinx.android.synthetic.main.layout_new_post_list_item.view.*
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId


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

        holder.post_title.text = model.postType
        holder.post_author.text = model.uid?.let {
            com.tellago.models.User(uid = it).getUserWithUid {
                if (it != null) {
                    it.displayName
                }
            }.toString()
        }
        holder.post_duration.text = Period.between(
            model.createDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            LocalDate.now()
        ).toString() + " ago"
        holder.likes.text = model.likes.size.toString()
        // need to display comments individually instead of as an entire ArrayList<String>
        holder.comments.text = model.comment.toString()

        // use Glide to set image to post_image
//        val requestOptions = RequestOptions()
//            .placeholder(R.drawable.ic_launcher_background)
//            .error(R.drawable.ic_launcher_background)
//
//
//        val imageURI = model.multimediaURI?.toUri()
//        // allow glide to LOAD (image URI from Firebase Storage) INTO post_image
//        if (imageURI == null) {
//            Log.d("No multimedia display", "FIRED")
//            holder.post_image.maxHeight = 0;
//            holder.post_image.maxWidth = 0;
//            Glide.with()
//                .applyDefaultRequestOptions(requestOptions)
//                .load(R.drawable.ic_launcher_background)
//                .into(post_image)
//
//        }
//        // Display image of post
//        Glide.with(itemView.context)
//            .applyDefaultRequestOptions(requestOptions)
//            .load(imageURI)
//            .into(post_image)
//
//
//        // Retrieve user's profile picture
//        // Display user's profile picture
//        newPost.uid?.let { com.tellago.models.User(uid = it).displayProfilePicture(itemView.context, post_profile_pic) }
//

    }


    // Use NewPostRecyclerAdapter to handle conditional for displaying Posts based on PostType
//        if (journeyPostsList != null) {
//            for (journeyPost in journeyPostsList) {
//                post.pid = journeyPost.toString()
//                post.getByPid {
//                    if (it != null) {
//
//                        val newPostObj = Post(
//                            uid = post.uid,
//                            postType = post.postType,
//                            journeyArray = post.journeyArray
//                        )
//
//                        when (it.postType) {
//                            "text post" -> {
//                                Log.d("postType is text post", "FIRED")
//                                //newPostObj.postType = it.postType!!
//
//                            }
//
//                            "poll" -> {
//                                Log.d("postType is poll", "FIRED")
//                                //newPostObj.postType = it.postType!!
//
//                            }
//
//                            "multimedia" -> {
//                                Log.d("postType is multimedia", "FIRED")
//                                //newPostObj.postType = it.postType!!
//
//                            }
//                        }
//
//                        Log.d("newPostObj", newPostObj.toString())
//                        newPostArrayList.add(newPostObj)
//                        Log.d("newPostArrayList", newPostArrayList.size.toString())
//
//
//                    }


    // Constructor for Post ViewHolder
    class NewPostViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var model: Post? = null

        // Setting properties to the View (reference to layout_new_post_list_item item ID)
        val post_image: ImageView = itemView.new_post_image
        val post_title: TextView = itemView.new_post_title
        val post_author: TextView = itemView.new_post_author
        val post_profile_pic: ImageView = itemView.new_post_profile_pic
        val post_duration: TextView = itemView.new_post_duration
        val likes: TextView = itemView.new_post_likes
        val comments: TextView = itemView.new_post_comments

        init {
            val activity: AppCompatActivity = itemView.context as AppCompatActivity

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
                Glide.with(activity.application.baseContext)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(R.drawable.ic_launcher_background)
                    .into(post_image)

            }
            // Display image of post
            Glide.with(activity.application.baseContext)
                .applyDefaultRequestOptions(requestOptions)
                .load(imageURI)
                .into(post_image)


            // Retrieve user's profile picture
            // Display user's profile picture
            model?.uid?.let {
                com.tellago.models.User(uid = it)
                    .displayProfilePicture(itemView.context, post_profile_pic)
            }
//            Glide.with(itemView.context)
//                .applyDefaultRequestOptions(requestOptions)
//                .load(newPost.profilePic)
//                .circleCrop()
//                .into(post_profile_pic)

            // insert onclicklisteners below


        }


        // bind method takes new post objects & bind to respective views in the layout
//        fun bind(newPost: Post) {
//            // replace post_title layout element??
//            post_title.text = newPost.postType
//            post_author.text = newPost.uid?.let {
//                com.tellago.models.User(uid = it).getUserWithUid {
//                    if (it != null) {
//                        it.displayName
//                    }
//                }.toString()
//            }
//            post_duration.text = Period.between(
//                newPost.createDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
//                LocalDate.now()
//            ).toString() + " ago"
//            likes.text = newPost.likes.size.toString()
//            // need to display comments individually instead of as an entire ArrayList<String>
//            comments.text = newPost.comment.toString()


//            // use Glide to set image to post_image
//            val requestOptions = RequestOptions()
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_background)
//
//
//            val imageURI = newPost.multimediaURI?.toUri()
//            // allow glide to LOAD (image URI from Firebase Storage) INTO post_image
//            if (imageURI == null) {
//                Log.d("No multimedia display", "FIRED")
//                post_image.maxHeight = 0;
//                post_image.maxWidth = 0;
//                Glide.with(itemView.context)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(R.drawable.ic_launcher_background)
//                    .into(post_image)
//
//            }
//            // Display image of post
//            Glide.with(itemView.context)
//                .applyDefaultRequestOptions(requestOptions)
//                .load(imageURI)
//                .into(post_image)
//
//
//            // Retrieve user's profile picture
//            // Display user's profile picture
//            newPost.uid?.let { com.tellago.models.User(uid = it).displayProfilePicture(itemView.context, post_profile_pic) }
////            Glide.with(itemView.context)
////                .applyDefaultRequestOptions(requestOptions)
////                .load(newPost.profilePic)
////                .circleCrop()
////                .into(post_profile_pic)

    }


}
