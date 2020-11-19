package com.tellago.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.models.Comment
import com.tellago.models.User
import kotlinx.android.synthetic.main.layout_comment.view.*

class CommentsRecyclerAdapter(opton: FirestoreRecyclerOptions<Comment>) : FirestoreRecyclerAdapter<Comment, CommentsRecyclerAdapter.CommentViewHolder>(opton) {
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentsLinearLayout: LinearLayout = itemView.comments_linearLayout
        val displayPicImageView: ImageView = itemView.displayPic_iv
        val displayNameTextView: TextView = itemView.displayName_tv
        val commentTextView: TextView = itemView.comment_tv
    }

    override fun onViewAttachedToWindow(holder: CommentViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.commentsLinearLayout.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comment) {
        holder.commentTextView.text = model.comment
        model.uid?.let {
            User(uid = it).getUserWithUid { user ->
                user?.displayProfilePicture(holder.itemView.context, holder.displayPicImageView)
                user?.displayName?.let { displayName ->
                    holder.displayNameTextView.text = displayName
                }
            }
        }
    }
}