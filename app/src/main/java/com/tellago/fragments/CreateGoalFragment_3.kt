package com.tellago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.models.Auth
import com.tellago.models.Auth.Companion.user
import com.tellago.models.Goal
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal_3.*


class CreateGoalFragment_3 : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var toast: CustomToast
    private lateinit var fragmentUtils: FragmentUtils

    private var goal = Goal()

    private var titlesList = mutableListOf<String>()
    private var descriptionsList = mutableListOf<String>()
    private var ownersList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = requireArguments()
        goal = bundle.getParcelable(goal::class.java.name)!!
        toast = CustomToast(requireContext())
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal_3, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postToList()

        btn_BackToFragmentTwo.setOnClickListener {
            fragmentUtils.popBackStack()
        }

        btn_CreateGoal.setOnClickListener {
            goal.uid = user?.uid

            goal.add {
                if (it != null) {
                    fragmentUtils.replace(ShowGoalsFragment())
                    toast.success("Goal created")
                }
                else toast.error("Please try again, there was an error creating your goal")
            }
        }
    }

    private fun addToList(title: String, description: String, owner: String, image: Int) {
        titlesList.add(title)
        descriptionsList.add(description)
        ownersList.add(owner)
        imagesList.add(image)
    }

    private fun postToList() {
        for (i in 1..17) {
            addToList("Title $i", "Description $i", "Owner $i", R.mipmap.ic_launcher_round)
        }
    }
}
