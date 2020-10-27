package com.tellago.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_resources_web_view.*
import kotlinx.android.synthetic.main.fragment_show_goal_details.*


class ResourcesWebViewFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_resources_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        val received_URL = bundle?.getString("URL")

        webview_resources.settings.javaScriptEnabled = true

        webview_resources.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webview_resources.loadUrl(received_URL)

    }

    private fun configureToolbar() {
        toolbar_resources_web_view.setNavigationIcon(R.drawable.toolbar_cancel_icon)
        toolbar_resources_web_view.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}