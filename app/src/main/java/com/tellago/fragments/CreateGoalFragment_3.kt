package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.R
import com.tellago.adapters.PostForCreateGoalRecyclerAdapter
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_3.*
import java.util.*
import kotlin.collections.ArrayList

class CreateGoalFragment_3 : Fragment() {
    private lateinit var toast: CustomToast
    private lateinit var fragmentUtils: FragmentUtils

    private var titlesList = mutableListOf<String>()
    private var descriptionsList = mutableListOf<String>()
    private var ownersList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_goal_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Currently populating recycler view with static data
        postToList()
        recycler_view_create_goal_show_posts.layoutManager = LinearLayoutManager(activity?.application?.baseContext)
        recycler_view_create_goal_show_posts.adapter = PostForCreateGoalRecyclerAdapter(titlesList, descriptionsList, ownersList, imagesList)

        btn_BackToFragmentTwo.setOnClickListener {
            fragmentUtils.popBackStack()
        }

        btn_CreateGoal.setOnClickListener {
            val bundle = requireArguments()

            val locale = Locale("en", "SG")
            val timezone = TimeZone.getTimeZone("Asia/Singapore")
            val category = ArrayList<String>()
            val deadline = Calendar.getInstance(timezone, locale)
            val reminderFreq = bundle.getInt("reminder")

            deadline.add(Calendar.MONTH, bundle.getInt("duration"))

            if (bundle.getBoolean("career")) category.add("career")
            if (bundle.getBoolean("family")) category.add("family")
            if (bundle.getBoolean("leisure")) category.add("leisure")

            Goal(
                uid = user?.uid,
                title = bundle.getString("title"),
                category = category,
                targetAmt = 5000,
                deadline = deadline.time,
                reminderMonthsFreq = if (reminderFreq == 0) null else reminderFreq,
                createDate = Calendar.getInstance(timezone, locale).time
            ).add {
                if (it != null) {
                    toast.success("Goal created successfully")
                    requireActivity().finish()
                }
                else {
                    toast.error("Failed to create goal, please try again")
                }
            }
        }
    }

    private fun addToList(title: String, description: String, owner: String, image: Int)
    {
        titlesList.add(title)
        descriptionsList.add(description)
        ownersList.add(owner)
        imagesList.add(image)
    }

    private fun postToList() {
        for(i in 1..17) {
            addToList("Title $i", "Description $i", "Owner $i", R.mipmap.ic_launcher_round)
        }
    }


}