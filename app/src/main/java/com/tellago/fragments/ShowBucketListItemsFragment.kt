package com.tellago.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.appevents.suggestedevents.ViewOnClickListener
import com.google.android.material.snackbar.Snackbar
import com.tellago.R
import com.tellago.adapters.ShowBucketListItemsRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.utilities.FragmentUtils
import com.tellago.utilities.SwipeToDelete
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_bucket_list_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (goal.gid.isNullOrBlank()) {
            configureToolbarBackToCreateGoal()
        } else {
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


                if (direction == ItemTouchHelper.LEFT) {
                    // if swiped LEFT (from right of screen to left of screen) --> display snackbar
                    val deletedItemName = adapter!!.retrieve(viewHolder.layoutPosition)
                    val itemMapDeleted = mutableMapOf(
                        "name" to deletedItemName,
                        "completed" to false
                    )

                    adapter!!.delete(viewHolder.layoutPosition)
                    Log.d("swipe to delete", "FIRED")


                    Snackbar.make(
                        viewHolder.itemView,
                        "Deleted bucket list item #${viewHolder.layoutPosition + 1}",
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Undo", undoDeleteOnClickListener(viewHolder.layoutPosition, itemMapDeleted)
                    )
                        .show()


                } else if (direction == ItemTouchHelper.RIGHT) {
                    // if swiped RIGHT (from left of screen to right of screen) --> display snackbar
                    val archivedItemName = adapter!!.retrieve(viewHolder.layoutPosition)
                    val itemMapBefore = mutableMapOf(
                        "name" to archivedItemName,
                        "completed" to false
                    )
                    val itemMapArchived = mutableMapOf(
                        "name" to archivedItemName,
                        "completed" to true
                    )

                    adapter!!.delete(viewHolder.layoutPosition)
                    adapter!!.add(viewHolder.layoutPosition, itemMapArchived)
                    Log.d("swipe to archive", "FIRED")


                    Snackbar.make(
                        viewHolder.itemView,
                        "Archived bucket list item #${viewHolder.layoutPosition + 1}",
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Undo", undoArchiveOnClickListener(viewHolder.layoutPosition, itemMapBefore)
                    ).show()

                }
            }


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_outline_48)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorSuccess
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_check_circle_outline_48)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            fun undoArchiveOnClickListener(
                position: Int,
                itemMapBefore: MutableMap<String, Any>?
            ): View.OnClickListener = View.OnClickListener { view ->
                adapter!!.delete(position)
                adapter!!.add(position, itemMapBefore)
                adapter!!.notifyItemInserted(position)
                Log.d("Undo Archive", "FIRED")
            }

            fun undoDeleteOnClickListener(
                position: Int,
                itemMapBefore: MutableMap<String, Any>?
            ): View.OnClickListener = View.OnClickListener { view ->
                adapter!!.add(position, itemMapBefore)
                adapter!!.notifyItemInserted(position)
                Log.d("Undo Delete", "FIRED")
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

            goal.updateBucketListByGid {
                if (it != null) {
                    Log.d("goal id saving", goal.gid.toString())
                }

                Log.d("saved latest bucketList", "FIRED")
            }
            
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