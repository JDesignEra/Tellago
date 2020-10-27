package com.tellago.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.fragments.*
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.activity_call_to_action.*
import kotlinx.android.synthetic.main.fragment_external_resources.*


class CallToActionActivity : AppCompatActivity() {
    private lateinit var fragmentUtils: FragmentUtils
    private var intentFrom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils =
            FragmentUtils(supportFragmentManager, R.id.fragment_container_call_to_action_activity)
        intentFrom = intent.getStringExtra("cta_type")
        Log.d("intent is: ", intentFrom)

        setContentView(R.layout.activity_call_to_action)

        // intentFrom = "resources"
        if (intentFrom == "resources") {
            val externalResourcesFragment: Fragment = ExternalResourcesFragment()

            val categoriesArray = intent.getStringArrayListExtra("categories") as ArrayList<String>

            externalResourcesFragment.arguments = Bundle().apply {
                putStringArrayList("arrayListString", categoriesArray)

            }

            fragmentUtils.replace(externalResourcesFragment, null, false)
        }

        // intentFrom = "consultant"
        else if (intentFrom == "consultant") {
            val contactFinancialConsultantFragment: Fragment = ContactFinancialConsultantFragment()


            fragmentUtils.replace(contactFinancialConsultantFragment, null, false)
        }

        configureToolbar()
    }



    private fun configureToolbar() {
        setSupportActionBar(toolbar_callToAction as Toolbar?)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.setTitle("Call To Action")
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.toolbar_back_icon)

        (toolbar_callToAction as Toolbar?)?.setNavigationOnClickListener {
            finish()
//            if (intentFrom == "consultant") {
//                finish()
//            }
//            else {
//                fragmentUtils.popBackStack()
//            }
        }
    }


}