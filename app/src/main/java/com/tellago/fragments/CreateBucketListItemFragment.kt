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
import kotlinx.android.synthetic.main.fragment_show_bucket_list_items.*


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

        btn_AddBucketListItem.setOnClickListener {
            val bucketItem = mutableMapOf(
                "name" to et_bucketListItemName.text.toString(),
                "completed" to false
            )

            goal.bucketList.add(bucketItem)
            goal.updateBucketListByGid {
                toast.success("Bucket item added successfully")

            }
            fragmentUtils.popBackStack()
        }
    }


    private fun configureToolbar() {
        toolbar_create_bucketListItem.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_create_bucketListItem.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

}