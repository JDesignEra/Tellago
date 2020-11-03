package com.tellago.fragments

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.activities.DisplayCommunityActivity
import com.tellago.activities.DisplayOtherUserActivity
import kotlinx.android.synthetic.main.fragment_community_feed.*
import kotlinx.android.synthetic.main.fragment_community_members.*


class CommunityMembersFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The following code is meant to enhance static data when displaying Layout only
        // Shift it to the relevant adapter during future development
        val uri_uri = requireContext().resourceUri(R.drawable.james_example)
        setImage((uri_uri))

        cardView_member_community_feed_4.setOnClickListener {
            // Display selected User Profile in new Activity
            val intent = Intent(requireContext(), DisplayOtherUserActivity::class.java)
            // use intent.putExtra to pass the unique user ID to be displayed
            val intendedUserID = "nS1lzldixjYhi7vNvtpgDa0bCux2"
            intent.putExtra("userID", intendedUserID)
            startActivity(intent)

        }


    }

    fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }

    private fun setImage(uri: Uri) {
        GlideApp.with(this)
            .load(uri).apply {
                transform(CenterInside(), CircleCrop())
            }.into(iv_member_profile_picture_4)
    }

}