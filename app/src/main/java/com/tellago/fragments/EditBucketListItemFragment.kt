package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_bucket_list_item.*

class EditBucketListItemFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    private var goal: Goal = Goal()
    private var originalBid: Int = 0
    private var filteredBid: Int = 0
    private var bucketItem: MutableMap<String, Any> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        originalBid = bundle.getInt("originalBid", 0)
        filteredBid = bundle.getInt("filteredBid", 0)
        bucketItem = goal.bucketList[originalBid]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_bucket_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        if (goal.bucketList.isNotEmpty()) {
            et_bucketListItemName.setText(bucketItem["name"] as String)

            if (bucketItem["completed"] as Boolean) {
                completed_true_checkbox.isChecked = true
            }
            else completed_false_checkbox.isChecked = true
        }

        completed_true_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) completed_false_checkbox.isChecked = false
        }

        completed_false_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) completed_true_checkbox.isChecked = false
        }

        btn_EditBucketListItem.setOnClickListener {
            if (et_bucketListItemName.text.toString().isBlank()) {
                et_bucketListItemName.error = "Field is required"
            }
            else {
                var originalBucketList = goal.bucketList.toMutableList()
                originalBucketList = originalBucketList.map {
                    it.toMutableMap()
                }.toMutableList()

                bucketItem["name"] = et_bucketListItemName.text.toString()
                bucketItem["completed"] = completed_true_checkbox.isChecked

                if (goal.gid.isNullOrBlank()) {
                    updateAdapter(originalBucketList, bucketItem)

                    fragmentUtils.popBackStack()
                    toast.success("Bucket item updated successfully")
                }
                else {
                    goal.updateBucketListByGid {
                        if (it != null) {
                            updateAdapter(originalBucketList, bucketItem)

                            fragmentUtils.popBackStack()
                            toast.success("Bucket item updated successfully")
                        }
                        else {
                            toast.error("Please try again, failed to update bucket item")
                        }
                    }
                }

                bucketItem.remove("idx")
            }
        }
    }

    private fun configureToolbar() {
        toolbar_edit_bucket_list_item.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

    private fun updateAdapter(originalBucketList: List<Map<String, Any>>, updatedBucketItem: MutableMap<String, Any>) {
        updatedBucketItem.put("idx", originalBid)

        Log.e("original", originalBucketList[originalBid]["completed"].toString())
        Log.e("updated", updatedBucketItem["completed"].toString())

        if (originalBucketList[originalBid]["completed"] as Boolean != updatedBucketItem["completed"] as Boolean) {
            if (originalBucketList[originalBid]["completed"] as Boolean) {
                Log.e("Condition 1", "Fired")

                ShowBucketListItemsOngoingFragment.adapter?.insert(
                    originalBucketList.toMutableList().filter {
                        !(it["completed"] as Boolean)
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )

                ShowBucketListItemsCompletedFragment.adapter?.remove(
                    originalBucketList.toMutableList().filter {
                        it["completed"] as Boolean
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )
            }
            else {
                Log.e("Condition 2", "Fired")

                ShowBucketListItemsOngoingFragment.adapter?.remove(
                    originalBucketList.toMutableList().filter {
                        !(it["completed"] as Boolean)
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )

                ShowBucketListItemsCompletedFragment.adapter?.insert(
                    originalBucketList.toMutableList().filter {
                        it["completed"] as Boolean
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )
            }
        }
        else {
            if (originalBucketList[originalBid]["completed"] as Boolean) {
                Log.e("Condition 3", "Fired")

                ShowBucketListItemsCompletedFragment.adapter?.change(filteredBid, updatedBucketItem, goal)
            }
            else {
                Log.e("Condition 4", "Fired")

                for (v in updatedBucketItem.values) {
                    Log.e("v", v.toString())
                }

                ShowBucketListItemsOngoingFragment.adapter?.change(filteredBid, updatedBucketItem, goal)
            }
        }
    }

    private fun updateRecyclerAdapter(
        originalBucketList: List<MutableMap<String, Any>>,
        updatedBucketItem: MutableMap<String, Any>,
        orignalBid: Int
    ) {
        Log.e("original", originalBucketList[orignalBid]["completed"].toString())
        Log.e("updated", updatedBucketItem["completed"].toString())
        Log.e("condition",
            (originalBucketList[orignalBid]["completed"] != updatedBucketItem["completed"]).toString()
        )

        if (originalBucketList[orignalBid]["completed"] != updatedBucketItem["completed"]) {
            if (updatedBucketItem["completed"] as Boolean) {
                Log.e("Condition 1", "Fired")

                ShowBucketListItemsCompletedFragment.adapter?.insert(
                    originalBucketList.toMutableList().filter {
                        it["completed"] as Boolean
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )

                ShowBucketListItemsOngoingFragment.adapter?.remove(
                    originalBucketList.toMutableList().filter {
                        !(it["completed"] as Boolean)
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )
            }
            else {
                Log.e("Condition 2", "Fired")

                ShowBucketListItemsOngoingFragment.adapter?.insert(
                    originalBucketList.toMutableList().filter {
                        !(it["completed"] as Boolean)
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )

                ShowBucketListItemsCompletedFragment.adapter?.remove(
                    originalBucketList.toMutableList().filter {
                        it["completed"] as Boolean
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )
            }
        }
        else {
            if (updatedBucketItem["completed"] as Boolean) {
                Log.e("Condition 3", "Fired")
                ShowBucketListItemsCompletedFragment.adapter?.change(
                    originalBucketList.toMutableList().filter {
                        it["completed"] as Boolean
                    }.size - 1,
                    updatedBucketItem,
                    goal
                )
            }
            else {
                Log.e("Condition 4", "Fired")
                ShowBucketListItemsOngoingFragment.adapter?.change(
                    originalBucketList.toMutableList().filter {
                        !(it["completed"] as Boolean)
                    }.size - 2,
                    updatedBucketItem,
                    goal
                )
            }
        }
    }
}