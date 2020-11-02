package com.tellago.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.activities.GoalsActivity
import com.tellago.adapters.PostForCreateGoalRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
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
    private var pids: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name) ?: Goal()
        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

        // This query will sort results by 'createDate' in ASCENDING order
        adapter = PostForCreateGoalRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(
                    collection
                        .orderBy("createDate", Query.Direction.DESCENDING)
                        .whereEqualTo("uid", user?.uid),
                    Post::class.java
                ).build()
        )

        pids = bundle.getStringArrayList("pids") ?: ArrayList()
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
        adapter?.setPids(pids)

        if (bundle.getBoolean(ShowJourneyPostsFragment::class.java.name, false)) {
            linear_layout_create_goal_3_bottom_back.isEnabled = false
            et_journeyTitle.setText(bundle.getString("journeyTitle"))
        }

        text_view_note_create_goal_fragment_3.error = "."
        text_view_note_create_goal_fragment_3.setOnClickListener {
            toast.warning(msg = "Select posts to add to your Journey.", gravity = Gravity.TOP or Gravity.END)
        }

        linear_layout_create_goal_3_bottom_back.setOnClickListener {
            val intent = Intent(requireContext(), this::class.java)
            intent.putExtra("pids", adapter?.getPids())

            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

            fragmentUtils.popBackStack()
        }

        btn_clearSelection.setOnClickListener {
            adapter?.clearSelections()
        }

        linear_layout_create_goal_3_bottom_finish.setOnClickListener {
            if (et_journeyTitle.text.toString().isBlank()) {
                et_journeyTitle.error = "Field is required"
                et_journeyTitle.requestFocus()
            }
            else {
                goal.uid = user?.uid
                goal.addWithJid(et_journeyTitle.text.toString(), pids) {
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
