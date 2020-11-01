package com.tellago.fragments

import android.animation.Animator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_community.*
import java.lang.reflect.Field


class CommunityFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )
        //configureToolbar()
        configureSearchToolbar()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater): Boolean {
        inflater.inflate(R.menu.menu_search_home, menu)
        return true
    }

//     override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        inflate(R.menu.menu_search_home, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        // Handle the selected item
        when (item.itemId) {
            R.id.action_search -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(search_toolbar_community_fragment, 1, true, true)
                } else {
                    search_toolbar_community_fragment.visibility = View.VISIBLE
                }
                val item_search =
                    search_toolbar_community_fragment.menu.findItem(R.id.action_filter_search)
                item_search.expandActionView()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // if search bar is in focus, then show 'SEARCH' text (adjust layout weights)
        search_bar_community.setOnQueryTextFocusChangeListener { _, hasFocus ->
            // hasFocus means searchView is selected
            if (hasFocus) {
//                displaySearchText()
                Log.d("toolbar anim 1", "FIRED")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d("toolbar anim 1a", "FIRED")
                    circleReveal(search_toolbar_community_fragment, 1, true, false)
                } else {
                    Log.d("toolbar anim 1b", "FIRED")

                    search_toolbar_community_fragment.visibility = View.GONE
                }
            }

//                community.visibility = View.GONE
//                fragmentUtils.replace(
//                    CommunitySearchFragment(),
//                    null,
//                    enter = R.anim.fragment_slide_left_enter_slow,
//                    exit = R.anim.fragment_slide_left_exit_slow
//                )

            else {
                // changing layout weights
                hideSearchText()

            }

        }

        search_bar_community.setOnSearchClickListener {
//            displaySearchText()
//            fragmentUtils.replace(
//                CommunitySearchFragment(),
//                null,
//                enter = R.anim.fragment_slide_left_enter_slow,
//                exit = R.anim.fragment_slide_left_exit_slow
//            )

            Log.d("toolbar anim 2", "FIRED")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                circleReveal(search_toolbar_community_fragment, 1, true, false)
            else
                search_toolbar_community_fragment.visibility = View.GONE
        }

    }

    public fun configureSearchToolbar() {
        val searchtoolbar = search_toolbar_community_fragment
        if (searchtoolbar != null) {
            searchtoolbar.inflateMenu(R.menu.menu_search)
            val search_menu = searchtoolbar.menu

            searchtoolbar.setNavigationOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(search_toolbar_community_fragment, 1, true, false)
                else
                    searchtoolbar.visibility = View.GONE

            }

            // filter functionality can be done later
            val item_search = search_menu.findItem(R.id.action_filter_search)

//            MenuItemCompat.setOnActionExpandListener(
//                item_search, MenuItemCompat.OnActionExpandListener{
//
//
//                        }
//
//                        )
//
//            )

//            item_search.collapseActionView()
//            item_search.expandActionView()

            @Override
            fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when collapsed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(search_toolbar_community_fragment, 1, true, false)
                } else {
                    searchtoolbar.visibility = View.GONE
                }
                return true
            }

            @Override
            fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when expanded
                return true
            }

            initSearchView()

        }

    }

    fun initSearchView() {
        val searchView: SearchView =
            search_toolbar_community_fragment.menu.findItem(R.id.action_filter_search)
                .getActionView() as SearchView

        // Enable/Disable Submit button in the keyboard
        searchView.setSubmitButtonEnabled(false)

        // Change search close button image
        val closeButton: ImageView = searchView.findViewById(R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable.ic_cancel_grey_48)


        // set hint and the text colors
        val txtSearch = searchView.findViewById(R.id.search_src_text) as EditText
        txtSearch.hint = "Search.."
        txtSearch.setHintTextColor(Color.DKGRAY)
        txtSearch.setTextColor(resources.getColor(R.color.colorPrimary))


        // set the cursor
        val searchTextView =
            searchView.findViewById(R.id.search_src_text) as AutoCompleteTextView
        try {
            val mCursorDrawableRes: Field =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.setAccessible(true)
            mCursorDrawableRes.set(
                searchTextView,
                R.drawable.ic_search
            ) //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (e: Exception) {
            e.printStackTrace()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                callSearch(newText)
                return true
            }

            fun callSearch(query: String) {
                //Do searching
                Log.i("query", "" + query)
            }
        })
    }


    public fun circleReveal(
        viewID: View,
        posFromRight: Int,
        containsOverflow: Boolean,
        isShow: Boolean
    ) {
        val myView = viewID
        var width = myView.width

        if (posFromRight > 0)
            width -= (posFromRight * 48) - (48 / 2)

        if (containsOverflow)
            width -= 36

        val cx: Int = width
        val cy: Int = myView.height / 2

        val anim: Animator

        if (isShow)
            anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0.0F, width.toFloat())
        else
            anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0.0F)

        anim.duration = 220

        // Change View to Invisible after animation has completed
        anim.addListener {
            if (!isShow) {

                myView.visibility = View.INVISIBLE
            }
        }

        // Change View to Visible to start the animation
        if (isShow) myView.visibility = View.VISIBLE

        // Start the animiation
        anim.start()


    }


    private fun hideSearchText() {
        val param = LinearLayout.LayoutParams(
            0,
            120,
            0.97f
        )
//        val param2 = LinearLayout.LayoutParams(
//            0,
//            120,
//            0.0f
//        )
        search_bar_community.setLayoutParams(param)
//        tv_community_search.layoutParams = param2
    }

    private fun displaySearchText() {
        val param = LinearLayout.LayoutParams(
            0,
            120,
            0.75f
        )
//        val param2 = LinearLayout.LayoutParams(
//            0,
//            140,
//            0.25f
//        )
//        val param3 = LinearLayout.LayoutParams(
//            0,
//            120,
//            0.25f
//        )
        search_bar_community.setLayoutParams(param)
//        tv_community_search.layoutParams = param3
    }
}