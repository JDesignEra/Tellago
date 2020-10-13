package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tellago.R
import com.tellago.activities.GoalsActivity
import com.tellago.adapters.PostForCreateGoalRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.models.Journey
import com.tellago.models.Post
import com.tellago.models.Post.Companion.collection
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_3.*

class CreateGoalFragment_3 : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var toast: CustomToast
    private lateinit var fragmentUtils: FragmentUtils

    private var goal = Goal()
    private var adapter: PostForCreateGoalRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

        adapter = PostForCreateGoalRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    collection.whereEqualTo("uid", user?.uid),
                    Post::class.java
                ).build()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        recycler_view_create_goal_show_posts.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_create_goal_show_posts.adapter = adapter

        btn_BackToFragmentTwo.setOnClickListener {
            fragmentUtils.popBackStack()
        }

        btn_CreateGoal.setOnClickListener {
            goal.uid = user?.uid

            val pids: ArrayList<String>? = if (adapter != null && adapter!!.getPids().isNotEmpty()) {
                adapter!!.getPids()
            }
            else null

            if (pids != null) {
                goal.addWithJid(pids) {
                    if (it != null) {
                        addSuccessRedirect()
                    }
                    else toast.error("Please try again, there was an error creating your goal")
                }
            }
            else {
                goal.add {
                    if (it != null) {
                        addSuccessRedirect()
                    }
                    else toast.error("Please try again, there was an error creating your goal")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    private fun addSuccessRedirect() {
        if (requireActivity().intent.getStringExtra(HomeFragment::class.java.name) == "show") {
            val intent = Intent(requireContext(), GoalsActivity::class.java)
            intent.putExtra(HomeFragment::class.java.name, "show")

            startActivity(intent)
        }

        requireActivity().finish()
        toast.success("Goal created")
    }

    private fun configureToolbar() {
        toolbar_createGoalFragment3.setNavigationOnClickListener {
            if (requireActivity().intent.getStringExtra(HomeFragment::class.java.name) == "show") {
                val intent = Intent(requireContext(), GoalsActivity::class.java)
                intent.putExtra(HomeFragment::class.java.name, "show")

                startActivity(intent)
            }

            requireActivity().finish()
        }
    }
}
