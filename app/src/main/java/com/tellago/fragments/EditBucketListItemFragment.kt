package com.tellago.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_bucket_list_item.*
import kotlinx.android.synthetic.main.fragment_create_bucket_list_item.et_bucketListItemName
import kotlinx.android.synthetic.main.fragment_edit_bucket_list_item.*
import java.util.ArrayList


class EditBucketListItemFragment : Fragment() {

    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast
    private lateinit var originalItemName: String
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_bucket_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Assign hint for et_bucketListItemName based on current value within bucketList ArrayList
        //et_bucketListItemName.text =
        // store existing value of et_bucketListItemName so that it can be used for deletion (for ArrayList<String>)
        originalItemName = et_bucketListItemName.text.toString()

        btn_EditBucketListItem.setOnClickListener {

            val showBucketListItemsFragment = ShowBucketListItemsFragment()
            assignBucketListToGoal()
//            showBucketListItemsFragment.arguments = Bundle().apply {
//                putParcelable(goal::class.java.name, goal)
//            }

            if (!goal.gid?.isBlank()!!) {
                goal.setByGid {
                    if (it != null) {
                        FragmentUtils(
                            requireActivity().supportFragmentManager,
                            R.id.fragment_container_goal_activity
                        ).replace(showBucketListItemsFragment)
                        toast.success("Bucket List Item updated")
                    }
                    else toast.error("Please try again, there was an issue when updating your Bucket List Item")
                }
            }


            else toast.error("Please try again, there was an issue retrieving the goal id")


        }
    }

    private fun assignBucketListToGoal() {

        // (Option 1) update 'bucketList' field of Goal document with an ArrayList of 'BucketListItem's

//        val bucketListItems = ArrayList<BucketListItem>()
//        val item = BucketListItem()
//
//        item.blitemname = et_bucketListItemName.text.toString()
//        bucketListItems.add(item)


        // (Option 2) update 'bucketList' field of Goal document with an ArrayList of String (based on bucketListName)
        val bucketListItems = goal.bucketList
        // remove original record based on item name onViewCreated
        bucketListItems.remove(originalItemName)
        // add new record based on updated value in et_bucketListItemName.text
        bucketListItems.add(et_bucketListItemName.text.toString())

        goal.bucketList = bucketListItems

    }

}