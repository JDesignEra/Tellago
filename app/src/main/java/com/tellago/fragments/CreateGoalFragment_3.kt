package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellago.R
import com.tellago.adapters.PostForCreateGoalRecyclerAdapter
import com.tellago.interfaces.GoalsCommunicator
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import kotlinx.android.synthetic.main.fragment_create_goal_3.*
import kotlinx.android.synthetic.main.fragment_create_goal_3.view.*
import java.util.*


class CreateGoalFragment_3 : Fragment() {
    private lateinit var communicator: GoalsCommunicator
    private lateinit var toast: CustomToast

    private var titlesList = mutableListOf<String>()
    private var descriptionsList = mutableListOf<String>()
    private var ownersList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communicator = activity as GoalsCommunicator
        toast = CustomToast(requireContext())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Currently populating recycler view with static data
        postToList()
        recycler_view_create_goal_show_posts.layoutManager = LinearLayoutManager(activity?.application?.baseContext)
        recycler_view_create_goal_show_posts.adapter = PostForCreateGoalRecyclerAdapter(titlesList, descriptionsList, ownersList, imagesList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_goal_3, container, false)

        view.btn_BackToFragmentTwo.setOnClickListener {
            communicator.popBackFragment()
        }

        view.btn_CreateGoal.setOnClickListener {
            setFragmentResultListener(communicator.requestKey) { _, bundle ->
                val locale = Locale("en", "SG")
                val timezone = TimeZone.getTimeZone("Asia/Singapore")
                val category = mutableListOf<String>()
                val deadline = Calendar.getInstance(timezone, locale)
                val reminderFreq = bundle.getInt(communicator.reminderKey)
                deadline.add(Calendar.MONTH, bundle.getInt(communicator.durationKey))

                if (bundle.getBoolean(communicator.careerKey)) category.add(communicator.careerKey)
                if (bundle.getBoolean(communicator.familyKey)) category.add(communicator.familyKey)
                if (bundle.getBoolean(communicator.leisureKey)) category.add(communicator.leisureKey)

                Goal(
                    uid = user?.uid,
                    title = bundle.getString(communicator.titleKey),
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

        return view
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