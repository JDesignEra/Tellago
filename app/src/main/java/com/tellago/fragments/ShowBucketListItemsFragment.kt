package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tellago.R
import com.tellago.adapters.ShowBucketListItemsRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import com.tellago.utilities.SwipeToDelete
import kotlinx.android.synthetic.main.fragment_show_bucket_list_items.*


class ShowBucketListItemsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var goal: Goal

    private var bundle: Bundle? = null
    private var adapter: ShowBucketListItemsRecyclerAdapter? = null
    private val createBucketListItemFragment: Fragment = CreateBucketListItemFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

        adapter = ShowBucketListItemsRecyclerAdapter(goal)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_bucket_list_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(goal.gid.isNullOrBlank()) {
            configureToolbarBackToCreateGoal()
        }
        else {
            configureToolbar()
        }


        recycler_view_show_bucketListItems_fragment.layoutManager = LinearLayoutManager(
            requireContext()
        )
        recycler_view_show_bucketListItems_fragment.adapter = adapter

        val item = object : SwipeToDelete(
            activity?.application?.baseContext,
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d("onSwiped", "FIRED")

                if (!goal.gid?.isBlank()!!) {
                    goal.setByGid {
                        if (it != null) {
                            Log.d("goal id saving", goal.gid.toString())
                        }
                    }
                }

                Log.d("savedRecords", "FIRED")

                if (direction == ItemTouchHelper.LEFT) {
                    // if swiped LEFT (from right of screen to left of screen) --> display snackbar
                    val deletedItemName = adapter!!.retrieve(viewHolder.adapterPosition).toString()

                    Snackbar.make(
                        viewHolder.itemView,
                        "Deleted bucket list item #${viewHolder.adapterPosition}",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Undo") {
                            it.setOnClickListener {
                                adapter!!.add(viewHolder.adapterPosition, deletedItemName)
                                adapter!!.notifyItemInserted(viewHolder.adapterPosition)
                                Log.d("Undo Delete", "FIRED")
                            }
                        }.show()

                    adapter!!.delete(viewHolder.adapterPosition + 1)

                }
                else if (direction == ItemTouchHelper.RIGHT) {
                    // if swiped RIGHT (from left of screen to right of screen) --> display snackbar
                    Snackbar.make(
                        viewHolder.itemView,
                        "Archived bucket list item #${viewHolder.adapterPosition + 1}",
                        Snackbar.LENGTH_SHORT
                    // missing functionality to archive. I.e. change status attribute of BucketListItem object

                    ).show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(item)
        // Attach ItemTouchHelper to recycler_view_show_bucketListItems_fragment
        itemTouchHelper.attachToRecyclerView(recycler_view_show_bucketListItems_fragment)

        fab_add_bucketListItem.setOnClickListener {
            createBucketListItemFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            ).replace(createBucketListItemFragment)
        }
    }

    private fun configureToolbar() {
        toolbar_show_bucketListItems.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_show_bucketListItems.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

    private fun configureToolbarBackToCreateGoal() {
        toolbar_show_bucketListItems.setNavigationIcon(R.drawable.ic_cancel_grey_48)
        toolbar_show_bucketListItems.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }

}