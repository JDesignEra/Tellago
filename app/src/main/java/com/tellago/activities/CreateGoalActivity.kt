package com.tellago.activities

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.tellago.R
import com.tellago.fragments.*
import com.tellago.interfaces.CreateGoalCommunicator
import com.tellago.utils.CustomToast
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_create_goal_1.*

class CreateGoalActivity : AppCompatActivity(), CreateGoalCommunicator {

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private val fragmentUtils = FragmentUtils(supportFragmentManager, R.id.fragment_container_create_goal)
    private val createGoalFragment1: Fragment = CreateGoalFragment_1()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)

        setContentView(R.layout.activity_create_goal)

        val window: Window = getWindow()

        // In Activity's onCreate() for instance
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        StartTimer()

        fragmentUtils.add(createGoalFragment1)

        configureToolbar()


    }


    override fun passStringDataComOne(editTextStringInput: String) {
        val bundle = Bundle()
        bundle.putString("string 1", editTextStringInput)
        Log.d("bundlePutString", editTextStringInput)

        val fragmentTwo = CreateGoalFragment_2()
        fragmentTwo.arguments = bundle
        fragmentUtils.addFragmentToFragment(fragmentTwo)
    }



    override fun passLongDataComOne(LongInput: Long) {
        val bundle = Bundle()
        bundle.putLong("Long 1", LongInput)

        val fragmentTwo = CreateGoalFragment_2()
        fragmentTwo.arguments = bundle
        fragmentUtils.addFragmentToFragment(fragmentTwo)
    }


    override fun passTimestampDataComOne(TimeStampInput: Timestamp) {
        val bundle = Bundle()
        bundle.putString("Timestamp to String 1", TimeStampInput.toString())

        val fragmentTwo = CreateGoalFragment_2()
        fragmentTwo.arguments = bundle
        fragmentUtils.addFragmentToFragment(fragmentTwo)
    }



    override fun firstFormSubmit(
        editTextStringInput: String,
        stateCareer: Int,
        stateFamily: Int,
        stateLeisure: Int
    ) {
        val bundle = Bundle()
        bundle.putString("string 1", editTextStringInput)
        bundle.putInt("careerSelected", stateCareer)
        bundle.putInt("familySelected", stateFamily)
        bundle.putInt("leisureSelected", stateLeisure)

        val fragmentTwo = CreateGoalFragment_2()
        fragmentTwo.arguments = bundle
        fragmentUtils.addFragmentToFragment(fragmentTwo)

    }

    override fun secondFormSubmit(
        editTextStringInput: String,
        stateCareer: Int,
        stateFamily: Int,
        stateLeisure: Int,
        durationIntInput : Int,
        durationStringInput : String,
        reminderInput : Int

    ) {
        val bundle = Bundle()
        bundle.putString("string 2", editTextStringInput)
        bundle.putInt("careerSelected", stateCareer)
        bundle.putInt("familySelected", stateFamily)
        bundle.putInt("leisureSelected", stateLeisure)
        bundle.putInt("duration Int", durationIntInput)
        bundle.putString("duration String", durationStringInput)
        bundle.putInt("reminder Int", reminderInput)

        val fragmentThree = CreateGoalFragment_3()
        fragmentThree.arguments = bundle
        fragmentUtils.addFragmentToFragment(fragmentThree)

    }



    private fun StartTimer() {
        handler = Handler()
        handlerTask = Runnable { // do something
            hideSystemUI()
            handler!!.postDelayed(handlerTask, 3000)
        }

        handlerTask!!.run()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun configureToolbar() {
        // Indicate that toolbar_editProfile will replace Actionbar
        setSupportActionBar(toolbar_createGoal as Toolbar?)

        val actionbar: ActionBar? = supportActionBar
        // To 'hide' Title display in actionbar
        actionbar?.setTitle("")
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_cancel_black_48)

        // Navigate back to MainActivity (by closing the current Edit Profile Activity)
        (toolbar_createGoal as Toolbar?)?.setNavigationOnClickListener {
            this.finish()
        }
    }


}