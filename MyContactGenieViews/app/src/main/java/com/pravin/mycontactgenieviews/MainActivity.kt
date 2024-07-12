package com.pravin.mycontactgenieviews

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import com.google.android.material.color.DynamicColors
import com.pravin.mycontactgenieviews.databinding.ActivityMainBinding
import com.pravin.mycontactgenieviews.util.FlavorUtils
import com.pravin.mycontactgenieviews.util.getFeatureBoundsInWindow
import com.pravin.mycontactgenieviews.util.isBookPosture
import com.pravin.mycontactgenieviews.util.isFlatPostureHorizontal
import com.pravin.mycontactgenieviews.util.isFlatPostureVertical
import com.pravin.mycontactgenieviews.util.isTableTopPosture
import com.pravin.mycontactgenieviews.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val windowInfoTracker = WindowInfoTracker.getOrCreate(this)
        lifecycleScope.launch {
            windowInfoTracker.windowLayoutInfo(this@MainActivity).collect { layoutInfo ->
                updateLayout(layoutInfo)
            }
        }
        FlavorUtils.printFlavorName()
    }


    private fun updateLayout(layoutInfo: WindowLayoutInfo) {
        val foldState = if(layoutInfo.displayFeatures.isNotEmpty()) {
            layoutInfo.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
        } else {
            null
        }
        if(foldState == null) {
            Log.d("MainFragment", "Fold state is null")
            return
        }

        viewModel.setFoldState(foldState)
        Log.d("MainFragment", "Fold state: $foldState")

        if (foldState != null) {
                when {
                    foldState.isBookPosture() -> {
                        binding.slidingPaneLayout.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.listFragment.layoutParams.width = 900
                        binding.detailFragment.layoutParams.width = 1300
                        binding.detailFragment.requestLayout()
                        binding.listFragment.requestLayout()
                        Log.d("MainFragment", "Book posture detected")
                    }
                    foldState.isTableTopPosture() -> {
                        binding.listFragment.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.detailFragment.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.detailFragment.requestLayout()
                        binding.listFragment.requestLayout()
                        Log.d("MainFragment", "Table top posture detected")
                        // Handle tabletop posture layout changes
                    }
                    foldState.isFlatPostureVertical() -> {
                        binding.listFragment.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.detailFragment.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.detailFragment.requestLayout()
                        binding.listFragment.requestLayout()
                        Log.d("MainFragment", "Flat posture vertical detected")
                    }
                    foldState.isFlatPostureHorizontal() -> {
                        binding.listFragment.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.detailFragment.layoutParams.width = LayoutParams.MATCH_PARENT
                        binding.detailFragment.requestLayout()
                        binding.listFragment.requestLayout()
                        Log.d("MainFragment", "Flat posture horizontal detected")
                    }

                    else -> {
                        Log.d("MainFragment", "Unknown posture detected")
                    }
                }

        } else {
            Log.d("MainFragment", "Fold state is null")
        }
    }


    fun showDetail() {
        val detailFragment = supportFragmentManager.findFragmentById(R.id.detail_fragment) as DetailFragment
        detailFragment.showProfileDetails()
        binding.slidingPaneLayout.openPane()
    }

}
