package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.adapters.NewPostRecyclerAdapter
import com.tellago.adapters.ShowAvailableJourneysForPostAttachRecyclerAdapter
import com.tellago.adapters.ShowJourneysRecyclerAdapter
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.utilities.FragmentUtils


class AttachPostToJourneysFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal : Goal
    private lateinit var journey: Journey
    private lateinit var post: Post
    private lateinit var adapter: ShowAvailableJourneysForPostAttachRecyclerAdapter


    private var bundle: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()
        journey = Journey()
        post = Post()

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

        val userID = user?.uid

        // use userID to obtain relevant Goals, and then the available Journeys from Goals
//        val query = FirebaseFirestore.getInstance().collection("goals").whereEqualTo("uid", userID)
//
//        Log.d("document", query.toString())

        val availableJourneysArrayList = ArrayList<String>()
        var jidsFromGoal = ArrayList<String>()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("goals").whereEqualTo("uid", userID)
            .get()
            .addOnSuccessListener {
                it ->
                for (document in it.documents)
                {
                    jidsFromGoal = document["jid"] as ArrayList<String>
                    Log.d("jidsFromGoal is", jidsFromGoal.toString())
                    for (jid in jidsFromGoal)
                    {
                        availableJourneysArrayList.add(jid)
                    }
                    Log.d("availableJourneys", availableJourneysArrayList.toString())

                }
            }


        Log.d("Ready to pass on jids", "FIRED")


        // to update query based on unique identifier for each journey (with reference to arrayListJourneyID)
//        val arrayListJourneyID = goal.jid
//        val query = FirebaseFirestore.getInstance().collection("journeys").whereIn(FieldPath.documentId(), arrayListJourneyID)
//
//
//        adapter = ShowJourneysRecyclerAdapter(
//            FirestoreRecyclerOptions.Builder<Journey>()
//                .setQuery(query, Journey::class.java)
//                .build()
//        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attach_post_to_journeys, container, false)
    }


    override fun onStart() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStart()

        adapter?.startListening()
    }

    override fun onStop() {
        // Adapter which is populated using Firestore data (through query) will require this function
        super.onStop()

        adapter?.stopListening()
    }


}