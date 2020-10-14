package com.tellago.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.adapters.ShowAvailableJourneysForPostAttachRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_attach_post_to_journeys.*


class AttachPostToJourneysFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal: Goal
    private lateinit var journey: Journey
    private lateinit var post: Post
    private lateinit var adapter: ShowAvailableJourneysForPostAttachRecyclerAdapter


    private var bundle: Bundle? = null

    //  broadcastMsgArrayListString declared to be persistent
    private var broadcastMsgArrayListString: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
        journey = Journey()
        post = Post()

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )


        if (this.arguments != null) bundle = requireArguments()
        val availableJourneysArrayList =
            bundle?.getStringArrayList("availableJourneysArrayList") as ArrayList<String>


        val query = FirebaseFirestore.getInstance().collection("journeys").whereIn(
            FieldPath.documentId(), availableJourneysArrayList
        )

        Log.d("Query is: ", "$query")

        adapter = ShowAvailableJourneysForPostAttachRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Journey>()
                .setQuery(query, Journey::class.java)
                .build()
        )


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attach_post_to_journeys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()


        val bundle_postUID = bundle?.getString("post.uid")
        val bundle_postType = bundle?.getString("post.postType")

        // Store values in text views with VISIBILITY = GONE
        tv_store_uid.text = bundle_postUID.toString()
        tv_store_postType.text = bundle_postType.toString()


        recycler_view_show_availableJourney_posts_fragment.layoutManager =
            LinearLayoutManager(requireContext())

        recycler_view_show_availableJourney_posts_fragment.adapter = adapter


        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "chooseJourney".
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mMessageReceiver, IntentFilter("chooseJourney")
        )




        btn_confirm_journey_selection.setOnClickListener {

            Log.d("result Data 1", broadcastMsgArrayListString.toString())

            val createPostFragment = CreatePostFragment()

            createPostFragment.arguments = bundle?.apply {
                putStringArrayList("arrayListString", broadcastMsgArrayListString)
                putString("post uid", tv_store_uid.text.toString())
                putString("post type", tv_store_postType.text.toString())
                Log.d("Passed String ArrayList", "FIRED")
            }
            fragmentUtils.replace(createPostFragment)

        }


    }


    override fun onStart() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStart()

        adapter.startListening()
    }

    override fun onStop() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStop()

        adapter.stopListening()
    }


    private fun configureToolbar() {
        toolbar_view_availableJourney_posts.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_view_availableJourney_posts.setNavigationOnClickListener {
            fragmentUtils.popBackStack(null)
        }
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "chooseJourney" is broadcasted.
    val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


            val journeyIntent = intent.action

            if (journeyIntent == "chooseJourney") {
                if (intent.getStringExtra("journey add") != null) {
                    val jidToAdd = intent.getStringExtra("journey add") as String
                    // here, there is a JID to add
                    if (!broadcastMsgArrayListString.contains(jidToAdd)) {
                        broadcastMsgArrayListString.add(jidToAdd)
                        //                    Log.d("broadcastMsg.. NOW", broadcastMsgArrayListString.toString())
                    }

                } else if (intent.getStringExtra("journey remove") != null) {
//                    Log.d("broadcastMsg.. b4 rem", broadcastMsgArrayListString.toString())

                    val jidToRemove = intent.getStringExtra("journey remove") as String
                    // here, there is a JID to remove
                    if (broadcastMsgArrayListString.contains(jidToRemove)) {
                        broadcastMsgArrayListString.remove(jidToRemove)
//                        Log.d("broadcastMsg.. NOW", broadcastMsgArrayListString.toString())
                    }
                }

            }


        }
    }


}