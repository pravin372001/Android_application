package com.example.newswithweather.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.newswithweather.R
import com.example.newswithweather.databinding.FragmentWebViewBinding

class WebView : Fragment() {

    private lateinit var webView: WebView
    private lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)

        webView = binding.webView
        webView.getSettings().setJavaScriptEnabled(true)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Intercept URL loading requests here
                val url = request?.url.toString()
                Log.d("WebViewFragment", "Loading URL: $url")
                // You can handle the URL here, extract the ReadMoreUrl and handle it as needed
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        // Load the URL
        val readMoreUrl = arguments?.getString("readMoreUrl")
        if (!readMoreUrl.isNullOrEmpty()) {
            webView.loadUrl(readMoreUrl)
        }
        return binding.root
    }

}