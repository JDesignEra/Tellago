package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tellago.R
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_settings.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()
        //configureNavigationDrawer()

    }

    private fun configureToolbar() {
        toolbar_Account.setNavigationIcon(R.drawable.ic_arrow_back_36)
        toolbar_Account.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }
    }


    // public fun NavigationBuilder : NavigationBuilder


//    private fun configureNavigationDrawer() {
//        val navigationView: NavigationView = navigation
//
//        navigationView.bringToFront()
//        navigationView.setNavigationItemSelectedListener {
//            onNavigationItemSelected(it)
//            true
//        }
//
//    }

//    fun onNavigationItemSelected(menuItem: MenuItem) {
//        val f: Fragment? = null
//        val menu_itemID = menuItem.itemId
//
//        when (menu_itemID) {
//            R.id.view_profile -> replaceFragment(profileFragment)
//            R.id.logout_from_drawer -> Auth().signOut(this) {
//                val intent = Intent(this, SplashActivity::class.java)
//                startActivity(intent)
//            }
//
//        }
//
//
//        if (f != null) {
//            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, f)
//            transaction.commit()
//            true
//        } else
//            false
//
//        val drawerLayout: DrawerLayout = drawer_layout
//        drawerLayout.closeDrawers()
//    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}