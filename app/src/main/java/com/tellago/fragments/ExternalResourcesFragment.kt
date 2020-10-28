package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_external_resources.*


class ExternalResourcesFragment : Fragment() {
    private lateinit var fragmentUtils: FragmentUtils
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentUtils =
            FragmentUtils(
                requireActivity().supportFragmentManager,
                R.id.fragment_container_call_to_action_activity
            )


        if (this.arguments != null) bundle = requireArguments()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_external_resources, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesArrayList = bundle?.getStringArrayList("arrayListString")

        if (categoriesArrayList != null) {
            for (category in categoriesArrayList) {

                if (category == "career") {
                    linear_layout_category_career.visibility = View.VISIBLE
                } else if (category == "family") {
                    linear_layout_category_family.visibility = View.VISIBLE
                } else if (category == "leisure") {
                    linear_layout_category_leisure.visibility = View.VISIBLE
                }
            }
        }

        // On click listeners will open external links

        // For Category of Career
        tv_career_link_1.setOnClickListener {
            val url = "https://www.forbes.com/sites/work-in-progress/2011/03/23/five-simple-tips-to-advance-your-career/#64b4db485473"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_career_link_1.text = "5 Simple Tips To Advance Your Career"

        tv_career_link_2.setOnClickListener {

            val url = "https://www.skillsfuture.sg/skills-framework"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_career_link_2.text = "Skills Framework"

        tv_career_link_3.setOnClickListener {

            val url = "https://www.lli.sg/"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_career_link_3.text = "Lifelong Learning Institute"

        tv_career_link_4.setOnClickListener {

            val url = "https://psgsfts.enterprisejobskills.sg/Course_Internet/"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_career_link_4.text = "Enterprise Job Skills"

        tv_career_link_5.setOnClickListener {

            val url = "https://www.skillsfuture.sg/psgtrainingsubsidy"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_career_link_5.text = "Productivity Solutions Grant Training Subsidy"

        tv_career_link_6.setOnClickListener {

            val url = "https://www.cpf.gov.sg/eSvc/Web/Schemes/RetirementCalculator/CoverPage"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_career_link_6.text = "Retirement Calculator"


        // For Category of Family
        tv_family_link_1.setOnClickListener {

            val url = "https://www.hdb.gov.sg/cs/infoweb/residential/buying-a-flat/new/eligibility"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_family_link_1.text = "HDB BTO Eligibility"

        tv_family_link_2.setOnClickListener {

            val url = "https://www.rom.gov.sg/reg_info/rom_marriage_process.asp"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_family_link_2.text = "Registry Of Marriage Process"

        tv_family_link_3.setOnClickListener {

            val url = "https://www.healthhub.sg/live-healthy/370/choosing_right_birth_control"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_family_link_3.text = "Healthhub Singapore"

        tv_family_link_4.setOnClickListener {

            val url = "https://www.ica.gov.sg/citizen/birth/citizen_birth_register"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_family_link_4.text = "Citizen Birth Registration"

        tv_family_link_5.setOnClickListener {

            val url = "https://cms.ecda.gov.sg/prweb/SubsidyCalculator/zGwoaxwY6Bz0rcpuMWgTMg%5B%5B*/!STANDARD"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_family_link_5.text = "Citizen Subsidy Calculator"

        // For Category of Leisure
        tv_leisure_link_1.setOnClickListener {

            val url = "https://tripzilla.sg/travel"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_leisure_link_1.text = "Tripzilla"

        tv_leisure_link_2.setOnClickListener {

            val url = "https://www.visitsingapore.com/see-do-singapore/beyond-singapore/"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }

        }
        tv_leisure_link_2.text = "Visit Singapore"

        tv_leisure_link_3.setOnClickListener {

            val url = "https://www.stb.gov.sg/content/stb/en/media-centre/media-releases/SingapoRediscovers-and-Expanded-Attractions-Guidelines.html"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_leisure_link_3.text = "Singapore Tourism Board"

        tv_leisure_link_4.setOnClickListener {

            val url = "https://www.singaporeair.com/en_UK/sg/plan-travel/discover-your-sia/restaurant-a380-at-changi/"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_leisure_link_4.text = "Restaurant a380 At Changi"

        tv_leisure_link_5.setOnClickListener {

            val url = "https://www.rwsentosa.com/en/articles/hotels/guide-to-rws-hotels-and-best-staycation-packages"
            confirmOpenURLAlert(url)
            bundle?.apply {
                putString("openURL", url)
            }
        }
        tv_leisure_link_5.text = "Resorts World Sentosa"


    }



    private fun confirmOpenURLAlert(url: String) {
        val newFragment = ConfirmOpenURLFragment(url)
        newFragment.show(requireActivity().supportFragmentManager, "Open URL Confirmation")
    }


    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the ConfirmEditProfileFragment.NoticeDialogListener interface
    fun onDialogPositiveClick(dialog: DialogFragment, inputURL: String) {
        // User touched the dialog's positive button
        Log.d("User press Positive FRG", "FIRED")

    }

    fun onDialogNegativeClick(dialog: DialogFragment) {
        Log.d("User press Negative FRG", "FIRED")

    }


}