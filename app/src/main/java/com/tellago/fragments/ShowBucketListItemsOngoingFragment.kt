package com.tellago.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
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
import kotlinx.android.synthetic.main.fragment_show_bucket_list_items_ongoing.*

class ShowBucketListItemsOngoingFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast
    private lateinit var goal: Goal

    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goal = ShowBucketListItemsTabsFragment.goal

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        toast = CustomToast(requireActivity().baseContext)
        adapter = ShowBucketListItemsRecyclerAdapter(goal)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_bucket_list_items_ongoing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view_show_bucketListItems_ongoing_fragment.layoutManager = LinearLayoutManager(
            requireContext()
        )
        recycler_view_show_bucketListItems_ongoing_fragment.adapter = adapter

        val recyclerViewSwipeDecorator = object : SimpleCallback(DOWN or UP, RIGHT or LEFT) {
            private var undoFlag = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val holdItem = adapter?.getAt(viewHolder.layoutPosition)?.toMap()

                if (holdItem != null && holdItem["idx"] != null && holdItem["completed"] != null) {
                    when (direction) {
                        LEFT -> {
                            adapter?.remove(viewHolder.layoutPosition)

                            Snackbar.make(view, "Deleting item #${viewHolder.layoutPosition + 1} - ${holdItem["name"]}", Snackbar.LENGTH_LONG)
                                .setAction("Undo", undoRemove(holdItem, viewHolder.layoutPosition))
                                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    val item = holdItem
                                    val itemPos = viewHolder.layoutPosition + 1

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)
                                        undoFlag = false
                                    }

                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)

                                        if (!undoFlag) {
                                            goal.bucketList.removeAt(holdItem["idx"] as Int)
                                            goal.updateBucketListByGid {
                                                if (it != null) toast.success(
                                                    "Item #${itemPos} - ${item?.get("name")} deleted",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
                                                else toast.error(
                                                    "Failed to delete Item #${itemPos} - ${item?.get("name")}",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
                                            }
                                        }
                                    }
                                }).show()
                        }
                        RIGHT -> {
                            adapter?.remove(viewHolder.layoutPosition)
                            ShowBucketListItemsCompletedFragment.adapter?.insert(holdItem)

                            Snackbar.make(view, "Completing Item #${viewHolder.layoutPosition + 1} - ${holdItem["name"]}", Snackbar.LENGTH_LONG)
                                .setAction("Undo", undoComplete(holdItem, viewHolder.layoutPosition))
                                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    val item = holdItem
                                    val itemPos = viewHolder.layoutPosition + 1

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)
                                        undoFlag = false
                                    }

                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)

                                        if (!undoFlag) {
                                            goal.bucketList[holdItem["idx"] as Int]["completed"] = true
                                            goal.updateBucketListByGid {
                                                if (it != null) toast.success(
                                                    "Item #${itemPos} - ${item?.get("name")} moved to complete",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
                                                else toast.error(
                                                    "Failed Item #${itemPos} - ${item?.get("name")} failed to moved to complete",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
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

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val builder = RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                builder.addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_48)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
                    .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.colorBackground))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_check_circle_48)
                    .addSwipeRightLabel("Completed")
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
                ShowBucketListItemsCompletedFragment.adapter?.remove()
                adapter?.updateFilteredList()
            }
        }

        val itemTouchHelper = ItemTouchHelper(recyclerViewSwipeDecorator)
        itemTouchHelper.attachToRecyclerView(recycler_view_show_bucketListItems_ongoing_fragment)
    }

    companion object {
        var adapter: ShowBucketListItemsRecyclerAdapter? = null
    }
}