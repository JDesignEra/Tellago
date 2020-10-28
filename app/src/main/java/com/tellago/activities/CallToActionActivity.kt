package com.tellago.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.fragments.ConfirmOpenURLFragment
import com.tellago.fragments.ContactFinancialConsultantFragment
import com.tellago.fragments.ExternalResourcesFragment
import com.tellago.fragments.ResourcesWebViewFragment
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.activity_call_to_action.*


class CallToActionActivity : AppCompatActivity(), ConfirmOpenURLFragment.NoticeDialogListener {
    private lateinit var fragmentUtils: FragmentUtils
    private var intentFrom: String? = null
    private var url : String = ""


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


    private fun confirmOpenURLAlert(url : String) {
        val newFragment = ConfirmOpenURLFragment(url)
        newFragment.show(supportFragmentManager, "Open URL Confirmation")
    }


    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the ConfirmOpenURLFragment.NoticeDialogListener interface
    override fun onDialogPositiveClick(dialog: DialogFragment, inputURL : String) {
        // User touched the dialog's positive button
        Log.d("User pressed Positive", "FIRED")

        val resourcesWebViewFragment = ResourcesWebViewFragment()
        openURLinWebView(resourcesWebViewFragment, inputURL)

    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User touched the dialog's negative button, so close the ConfirmOpenURLFragment()
        supportFragmentManager.beginTransaction().remove(ConfirmOpenURLFragment(url)).commit()

    }

    private fun openURLinWebView(resourcesWebViewFragment: ResourcesWebViewFragment, url: String) {
        resourcesWebViewFragment.arguments = Bundle().apply {
            putString(
                "URL",
                url
            )
            fragmentUtils.replace(resourcesWebViewFragment)
        }
    }


}