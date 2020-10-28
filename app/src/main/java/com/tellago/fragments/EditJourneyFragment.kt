package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.adapters.EditJourneyRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.models.Post.Companion.collection
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_journey.*

class EditJourneyFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    private var goal = Goal()
    private var journey = Journey()
    private var adapter: EditJourneyRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name) ?: Goal()
        journey = bundle.getParcelable(journey::class.java.name) ?: Journey()
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        toast = CustomToast(requireContext())

        adapter = EditJourneyRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    collection.whereEqualTo("uid", user?.uid).orderBy("createDate", Query.Direction.ASCENDING),
                    Post::class.java
                ).build()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar_edit_journey.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_edit_journey.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }

        Log.e(this::class.java.name, goal.jid.isNotEmpty().toString())
        Log.e(this::class.java.name, goal.jid.size.toString())
        if (goal.jid.isNotEmpty()) {
            tv_edit_journey.text = "Update Journey"
            et_journeyTitle.setText(journey.title)
            if (journey.pids.isNotEmpty()) adapter?.setPids(journey.pids)
        }

        post_recylcerView.layoutManager = LinearLayoutManager(requireContext())
        post_recylcerView.adapter = adapter

        constraint_layout_edit_journey.setOnClickListener {
            journey.pids = adapter?.getPids() ?: ArrayList()

            if (goal.jid.isNullOrEmpty()) {
                when {
                    et_journeyTitle.text.isNullOrBlank() -> et_journeyTitle.error = "Journey Name is required"
                    adapter!!.getPids().isNotEmpty() -> {
                        Journey(
                            uid = user?.uid,
                            title = et_journeyTitle.text.toString(),
                            pids = adapter!!.getPids()
                        ).add { journey ->
                            Log.e(this::class.java.name, journey?.jid.toString())
                            if (journey?.jid != null && journey.jid!!.isNotBlank()) {
                                goal.jid.add(journey.jid!!)
                                Log.e(this::class.java.name, goal.jid.size.toString())

                                goal.updateJidByGid { goal ->
                                    if (goal != null) {
                                        toast.success("Journey added successfully")

                                        val intent = Intent(requireContext(), this::class.java)
                                        intent.putExtra(journey::class.java.name, journey)

                                        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

                                        fragmentUtils.popBackStack()
                                    } else toast.error("Please try again, fail to add Journey")
                                }
                            } else toast.error("Please try again, Fail to add Journey")
                        }
                    }
                    else -> toast.error("Please select at least one post")
                }
            }
            else {
                if (et_journeyTitle.text!!.isNotBlank()) {
                    adapter?.getPids()?.let { pids ->
                        Journey(
                            journey.jid,
                            title = et_journeyTitle.text.toString(),
                            pids = pids
                        ).updateByJid {
                            if (it != null) {
                                toast.success("Journey updated")

                                val intent = Intent(requireContext(), this::class.java)
                                intent.putExtra(journey::class.java.name, it)

                                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

                                fragmentUtils.popBackStack()
                            } else toast.error("Fail to update Journey, please try again")
                        }
                    }
                }
                else et_journeyTitle.error = "Journey Name is required"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.startListening()
    }
}