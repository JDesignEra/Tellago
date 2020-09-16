package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.EditProfileActivity
import com.tellago.R
import com.tellago.models.Auth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_displayName.text = Auth.user?.displayName ?: "Guest"
        profile_bio.text = Auth.profile?.bio ?: "Introduce yourself to the others."

        button_edit_profile.setOnClickListener {
            var nextActivity: Intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(nextActivity)
            activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }
    }
}