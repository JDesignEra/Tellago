package com.tellago

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tellago.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1478
    private val signInProviders = Arrays.asList(
        AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val lifeaspirationFragment = LifeAspirationFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser != null) {
            replaceFragment(profileFragment)

            // the following code will replace the current fragment based on the selected navigation
            // item from the bottom navigation bar
            bottom_navigation.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.ic_home -> replaceFragment(homeFragment)
                    R.id.ic_people -> replaceFragment(communityFragment)
                    R.id.ic_flag -> replaceFragment(lifeaspirationFragment)
                    R.id.ic_profile -> replaceFragment(profileFragment)
                }

                true
            }
        }
        else {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}