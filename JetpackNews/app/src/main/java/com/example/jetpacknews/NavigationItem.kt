package com.example.jetpacknews

sealed class NavigationItem(var route: String) {
    data object News : NavigationItem("news")
    data object Weather : NavigationItem("weather")
    data object WebViews : NavigationItem("webView")
}