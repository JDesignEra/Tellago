package com.tellago.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.tellago.R
import com.tellago.adapters.ShowBucketListItemsRecyclerAdapter
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_show_bucket_list_items_completed.*

class ShowBucketListItemsCompletedFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast
    private lateinit var goal: Goal

    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = Goal()

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        toast = CustomToast(requireContext())

        adapter = ShowBucketListItemsRecyclerAdapter(goal, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_bucket_list_items_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view_show_bucketListItems_completed_fragment.layoutManager = LinearLayoutManager(
            requireContext()
        )
        recycler_view_show_bucketListItems_completed_fragment.adapter = adapter

        recycler_view_show_bucketListItems_completed_fragment.layoutManager = LinearLayoutManager(
            requireContext()
        )
        recycler_view_show_bucketListItems_completed_fragment.adapter = adapter

        val recyclerViewSwipeDecorator = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            private var snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
            private var dragFrom = -1
            private var dragTo = -1
            private var prevState = -1
            private var updatingDrag = false
            private var undoFlag = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val holdItem = adapter?.getAt(viewHolder.layoutPosition)?.toMap()

                if (holdItem != null && holdItem["idx"] != null && holdItem["completed"] != null) {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            adapter?.remove(viewHolder.layoutPosition)

                            snackbar.setText("Deleting item #${viewHolder.layoutPosition + 1} - ${holdItem["name"]}")
                                .setAction("Undo", undoRemove(holdItem, viewHolder.layoutPosition))
                                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)
                                        undoFlag = false
                                    }

                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)

                                        if (!undoFlag) {
                                            goal.bucketList.removeAt(holdItem["idx"] as Int)
                                            goal.updateBucketListByGid {
                                                if (it != null) toast.success("Item deleted successfully")
                                                else {
                                                    undoRemove(holdItem, viewHolder.layoutPosition)
                                                    toast.error("Please try again, failed to delete item")
                                                }
                                            }
                                        }
                                    }
                                }).show()
                        }
                        ItemTouchHelper.RIGHT -> {
                            adapter?.remove(viewHolder.layoutPosition)
                            ShowBucketListItemsOngoingFragment.adapter?.insert(holdItem)

                            snackbar.setText("Item #${viewHolder.layoutPosition + 1} - ${holdItem["name"]} moving to 'In Progress'")
                                .setAction("Undo", undoComplete(holdItem, viewHolder.layoutPosition))
                                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)

                                        undoFlag = false
                                    }

                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)

                                        if (!undoFlag) {
                                            updatingDrag = true

                                            goal.bucketList[holdItem["idx"] as Int]["completed"] = false
                                            goal.updateBucketListByGid {
                                                if (it != null) toast.success("Item moved to 'In Progress' tab successfully")
                                                else {
                                                    undoComplete(holdItem, viewHolder.layoutPosition)
                                                    toast.error("Please try again, failed to moved item to 'In Progress' tab")
                                                }
                                            }
                                        }
                                    }
                                }).show()
                        }
                    }
                }
                else {
                    adapter?.updateFilteredList()
                    toast.error("Please try again, you are swiping too many items at lightning speed")
                }
            }

            override fun isLongPressDragEnabled(): Boolean {
                super.isLongPressDragEnabled()

                if (updatingDrag) return false

                return true
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onMoved(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                fromPos: Int,
                target: RecyclerView.ViewHolder,
                toPos: Int,
                x: Int,
                y: Int
            ) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                dragFrom = fromPos
                dragTo = toPos

                adapter?.move(dragFrom, dragTo)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                when (actionState) {
                    ItemTouchHelper.ACTION_STATE_IDLE -> {
                        if (dragFrom > -1 && dragTo > -1 && dragFrom != dragTo) {
                            val holdItem = adapter?.getAt(dragTo)
                            val targetHold = adapter?.getAt(dragFrom)

                            if (holdItem != null && targetHold != null &&
                                holdItem["idx"] != null && targetHold["idx"] != null &&
                                dragFrom > -1 && dragTo > -1 && dragFrom != dragTo &&
                                prevState == ItemTouchHelper.ACTION_STATE_DRAG
                            ) {
                                snackbar.setText("Item #${dragFrom + 1} - ${holdItem["name"]} moving to #${dragTo + 1}")
                                    .setAction("Undo", undoMove(dragFrom, dragTo))
                                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                        override fun onShown(transientBottomBar: Snackbar?) {
                                            super.onShown(transientBottomBar)
                                            undoFlag = false
                                        }

                                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                            super.onDismissed(transientBottomBar, event)

                                            if (!undoFlag && !updatingDrag) {
                                                updatingDrag = true
                                                var toOriginalIdx = targetHold["idx"] as Int

                                                if (dragTo > dragFrom) {
                                                    if (toOriginalIdx + 1 < goal.bucketList.size) toOriginalIdx += 1
                                                }
                                                else {
                                                    if (toOriginalIdx > 0) toOriginalIdx -= 1
                                                }

                                                goal.bucketList.removeAt(holdItem["idx"] as Int).apply {
                                                    goal.bucketList.add(
                                                        toOriginalIdx,
                                                        this
                                                    )
                                                }

                                                goal.updateBucketListByGid {
                                                    if (it != null) {
                                                        toast.success("Item moved successfully")
                                                    }
                                                    else {
                                                        undoMove(dragFrom, dragTo)
                                                        toast.error("Please try again, failed to move item")
                                                    }

                                                    adapter?.updateFilteredList()
                                                    ShowBucketListItemsOngoingFragment.adapter?.updateFilteredList()
                                                    resetDragStates()
                                                }
                                            }
                                        }
                                    }).show()
                            }
                        }
                        else resetDragStates()
                    }
                }

                prevState = actionState
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val builder = RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                builder.addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_outline_48)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
                    .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.colorBackground))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorWarning))
                    .addSwipeRightActionIcon(R.drawable.ic_hourglass_top_white_24dp)
                    .addSwipeRightLabel("In Progress")
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
                    .setSwipeRightLabelColor(ContextCompat.getColor(requireContext(), R.color.colorBackground))
                    .create()
                    .decorate()
            }

            private fun undoRemove(item: Map<String, Any>, position: Int?): View.OnClickListener = View.OnClickListener {
                undoFlag = true
                adapter?.insert(item, position)
                adapter?.updateFilteredList()
            }

            private fun undoComplete(item: Map<String, Any>, position: Int?): View.OnClickListener = View.OnClickListener {
                undoFlag = true
                adapter?.insert(item, position)
                ShowBucketListItemsOngoingFragment.adapter?.remove()
                adapter?.updateFilteredList()
            }

            private fun undoMove(originalFromPosition: Int, originalToPosition: Int): View.OnClickListener = View.OnClickListener {
                undoFlag = true
                adapter?.move(originalToPosition, originalFromPosition)
                adapter?.updateFilteredList()
            }

            private fun resetDragStates() {
                dragFrom = -1
                dragTo = -1
                updatingDrag = false
                prevState = -1
            }
        }

        val itemTouchHelper = ItemTouchHelper(recyclerViewSwipeDecorator)
        itemTouchHelper.attachToRecyclerView(recycler_view_show_bucketListItems_completed_fragment)
    }

    companion object {
        var adapter: ShowBucketListItemsRecyclerAdapter? = null
    }
}