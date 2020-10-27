package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import kotlinx.android.synthetic.main.fragment_external_resources.*


class ExternalResourcesFragment : Fragment() {

    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.arguments != null) bundle = requireArguments()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_external_resources, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesArrayList = bundle?.getStringArrayList("arrayListString")

        if (categoriesArrayList != null) {
            for (category in categoriesArrayList) {
//                val previousText = tv_external_resources_categories.text
//                tv_external_resources_categories.text =
//                    String.format("$previousText ${System.lineSeparator()} $category")

                if (category == "career") {
                    linear_layout_category_career.visibility = View.VISIBLE
                } else if (category == "family") {
                    linear_layout_category_family.visibility = View.VISIBLE
                } else if (category == "leisure") {
                    linear_layout_category_leisure.visibility = View.VISIBLE
                }
            }
        }

        // On click listeners will open external links
        // For Category of Career
        tv_career_link_1.setOnClickListener {
            
        }

        // For Category of Family


        // For Category of Leisure



    }
}