package com.pravin.mycontactgenieviews

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {

    private lateinit var _slidingPaneLayout: SlidingPaneLayout
    val slidingPaneLayout get() = _slidingPaneLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _slidingPaneLayout = findViewById(R.id.sliding_pane_layout)
        DynamicColors.applyToActivityIfAvailable(this)
    }

    fun showDetail() {
        val detailFragment = supportFragmentManager.findFragmentById(R.id.detail_fragment) as DetailFragment
        detailFragment.showProfileDetails()
        slidingPaneLayout.openPane()
    }
}
