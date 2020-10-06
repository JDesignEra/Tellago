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
    private var bid: Int = 0
    private var bucketItem: MutableMap<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        bid = bundle.getInt("bid")
        bucketItem = goal.bucketList[bid]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_bucket_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        if (goal.bucketList.isNotEmpty()) {
            et_bucketListItemName.setText(bucketItem?.get("name") as String)

            if (bucketItem?.get("completed") as Boolean) {
                completed_true_checkbox.isChecked = true
            }
            else completed_false_checkbox.isChecked = true
        }

        completed_true_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) completed_false_checkbox.isChecked = false
        }

        completed_false_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) completed_true_checkbox.isChecked = false
        }

        btn_EditBucketListItem.setOnClickListener {
            et_bucketListItemName.text?.let { t -> bucketItem?.put("name", t.toString()) }
            bucketItem?.put("completed", completed_true_checkbox.isChecked)

            if (goal.gid.isNullOrBlank()) {
                fragmentUtils.popBackStack()
                toast.success("Bucket item updated successfully")
            }
            else {
                goal.updateBucketListByGid {
                    if (it != null) {
                        fragmentUtils.popBackStack()
                        toast.success("Bucket item updated successfully")
                    }
                    else toast.error("Please try again, failed to update bucket item")
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