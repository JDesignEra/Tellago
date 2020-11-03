package com.tellago.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.tellago.MainActivity
import com.tellago.R
import kotlinx.android.synthetic.main.activity_open_p_d_f.*

class OpenPDFActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_open_p_d_f)

        webview_open_pdf.getSettings().setJavaScriptEnabled(true)
        webview_open_pdf.getSettings().setPluginState(WebSettings.PluginState.ON)
//        webview_open_pdf.setWebViewClient(WebChromeClient())

//        val pdfURL
//
//        String pdfURL = "http://dl.dropboxusercontent.com/u/37098169/Course%20Brochures/AND101.pdf";
//        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdfURL);
//
//        setContentView(webView)


    }
}