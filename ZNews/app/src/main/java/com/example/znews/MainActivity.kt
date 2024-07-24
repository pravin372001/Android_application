package com.example.znews

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.znews.databinding.ActivityMainBinding
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.my_nav)
        val start = if(viewModel.currentUser()){
            R.id.newsListFragment
        } else {
            R.id.loginFragment
        }
        navGraph.setStartDestination(start)
        navController.graph = navGraph

    }
}