package com.tellago.fragments

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.models.Communities
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_community_feed.*
import kotlinx.android.synthetic.main.fragment_community_members.*


class CommunityFeedFragment : Fragment() {

    private var communityID_received: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityID_received =  requireActivity().intent.getStringExtra("communityID")
        Log.d("CID in CommFeed is: ", communityID_received)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_feed, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate card views (not yet in adapter) using values obtained from Firestore query
        Communities(cid = communityID_received).getByCid {
            if (it != null) {
                Log.d("Community Feed ID: ", it.cid)
            }
        }


                // The following code is meant to enhance static data when displaying Layout only
        // Shift it to the relevant adapter during future development
        val uri_uri = requireContext().resourceUri(R.drawable.james_example_2)
        setImage((uri_uri))

    }


    fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }

    private fun setImage(uri: Uri){
        GlideApp.with(this)
            .load(uri).apply {
                transform(CenterInside(), CircleCrop())
            }.into(iv_post_profile_picture_1)
    }

}