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

        goal = ShowBucketListItemsTabsFragment.goal

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
            private var undoFlag = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val holdItem = adapter?.getAt(viewHolder.layoutPosition)?.toMap()

                if (holdItem != null && holdItem["idx"] != null && holdItem["completed"] != null) {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            adapter?.remove(viewHolder.layoutPosition)

                            Snackbar.make(view, "Deleting item #${viewHolder.layoutPosition + 1} - ${holdItem["name"]}", Snackbar.LENGTH_LONG)
                                .setAction("Undo", undoRemove(holdItem, viewHolder.layoutPosition))
                                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    val item = holdItem.toMap()
                                    val itemPos = viewHolder.layoutPosition + 1

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)
                                        undoFlag = false
                                    }

                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)

                                        if (!undoFlag) {
                                            val idx = if (item["idx"] as Int > goal.bucketList.size - 1 && item["idx"] as Int > 0) {
                                                item["idx"] as Int - 1
                                            }
                                            else item["idx"] as Int

                                            goal.bucketList.removeAt(idx)
                                            goal.updateBucketListByGid {
                                                if (it != null) toast.success(
                                                    "Item #${itemPos} - ${item["name"]} deleted",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
                                                else toast.error(
                                                    "Failed to delete Item #${itemPos} - ${item["name"]}",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
                                            }
                                        }
                                    }
                                }).show()
                        }
                        ItemTouchHelper.RIGHT -> {
                            adapter?.remove(viewHolder.layoutPosition)
                            ShowBucketListItemsOngoingFragment.adapter?.insert(holdItem)

                            Snackbar.make(
                                view,
                                "Item #${viewHolder.layoutPosition + 1} - ${holdItem["name"]} moving to 'In Progress'",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(
                                    "Undo",
                                    undoComplete(holdItem, viewHolder.layoutPosition)
                                )
                                .addCallback(object :
                                    BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    val item = holdItem.toMap()
                                    val itemPos = viewHolder.layoutPosition + 1

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)
                                        undoFlag = false
                                    }

                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)

                                        if (!undoFlag) {
                                            val idx = if (item["idx"] as Int > goal.bucketList.size - 1 && item["idx"] as Int > 0) {
                                                item["idx"] as Int - 1
                                            }
                                            else item["idx"] as Int


                                            goal.bucketList[idx]["completed"] = false
                                            goal.updateBucketListByGid {
                                                if (it != null) toast.success(
                                                    "Item #${itemPos} - ${item["name"]} moved to in progress",
                                                    gravity = Gravity.TOP or Gravity.END,
                                                    cornerRadius = 5
                                                )
                                                else toast.error(
                                                    "Item #${itemPos} - ${item["name"]} failed to move to in progress",
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
                    .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.colorWhiteBackground))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorWarning))
                    .addSwipeRightActionIcon(R.drawable.ic_hourglass_top_white_48dp)
                    .addSwipeRightLabel("In Progress")
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
                    .setSwipeRightLabelColor(ContextCompat.getColor(requireContext(), R.color.colorWhiteBackground))
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
        }

        val itemTouchHelper = ItemTouchHelper(recyclerViewSwipeDecorator)

        if (!goal.completed) itemTouchHelper.attachToRecyclerView(recycler_view_show_bucketListItems_completed_fragment)
    }

    companion object {
        var adapter: ShowBucketListItemsRecyclerAdapter? = null
    }
}