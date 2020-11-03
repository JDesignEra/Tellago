package com.tellago.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.Query
import com.tellago.R
import com.tellago.fragments.DisplayOtherUserFragment
import com.tellago.models.Auth
import com.tellago.models.Post
import com.tellago.models.User
import com.tellago.utilities.FragmentUtils

class DisplayOtherUserActivity : AppCompatActivity() {
    private lateinit var fragmentUtils: FragmentUtils
    private var intentFrom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils =
            FragmentUtils(supportFragmentManager, R.id.display_other_user_profile_fragment_container)
        intentFrom = intent.getStringExtra("userID")
        Log.d("intent is: ", intentFrom)

        setContentView(R.layout.activity_display_other_user)


        // Display another user's profile based on the intendedUserID received
        val displayOtherUserFragment = DisplayOtherUserFragment()

        displayOtherUserFragment.arguments = Bundle().apply {
                putString("userID", intentFrom.toString())
            }

        fragmentUtils.replace(displayOtherUserFragment, null, enter = R.anim.slide_in_up_slow, exit = R.anim.slide_out_up_slow)


    }




override fun onDestroy() {
    super.onDestroy()

    this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
}



}