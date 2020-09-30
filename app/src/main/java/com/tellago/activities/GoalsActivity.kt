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
import com.tellago.fragments.ShowGoalsFragment
import com.tellago.interfaces.GoalsCommunicator
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_create_goal.*

class GoalsActivity : AppCompatActivity(), GoalsCommunicator {
    override var requestKey: String = ""
        get() {return field}
        set(value) { field = value }
    override val titleKey: String = "title"
    override val careerKey: String = "career"
    override val familyKey: String = "family"
    override val leisureKey: String = "leisure"
    override val durationKey: String = "duration"
    override val reminderKey: String = "reminder"

    private var handler: Handler? = null
    private var handlerTask: Runnable? = null
    private val createGoalFragment1: Fragment = CreateGoalFragment_1()
    private val showGoalsFragment: Fragment = ShowGoalsFragment()

    private lateinit var fragmentUtils: FragmentUtils
    private var intentExtra: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)

        setContentView(R.layout.activity_create_goal)

        // In Activity's onCreate() for instance
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        StartTimer()

        fragmentUtils = FragmentUtils(supportFragmentManager, R.id.fragment_container_goal_activity)
        intentExtra = intent.getStringExtra("INTENT_EXTRA")

        if (intentExtra == "add_goal") {
            fragmentUtils.add(createGoalFragment1)
        }
        else {
            fragmentUtils.add(showGoalsFragment)
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
        // To 'hide' Title display in actionbar
        actionbar?.setTitle("")
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_36)

        (toolbar_createGoal as Toolbar?)?.setNavigationOnClickListener {
            if (intentExtra == "add_goal") {
                finish()
            }
            else {
                fragmentUtils.popBackStack()
            }
        }
    }

    override fun replaceFragment(fragment: Fragment) {
        fragmentUtils.replace(fragment)
    }

    override fun popBackFragment() {
        fragmentUtils.popBackStack()
    }
}