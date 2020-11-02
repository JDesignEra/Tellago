package com.tellago.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tellago.R
import com.tellago.fragments.CommunityTabsFragment
import com.tellago.services.ExitService
import com.tellago.utilities.FragmentUtils

class DisplayCommunityActivity : AppCompatActivity() {

    private val fragmentUtils = FragmentUtils(supportFragmentManager, R.id.display_community_fragment_container)
    private val communityTabsFragment = CommunityTabsFragment()

    override fun onStart() {
        super.onStart()
        startService(Intent(this, ExitService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_community)

        // Display communityTabsFragment by default
        fragmentUtils.replace(communityTabsFragment, null, enter = R.anim.slide_in_up_slow, exit = R.anim.slide_out_up_slow)

    }


    override fun onDestroy() {
        super.onDestroy()

        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }


}