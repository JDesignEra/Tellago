package com.tellago.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tellago.R
import com.tellago.activities.CallToActionActivity
import com.tellago.models.Goal
import com.tellago.utilities.CustomToast
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_edit_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.constraint_layout_edit_goal_details
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ShowGoalDetailsFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var toast: CustomToast

    private var bundle: Bundle? = null
    private var goal = Goal()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container_goal_activity
        )
        toast = CustomToast(requireContext())

        if (this.arguments != null) bundle = requireArguments()
        if (bundle != null) goal = bundle!!.getParcelable(goal::class.java.name)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        // We could actually use this for call to action
        // do a little compute based on the Category (and sub categories) of the Goal
        // then show relevant products, links, and contact details using the Carousel view
        val heroImagesId = arrayOf(
            R.drawable.family_invisi_bg,
            R.drawable.job_invisi_bg,
            R.drawable.travel_invisi_bg
        )

        linearLayout_heroHeader_indicators.removeAllViews()

        for (i in heroImagesId.indices) {
            val indicatorsImageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (30 * resources.displayMetrics.density).roundToInt(),
                    (5 * resources.displayMetrics.density).roundToInt()
                ).apply {
                    marginStart = (3 * resources.displayMetrics.density).roundToInt()
                    marginEnd = (3 * resources.displayMetrics.density).roundToInt()
                }

                scaleType = ImageView.ScaleType.FIT_XY

                if (i == 0) setImageResource(R.drawable.progress_rectangle_selected)
                else setImageResource(R.drawable.progress_rectangle_unselected)
            }

            linearLayout_heroHeader_indicators.addView(indicatorsImageView)
        }

        heroHeader_carouselView.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    for ((i, v) in linearLayout_heroHeader_indicators.children.withIndex()) {
                        val indicatorImageView = v as ImageView

                        if (i == position) indicatorImageView.setImageResource(R.drawable.progress_rectangle_selected)
                        else indicatorImageView.setImageResource(R.drawable.progress_rectangle_unselected)
                    }
                }
            })

            setImageListener { position, imageView ->
                imageView.apply {
                    setImageResource(heroImagesId[position])
                    scaleType = ImageView.ScaleType.FIT_CENTER

                    setOnClickListener {
                        if (position == 1) {
//                            toast.warning(
//                                msg = "You have clicked on View: $position",
//                                gravity = Gravity.CENTER
//                            )

                            val intent = Intent(activity, CallToActionActivity::class.java)
                            intent.putExtra("cta_type", "consultant")
                            startActivity(intent)
                        } else if (position == 2) {
//                            toast.error(
//                                msg = "You have clicked on View: $position",
//                                gravity = Gravity.CENTER
//                            )

                            val intent = Intent(activity, CallToActionActivity::class.java)
                            intent.putExtra("cta_type", "consultant")
                            startActivity(intent)
                        } else if (position == 0) {
//                            toast.success(
//                                msg = "You have clicked on View: $position",
//                                gravity = Gravity.CENTER
//                            )

                            val intent = Intent(activity, CallToActionActivity::class.java)
                            intent.putExtra("cta_type", "consultant")
                            startActivity(intent)
                        }
                    }
                }
            }

            pageCount = heroImagesId.size
        }

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        tv_title.text = goal.title.toUpperCase()
        tv_deadline.text = "by ${dateFormatter.format(goal.deadline)}"

        Handler().post {

            // reset navigation button on right side of toolbar
            constraint_layout_confirm_currentAmt_changes.visibility = View.GONE
            constraint_layout_edit_goal_details.visibility = View.VISIBLE

            val categoryStrToBtn = mapOf<String, Int>(
                "career" to btn_career.id,
                "family" to btn_family.id,
                "leisure" to btn_leisure.id
            )

            // uncheck all toggleGrp buttons
            categoryStrToBtn["career"]?.let { id ->
                categories_toggleGrp_show_goal_details.uncheck(id)
            }
            categoryStrToBtn["family"]?.let { id ->
                categories_toggleGrp_show_goal_details.uncheck(id)
            }
            categoryStrToBtn["leisure"]?.let { id ->
                categories_toggleGrp_show_goal_details.uncheck(id)
            }


            // hide all buttons in toggle group
            btn_career.visibility = View.GONE
            btn_family.visibility = View.GONE
            btn_leisure.visibility = View.GONE


            // check toggleGrp buttons based on goal.categories
            goal.categories.forEach {
                categoryStrToBtn[it]?.let { id ->
                    categories_toggleGrp_show_goal_details.check(id)
                }
            }

            // unhide all buttons in toggle group
            btn_career.visibility = View.VISIBLE
            btn_family.visibility = View.VISIBLE
            btn_leisure.visibility = View.VISIBLE


            computeAndDisplayProgress()

        }

        tv_goalAmt.text = DecimalFormat("$#,###").format(goal.targetAmt)
        tv_currentAmt.text = DecimalFormat("$#,###").format(goal.currentAmt)


        if (goal.completed) {
            btn_CompleteGoal.isEnabled = false
            btn_CompleteGoal.text = "Completed"
        }

        btn_Resources.setOnClickListener {

            val intent = Intent(activity, CallToActionActivity::class.java)
            intent.putExtra("cta_type", "resources")
            intent.putStringArrayListExtra("categories", goal.categories)

            startActivity(intent)

        }

        btn_Journey_View.setOnClickListener {
            val showJourneyPostsFragment = ShowJourneyPostsFragment()
            showJourneyPostsFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(
                showJourneyPostsFragment,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit
            )
        }

        btn_Bucket_List_View.setOnClickListener {
            val showBucketListItemsTabFragment = ShowBucketListItemsTabsFragment()

            showBucketListItemsTabFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }
            fragmentUtils.replace(
                showBucketListItemsTabFragment,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit
            )
        }


        constraint_layout_edit_goal_details.setOnClickListener {
            val editGoalDetailsFragment = EditGoalDetailsFragment()

            editGoalDetailsFragment.arguments = Bundle().apply {
                putParcelable(goal::class.java.name, goal)
            }

            fragmentUtils.replace(
                editGoalDetailsFragment,
                enter = R.anim.fragment_close_enter,
                exit = R.anim.fragment_open_exit,
                popEnter = R.anim.fragment_slide_right_enter,
                popExit = R.anim.fragment_slide_right_exit
            )
        }

        linear_layout_edit_currentAmt.setOnClickListener {
            val currentAmt = tv_currentAmt.text
            et_currentAmt.hint = currentAmt
            et_currentAmt.visibility = View.VISIBLE
            tv_currentAmt.visibility = View.GONE
        }

        et_currentAmt.addTextChangedListener {
            constraint_layout_confirm_currentAmt_changes.visibility = View.VISIBLE
            constraint_layout_edit_goal_details.visibility = View.GONE
        }

        // this click listener will update the record for the current Goal based on the new Current Amount
        constraint_layout_confirm_currentAmt_changes.setOnClickListener {
            if (!goal.gid?.isBlank()!!) {

                // first check that currentAmount does not exceed targetAmount
                val errors = mutableMapOf<String, String>()
                val moneyRegex = Regex("\\d+?\\.\\d{3,}")

                et_currentAmt.error = null

                when {
                    moneyRegex.matches(et_currentAmt.text.toString()) -> errors["currentAmt"] =
                        "Cents can't be more than 2 digits"
                    et_currentAmt.text.toString()
                        .toDouble() > goal.targetAmt -> errors["currentAmt"] =
                        "Please enter an amount less than or equal to the Target Amount"
                }

                if (errors.isNotEmpty()) {
                    errors["currentAmt"].let { et_currentAmt.error = it }
                } else {

                    // reassign currentAmt of Goal Model if there are no errors in Edit Text CurrentAMt
                    goal.currentAmt = et_currentAmt.text.toString().toDouble()

                    goal.setByGid {
                        if (it != null) {
//                            toast.success("Current amount updated for Goal")

                            // Change visibility of toolbar (top right)
                            constraint_layout_confirm_currentAmt_changes.visibility = View.GONE
                            constraint_layout_edit_goal_details.visibility = View.VISIBLE

                            // Display updated goal.currentAmt in tv_currentAmt
                            tv_currentAmt.text = DecimalFormat("$#,###").format(goal.currentAmt)
                            et_currentAmt.visibility = View.GONE
                            tv_currentAmt.visibility = View.VISIBLE
                            computeAndDisplayProgress()
                        } else toast.error("Please try again, there was an issue when updating the current amount.")
                    }
                }
            }
        }

        btn_DeleteGoal.setOnClickListener {
            Goal(gid = goal.gid).deleteByGid()
            fragmentUtils.popBackStack()
        }

        btn_CompleteGoal.setOnClickListener {
            val bucketFilter = goal.bucketList.toList().filter { !(it["completed"] as Boolean) }

            if (goal.currentAmt < goal.targetAmt) {
                toast.error("Current Amount needs to be more than or equals to Targeted Amount")
            } else if (!bucketFilter.isNullOrEmpty()) {
                toast.error("Bucket List contains in progress item(s)")
            } else {
                goal.completed = true
                goal.updateCompleteByGid {
                    if (it != null) {
                        btn_CompleteGoal.isEnabled = false
                        toast.success("Goal completed successfully")
                    } else toast.error("Please try again, there was an issue completing the goal")
                }
            }
        }
    }

    private fun computeAndDisplayProgress() {
        val monetaryPercent = (goal.currentAmt / goal.targetAmt * 100).roundToInt()


        val bucketListPercent = if (goal.bucketList.size > 0) {
            (goal.bucketList.filter { it["completed"] as Boolean }
                .toMutableList().size.toDouble() / goal.bucketList.size.toDouble() * 100).roundToInt()
        } else 100

        val overallPercent = if (goal.bucketList.size < 1) {
            monetaryPercent
        } else (monetaryPercent + bucketListPercent) / 2


        tv_overallProgress.text = "$overallPercent%"
        progressIndicator_overallProgress.progress = overallPercent
    }

    private fun configureToolbar() {
        toolbar_view_goal_details.setNavigationIcon(R.drawable.toolbar_back_icon)
        toolbar_view_goal_details.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}