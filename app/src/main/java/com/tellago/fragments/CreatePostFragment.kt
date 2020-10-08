package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_show_journeys.*


class CreatePostFragment : Fragment() {

    private lateinit var fragmentUtils: FragmentUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        chip_media_image.setOnClickListener {
            Log.d("chip for image", "FIRED")
        }

        chip_media_video.setOnClickListener {
            Log.d("chip for video", "FIRED")
        }

        chip_add_poll.setOnClickListener {
            Log.d("chip for adding poll", "FIRED")
        }

        chip_add_journey.setOnClickListener {
            Log.d("chip for journey", "FIRED")
        }


        btn_CreatePost.setOnClickListener {
            Log.d("Creating post", "FIRED")
        }

    }

    private fun configureToolbar() {

        toolbar_create_post.setNavigationOnClickListener {
            fragmentUtils.popBackStack()
        }
    }

}