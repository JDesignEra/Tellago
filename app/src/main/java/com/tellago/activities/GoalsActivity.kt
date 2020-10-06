package com.tellago.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.fragments.CreateGoalFragment_1
import com.tellago.fragments.HomeFragment
import com.tellago.fragments.ShowGoalsFragment
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.activity_create_goal.*

class GoalsActivity : AppCompatActivity() {
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null

    private lateinit var fragmentUtils: FragmentUtils
    private var intentFrom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)

        setContentView(R.layout.activity_create_goal)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        StartTimer()

        fragmentUtils = FragmentUtils(supportFragmentManager, R.id.fragment_container_goal_activity)
        intentFrom = intent.getStringExtra(HomeFragment::class.java.name)

        if (intentFrom == "add") {
            val createGoalFragment1: Fragment = CreateGoalFragment_1()
            createGoalFragment1.arguments = Bundle().apply {
                putString(HomeFragment::class.java.name, "add")
            }

            fragmentUtils.replace(createGoalFragment1, null, false)
        }
        else {
            val showGoalsFragment: Fragment = ShowGoalsFragment()
            showGoalsFragment.arguments = Bundle().apply {
                putString(HomeFragment::class.java.name, "show")
            }

            fragmentUtils.replace(showGoalsFragment, null, false)
        }

        configureToolbar()
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
        setSupportActionBar(toolbar_createGoal as Toolbar?)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.setTitle("")
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_36)

        (toolbar_createGoal as Toolbar?)?.setNavigationOnClickListener {
            if (intentFrom == "add") {
                finish()
            }
            else {
                fragmentUtils.popBackStack()
            }
        }
    }
}