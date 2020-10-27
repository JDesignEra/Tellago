package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
//                val previousText = tv_external_resources_categories.text
//                tv_external_resources_categories.text =
//                    String.format("$previousText ${System.lineSeparator()} $category")

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
        val resourcesWebViewFragment = ResourcesWebViewFragment()
        // For Category of Career
        tv_career_link_1.setOnClickListener {

//            confirmEditProfileAlert()

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.forbes.com/sites/work-in-progress/2011/03/23/five-simple-tips-to-advance-your-career/#64b4db485473"
            )
        }
        tv_career_link_1.text = "5 Simple Tips To Advance Your Career"

        tv_career_link_2.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.skillsfuture.sg/skills-framework"
            )

        }
        tv_career_link_2.text = "Skills Framework"

        tv_career_link_3.setOnClickListener {

            openURLinWebView(resourcesWebViewFragment, "https://www.lli.sg/")
        }
        tv_career_link_3.text = "Lifelong Learning Institute"

        tv_career_link_4.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://psgsfts.enterprisejobskills.sg/Course_Internet/"
            )
        }
        tv_career_link_4.text = "Enterprise Job Skills"

        tv_career_link_5.setOnClickListener {


            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.skillsfuture.sg/psgtrainingsubsidy"
            )
        }
        tv_career_link_5.text = "Productivity Solutions Grant Training Subsidy"

        tv_career_link_6.setOnClickListener {


            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.cpf.gov.sg/eSvc/Web/Schemes/RetirementCalculator/CoverPage"
            )
        }
        tv_career_link_6.text = "Retirement Calculator"


        // For Category of Family
        tv_family_link_1.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.hdb.gov.sg/cs/infoweb/residential/buying-a-flat/new/eligibility"
            )
        }
        tv_family_link_1.text = "HDB BTO Eligibility"

        tv_family_link_2.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.rom.gov.sg/reg_info/rom_marriage_process.asp"
            )

        }
        tv_family_link_2.text = "Registry Of Marriage Process"

        tv_family_link_3.setOnClickListener {


            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.healthhub.sg/live-healthy/370/choosing_right_birth_control"
            )

        }
        tv_family_link_3.text = "Healthhub Singapore"

        tv_family_link_4.setOnClickListener {


            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.ica.gov.sg/citizen/birth/citizen_birth_register"
            )

        }
        tv_family_link_4.text = "Citizen Birth Registration"

        tv_family_link_5.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://cms.ecda.gov.sg/prweb/SubsidyCalculator/zGwoaxwY6Bz0rcpuMWgTMg%5B%5B*/!STANDARD"
            )

        }
        tv_family_link_5.text = "Citizen Subsidy Calculator"

        // For Category of Leisure
        tv_leisure_link_1.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://tripzilla.sg/travel"
            )

        }
        tv_leisure_link_1.text = "Tripzilla"

        tv_leisure_link_2.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.visitsingapore.com/see-do-singapore/beyond-singapore/"
            )

        }
        tv_leisure_link_2.text = "Visit Singapore"

        tv_leisure_link_3.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.stb.gov.sg/content/stb/en/media-centre/media-releases/SingapoRediscovers-and-Expanded-Attractions-Guidelines.html"
            )
        }
        tv_leisure_link_3.text = "Singapore Tourism Board"

        tv_leisure_link_4.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.singaporeair.com/en_UK/sg/plan-travel/discover-your-sia/restaurant-a380-at-changi/"
            )
        }
        tv_leisure_link_4.text = "Restaurant a380 At Changi"

        tv_leisure_link_5.setOnClickListener {

            openURLinWebView(
                resourcesWebViewFragment,
                "https://www.rwsentosa.com/en/articles/hotels/guide-to-rws-hotels-and-best-staycation-packages"
            )
        }
        tv_leisure_link_5.text = "Resorts World Sentosa"


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

    private fun confirmEditProfileAlert() {
        val newFragment = ConfirmEditProfileFragment()
        newFragment.show(requireActivity().supportFragmentManager, "Edit Profile Confirmation")
    }


}