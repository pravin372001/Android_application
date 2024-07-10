package com.pravin.automationdemo

import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var loadWebView by remember { mutableStateOf(false) }

                if (loadWebView) {
                    WebViewScreen(url = "https://labour.tn.gov.in/services/users/login")
                } else {
                    Button(onClick = { loadWebView = true }) {
                        Text("Go to WebView")
                    }
                }
            }
        }
    }
}

@Composable
fun WebViewScreen(url: String) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // JavaScript to fill a text field
                    view?.evaluateJavascript(
                        "document.getElementById('usernamet').value = 'jweightronics2014@gmail.com';", null)

                    // JavaScript to select a dropdown option
                    view?.evaluateJavascript(
                        "document.getElementById('passwordt').value = 'AJs@2004';", null)

                    // Trigger any necessary events if needed
                    view?.evaluateJavascript(
                        "document.getElementById('').click();", null)
                }
            }
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(url)
        }
    }, update = { webView ->
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // Ignore SSL certificate errors
                handler?.proceed()
            }
        }

        webView.loadUrl(url)
    })
}
