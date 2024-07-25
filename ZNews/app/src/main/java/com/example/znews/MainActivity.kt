package com.example.znews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.znews.databinding.ActivityMainBinding
import com.example.znews.util.isBookPosture
import com.example.znews.util.isFlatPostureHorizontal
import com.example.znews.util.isFlatPostureVertical
import com.example.znews.util.isTableTopPosture
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowInfoTracker = WindowInfoTracker.getOrCreate(this)
        lifecycleScope.launch {
            windowInfoTracker.windowLayoutInfo(this@MainActivity).collect { layoutInfo ->
                updateLayout(layoutInfo)
            }
        }

        if(true){
            binding.slidingPaneLayout.visibility = View.VISIBLE
            binding.loginFragment.visibility = View.GONE
        } else {
            binding.slidingPaneLayout.visibility = View.GONE
            binding.loginFragment.visibility = View.VISIBLE
        }

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

        when {
            foldState.isTableTopPosture() -> {
                binding.listView.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.detailView.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.detailView.requestLayout()
                binding.listView.requestLayout()
                Log.d("MainFragment", "Fold state is table top")
            }
            foldState.isBookPosture() -> {
                binding.slidingPaneLayout.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.listView.layoutParams.width = 1000
                binding.detailView.layoutParams.width = 1300
                binding.detailView.requestLayout()
                binding.listView.requestLayout()
                Log.d("MainFragment", "Fold state is book")
            }
            foldState.isFlatPostureVertical() -> {
                binding.listView.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.detailView.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.detailView.requestLayout()
                binding.listView.requestLayout()
                Log.d("MainFragment", "Fold state is flat vertical")
            }
            foldState.isFlatPostureHorizontal() -> {
                binding.listView.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.detailView.layoutParams.width = LayoutParams.MATCH_PARENT
                binding.detailView.requestLayout()
                binding.listView.requestLayout()
                Log.d("MainFragment", "Fold state is flat horizontal")
            }
            else -> {
                Log.d("MainFragment", "Fold state is not supported")
            }
        }

    }

    fun getBinding(): ActivityMainBinding {
        return binding
    }
}