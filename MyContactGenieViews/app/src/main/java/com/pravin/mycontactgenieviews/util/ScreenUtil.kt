package com.pravin.mycontactgenieviews.util

import android.util.Log
import com.squareup.picasso.BuildConfig

class FlavorUtils {
    companion object {
        fun printFlavorName() {
            Log.d("FlavorUtils", BuildConfig.FLAVOR)
        }
    }
}


