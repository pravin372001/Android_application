package com.pravin.tripwake.listener

import android.app.Activity

interface ActivityProvider {
    fun getActivity(): Activity
}
