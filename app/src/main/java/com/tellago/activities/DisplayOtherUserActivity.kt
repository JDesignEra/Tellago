package com.tellago.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tellago.R
import com.tellago.fragments.DisplayOtherUserFragment
import com.tellago.fragments.ProfileFragment
import com.tellago.models.Auth
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.activity_display_other_user.*

class DisplayOtherUserActivity : AppCompatActivity() {
    private lateinit var fragmentUtils: FragmentUtils
    private var intentFrom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils =
            FragmentUtils(
                supportFragmentManager,
                R.id.display_other_user_profile_fragment_container
            )
        intentFrom = intent.getStringExtra("userID")
        Log.d("intent is: ", intentFrom)

        setContentView(R.layout.activity_display_other_user)

        toolbar_display_other_user_navigation_cancel.setOnClickListener {
            finish()
        }


        // if intendedUserID matches current user, then redirect to ProfileFragment
        val viewingUserUid = Auth.user?.uid
        if (intentFrom == viewingUserUid) {
            fragmentUtils.replace(
                ProfileFragment(),
                null,
                enter = R.anim.slide_in_left,
                exit = R.anim.slide_out_left
            )
        }
        else {
            // Display another user's profile based on the intendedUserID received
            val displayOtherUserFragment = DisplayOtherUserFragment()

            displayOtherUserFragment.arguments = Bundle().apply {
                putString("userID", intentFrom.toString())
            }

            fragmentUtils.replace(
                displayOtherUserFragment,
                null,
                enter = R.anim.slide_in_left,
                exit = R.anim.slide_out_left
            )

        }

    }




    override fun onDestroy() {
        super.onDestroy()

        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }


}