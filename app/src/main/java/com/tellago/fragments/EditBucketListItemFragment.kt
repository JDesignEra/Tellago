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
    private var bid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        bid = bundle.getInt("bid")
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_bucket_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (goal.bucketList.isNotEmpty()) et_bucketListItemName.setText(goal.bucketList[bid]?.get("name") as String)

        btn_EditBucketListItem.setOnClickListener {
            et_bucketListItemName.text?.let { t -> goal.bucketList[bid]?.put("name", t.toString()) }

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