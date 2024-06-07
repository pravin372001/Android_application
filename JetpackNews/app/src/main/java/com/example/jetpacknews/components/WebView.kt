package com.example.jetpacknews.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.jetpacknews.ui.theme.JetpackNewsTheme

@Composable
fun ChromeView(
    modifier: Modifier = Modifier.fillMaxSize(),
    url: String,
    webViewClient: WebViewClient = WebViewClient()
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                this.webViewClient = webViewClient
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

@Preview
@Composable
private fun ChromePreview() {
    JetpackNewsTheme {
        ChromeView(url="www.google.com")
    }
}