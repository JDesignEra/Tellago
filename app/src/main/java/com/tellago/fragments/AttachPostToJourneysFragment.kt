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

        Log.d("post.uid", bundle!!.getString("post.uid").toString())


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

        recycler_view_show_availableJourney_posts_fragment.layoutManager =
            LinearLayoutManager(requireContext())

        recycler_view_show_availableJourney_posts_fragment.adapter = adapter


        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "chooseJourney".
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mMessageReceiver, IntentFilter ("chooseJourney")
        )





        btn_confirm_journey_selection.setOnClickListener {

            Log.d("result Data 1", broadcastMsgArrayListString.toString())

            val createPostFragment = CreatePostFragment()

            createPostFragment.arguments = bundle?.apply {
                putStringArrayList("arrayListString", broadcastMsgArrayListString)
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

            // reset broadcastMsgArrayListString
            broadcastMsgArrayListString = ArrayList()

            // Get extra data included in the Intent
            val iterate = intent.getIntExtra("journey count", 0)
            Log.d("iterate value", "$iterate")
            for (i in 0 .. iterate)
            {
                val jidReceived = intent.getStringExtra("journey #$i")
                broadcastMsgArrayListString.add(jidReceived)
            }
            Log.d("broadcastMsgAL", broadcastMsgArrayListString.toString())
//            val selectJourneyList =
//                intent.getStringArrayExtra("selectedJourneyList") as Array<String>
//            Log.d("selectedJourneyList R", selectJourneyList.toString())
        }
    }

//    private val receiverListener = mMessageReceiver()
//
//    inner class mMessageReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//
//            when (intent?.action) {
//                "chooseJourney" -> Log.d("choose", "FIRED")
//            }
//
//            // Get extra data included in the Intent
////            val selectJourneyList = intent.getStringArrayListExtra("selectedJourneyList")
//            val selectJourneyList =
//                intent?.getStringArrayExtra("selectedJourneyList") as Array<String>
//            Log.d("selectedJourneyList R", selectJourneyList.toString())
//            Log.d("count", selectJourneyList.count().toString())
//            // pass over to...
//            //broadcastMsg = selectJourneyList.toMutableList()
//            for (journey in selectJourneyList) {
//                broadcastMsg.add(journey)
//            }
//
//
//        }
//    }

}