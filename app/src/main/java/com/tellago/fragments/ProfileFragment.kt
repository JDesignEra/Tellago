package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.tellago.MainActivity
import com.tellago.R
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogout.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this.requireContext())
            .addOnCompleteListener {
                MainActivity.user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this.requireContext(), MainActivity::class.java)

                startActivity(intent)
            }
    }
}