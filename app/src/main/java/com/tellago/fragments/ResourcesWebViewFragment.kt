package com.tellago.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.tellago.R
import com.tellago.utilities.FragmentUtils
import kotlinx.android.synthetic.main.fragment_resources_web_view.*


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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()


        val received_URL = bundle?.getString("URL")

        webview_resources.settings.javaScriptEnabled = true
        webview_resources.settings.pluginState = WebSettings.PluginState.ON

        // to support PDF viewing in WebView
        webview_resources.settings.builtInZoomControls = true
        webview_resources.settings.displayZoomControls = false

        // setting WebChromeClient (require API lv 26)
        webview_resources.webChromeClient = WebChromeClient()



        webview_resources.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                webview_resources.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()")

            }
        }
        if (received_URL != null) {
            if (received_URL.contains(".pdf"))
            {
                Log.d("this is PDF", "FIRED")
                // Load URL as PDF
//                webview_resources.loadUrl("https://docs.google.com/gview?embedded=true&url=" + received_URL.getPdfURL())
                val urlToOpen = "https://docs.google.com/gview?embedded=true&url=" + received_URL
                Log.d("To Open The Following: ", urlToOpen)


                Handler().postDelayed(
                    {
                        Log.d("Handler Post ran: ", "FIRED")
                        webview_resources.loadUrl(urlToOpen)


                        Log.d("Running Download Act: ", "FIRED")
                        webview_resources.setDownloadListener(
                            DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                                val i = Intent(Intent.ACTION_QUICK_VIEW)
                                i.data = Uri.parse(url)
                                startActivity(i)
                            }

                        )


                    }, 600
                )

            }
            else {
                // Load URL without PDF
                webview_resources.loadUrl(received_URL)
            }
        }


    }

    private fun configureToolbar() {
        toolbar_resources_web_view.setNavigationIcon(R.drawable.toolbar_cancel_icon)
        toolbar_resources_web_view.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            fragmentUtils.popBackStack()
        }
    }
}