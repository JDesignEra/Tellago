package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tellago.R
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_categories_dialog.*


class CategoriesDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var bool_cat_1 = 0
        var bool_cat_2 = 0
        var bool_cat_3 = 0

        category_btn_1_edit.setOnClickListener {
            if (category_btn_1_edit.isChecked) bool_cat_1 = 1
            else bool_cat_1 = 0
        }

        category_btn_2_edit.setOnClickListener {
            if (category_btn_2_edit.isChecked) bool_cat_2 = 1
            else bool_cat_2 = 1
        }

        category_btn_3_edit.setOnClickListener {
            if (category_btn_3_edit.isChecked) bool_cat_3 = 1
            else bool_cat_3 = 0
        }

        val bundle = this.arguments
        if (bundle != null) {
            Log.d("Goal ID is", bundle.getString("goal_id").toString())
            tv_goalID_categories_gone.text = bundle.getString("goal_id").toString()

            btn_categories_edit_confirm.setOnClickListener {
                val editGoalDetailsFragment = EditGoalDetailsFragment()
                // New Bundle


                if (bool_cat_1 == 1) bundle.putBoolean("career", true)
                else bundle.putBoolean("career", false)
                if (bool_cat_2 == 1) bundle.putBoolean("family", true)
                else bundle.putBoolean("family", false)
                if (bool_cat_3 == 1) bundle.putBoolean("leisure", true)
                else bundle.putBoolean("leisure", false)
                bundle.putString("update Categories", "updated")
                bundle.putString("goal_id", tv_goalID_categories_gone.text.toString())

                editGoalDetailsFragment.arguments = bundle

                FragmentUtils(
                    requireActivity().supportFragmentManager,
                    R.id.fragment_container_goal_activity
                )
                    .replace(editGoalDetailsFragment)
            }
        }
    }
}