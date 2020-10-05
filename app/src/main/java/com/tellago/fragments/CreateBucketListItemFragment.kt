package com.tellago.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.models.BucketListItem
import com.tellago.models.Goal
import com.tellago.models.UserPost
import com.tellago.utilities.FragmentUtils
import com.tellago.utilities.NumPickerUtils
import kotlinx.android.synthetic.main.fragment_create_bucket_list_item.*
import kotlinx.android.synthetic.main.fragment_create_goal_2.*
import java.util.*


class CreateBucketListItemFragment : Fragment() {

    private lateinit var bundle: Bundle
    private lateinit var fragmentUtils: FragmentUtils
    private var goal: Goal = Goal()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        return inflater.inflate(R.layout.fragment_create_bucket_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_AddBucketListItem.setOnClickListener {

            // check if goal id has value
            // if goal id is null, then user should be redirect to CreateGoalFragment2

            val createGoalFragment2 = CreateGoalFragment_2()
            assignBucketListToGoal()
            createGoalFragment2.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_goal_activity
            ).replace(createGoalFragment2)



            // else if goal id has a value, then user should be redirect to ShowBucketListItemsFragment

//            fragmentUtils.replace(ShowBucketListItemsFragment())
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
        var bucketListItems = ArrayList<String>()
        bucketListItems.add(et_bucketListItemName.text.toString())

        goal.bucketList = bucketListItems

    }

}