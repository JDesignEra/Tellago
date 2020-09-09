package com.tellago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tellago.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*

class MainActivity : AppCompatActivity() {
    private val communityFragment = CommunityFragment()
    private val homeFragment = HomeFragment()
    private val lifeaspirationFragment = LifeAspirationFragment()
    private val profileFragment = ProfileFragment()
    private val authFragment = AuthFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(authFragment)


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

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

    }


}