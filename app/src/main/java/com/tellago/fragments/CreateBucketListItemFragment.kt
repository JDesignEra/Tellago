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
import kotlinx.android.synthetic.main.fragment_create_bucket_list_item.*

class CreateBucketListItemFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast
    private var goal: Goal = Goal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = CustomToast(requireContext())

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_bucket_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        et_bucketListItemName.text?.clear()

        constraint_layout_create_bucket_list_item.setOnClickListener {
            if (et_bucketListItemName.text.toString().isBlank()) {
                et_bucketListItemName.error = "Field is required"
            }
            else {
                val bucketItem = mutableMapOf(
                    "name" to et_bucketListItemName.text.toString(),
                    "completed" to false
                )

                goal.bucketList.add(bucketItem)

                if (goal.gid.isNullOrBlank()) {
                    ShowBucketListItemsOngoingFragment.adapter?.updateFilteredList()
                    ShowBucketListItemsCompletedFragment.adapter?.updateFilteredList()

                    fragmentUtils.popBackStack()
                    toast.success("Bucket item added successfully")
                    et_bucketListItemName.setText("")
                }
                else {
                    goal.updateBucketListByGid {
                        if (it != null) {
                            ShowBucketListItemsOngoingFragment.adapter?.updateFilteredList()
                            ShowBucketListItemsCompletedFragment.adapter?.updateFilteredList()

                            fragmentUtils.popBackStack()
                            toast.success("Bucket item added successfully")
                            et_bucketListItemName.setText("")
                        }
                        else toast.error("Please try again, failed to add bucket item")
                    }
                }
            }
        }
    }

    private fun configureToolbar() {
        toolbar_create_bucketListItem.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }
}