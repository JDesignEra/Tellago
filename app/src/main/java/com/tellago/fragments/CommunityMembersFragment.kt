package com.tellago.fragments

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tellago.GlideApp
import com.tellago.R
import com.tellago.activities.DisplayOtherUserActivity
import com.tellago.adapters.ShowCommunityMembersRecyclerAdapter
import com.tellago.adapters.UserPostRecyclerAdapter
import com.tellago.models.Auth
import com.tellago.models.Communities
import com.tellago.models.User
import com.tellago.utilities.CustomToast
import kotlinx.android.synthetic.main.fragment_attach_post_to_journeys.*
import kotlinx.android.synthetic.main.fragment_community_members.*


class CommunityMembersFragment : Fragment() {

    private var communityID_received: String? = null
    private var currenUserID: String = Auth.user!!.uid
    private lateinit var toast: CustomToast
    private lateinit var showCommunityMembersAdapter: ShowCommunityMembersRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityID_received =  requireActivity().intent.getStringExtra("communityID")


        toast = CustomToast(requireContext())

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


        // Populate card views (not yet in adapter) using values obtained from Firestore query
        Communities(cid = communityID_received).getByCid {
            if (it != null) {

                val allMemberUIDS = it.uids

                val listOfAdminUID = ArrayList<String>()
                val listOfUserUID = ArrayList<String>()

                for (uid in allMemberUIDS)
                {
                    if (uid.value == "admin")
                    {
                        listOfAdminUID.add(uid.key)
                    }

                    else if (uid.value == "user")
                    {
                        listOfUserUID.add(uid.key)
                    }
                }

                Log.d("listOfAdminUID: ", listOfAdminUID.toString())
                Log.d("listOfUserUID: ", listOfUserUID.toString())



                // convert each uid in arrayList to a full User Object
                val listOfUsers_Admin = ArrayList<User>()
                for (uid in listOfAdminUID)
                {
                    Log.d("The UID is: ", uid)
                    User(uid = uid).getUserWithUid {
                        if (it != null) {
                            val newUser : User = it
                            Log.d("the newAdmin is: ", newUser.toString())

                            listOfUsers_Admin.add(newUser)
                            Log.d("this user name is: ", it.displayName.toString())
                        }
                    }
                }


                // convert each uid in arrayList to a full User Object
                val listOfUsers_User = ArrayList<User>()
                for (uid in listOfUserUID)
                {
                    User(uid = uid).getUserWithUid {
                        if (it != null) {

                            val newUser : User = it
                            Log.d("the newUser is: ", newUser.toString())

                            listOfUsers_User.add(newUser)
                            Log.d("this user name is: ", it.displayName.toString())

                        }
                    }
                }

                // Delay before handling the following code:
                Handler().postDelayed({

                    Log.d("List Of Admin: ", listOfUsers_Admin.toString())

                    // Pass list of Community Admins (User Objects) to adapter...
                    showCommunityMembersAdapter =
                        ShowCommunityMembersRecyclerAdapter(currenUserID, listOfUsers_Admin)

                    recycler_view_community_admin.layoutManager =
                        LinearLayoutManager(requireContext())
                    recycler_view_community_admin.adapter = showCommunityMembersAdapter


                    // Pass list of Community Members (User Objects) to adapter...
                    showCommunityMembersAdapter =
                        ShowCommunityMembersRecyclerAdapter(currenUserID, listOfUsers_User)


                    recycler_view_community_followers.layoutManager =
                        LinearLayoutManager(requireContext())
                    recycler_view_community_followers.adapter = showCommunityMembersAdapter

                }, 500)


            }
        }



    }

    override fun onStart() {
        super.onStart()
//        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
//        adapter.stopListening()
    }

    fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }

    private fun setImage(uri: Uri, imageViewID : ImageView) {
        GlideApp.with(this)
            .load(uri).apply {
                transform(CenterInside(), CircleCrop())
            }.into(imageViewID)
    }

}