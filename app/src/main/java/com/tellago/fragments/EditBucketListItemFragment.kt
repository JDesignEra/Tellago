package com.tellago.fragments

import android.os.Bundle
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

            if (bucketItem["completed"] as Boolean) completed_toggleGrp.check(R.id.completed_btn)
            else completed_toggleGrp.check(R.id.in_progress_btn)
        }

        btn_EditBucketListItem.setOnClickListener {
            if (et_bucketListItemName.text.toString().isBlank()) {
                et_bucketListItemName.error = "Field is required"
            }
            else {
                val originalItem = bucketItem.toMutableMap()

                bucketItem["name"] = et_bucketListItemName.text.toString()
                bucketItem["completed"] = when (completed_toggleGrp.checkedButtonId) {
                    R.id.completed_btn -> true
                    else -> false
                }

                if (goal.gid.isNullOrBlank()) {
                    ShowBucketListItemsOngoingFragment.adapter?.updateFilteredList()
                    ShowBucketListItemsCompletedFragment.adapter?.updateFilteredList()

                    fragmentUtils.popBackStack()
                    toast.success("Bucket item updated successfully")
                }
                else {
                    goal.updateBucketListByGid {
                        if (it != null) {
                            ShowBucketListItemsOngoingFragment.adapter?.updateFilteredList()
                            ShowBucketListItemsCompletedFragment.adapter?.updateFilteredList()

                            fragmentUtils.popBackStack()
                            toast.success("Bucket item updated successfully")
                        }
                        else {
                            goal.bucketList[originalBid] = originalItem
                            toast.error("Please try again, failed to update bucket item")
                        }
                    }
                }
            }
        }
    }

    private fun configureToolbar() {
        toolbar_edit_bucket_list_item.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }
}