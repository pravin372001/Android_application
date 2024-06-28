package com.example.jetpacknews

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpacknews.components.BottomNavigation
import com.example.jetpacknews.components.ChromeView
import com.example.jetpacknews.components.NewsHome
import com.example.jetpacknews.components.WeatherUI
import com.example.jetpacknews.repository.NewsRepository
import com.example.jetpacknews.repository.WeatherRepository
import com.example.jetpacknews.ui.theme.JetpackNewsTheme
import com.example.jetpacknews.viewmodel.NewsViewModel
import com.example.jetpacknews.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, NewsViewModel.NewsViewModelFactory(NewsRepository(this)))[NewsViewModel::class.java]
        weatherViewModel = ViewModelProvider(this, WeatherViewModel.WeatherViewModelFactory(WeatherRepository(this)))[WeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check and request location permissions
        checkLocationPermission()
        Log.d("location -> MainActivity", "onCreate: ")
        getLastKnownLocation()
        weatherViewModel.location.observe(this) {
            Log.d("location -> MainActivity", it)
            if (it != null) {
                weatherViewModel.fetchWeatherData()
            }
        }
        if(isNetworkAvailable(this)){
            viewModel.deleteNews()
            for (it in viewModel.getCatergoriesList()) {
                viewModel.fetchNews(it)
            }
        } else{
//            viewModel.getNews("all")
            viewModel.fetchPagingNews("all")
        }
        viewModel.isDbDataInsertedLiveData.observe(this) {
            if(it){
//                viewModel.getNews("all")
                viewModel.fetchPagingNews("all")
            }
        }
        setContent {
            JetpackNewsTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = NavigationItem.News.route) {
                        composable(NavigationItem.News.route) {
                            NewsHome(
                                modifier = Modifier.padding(innerPadding),
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable(
                            "${NavigationItem.WebViews.route}/{url}",
                            arguments = listOf(
                                navArgument("url") {
                                    type = NavType.StringType
                                }
                            )
                        ) {backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url")
                            ChromeView(
                                modifier = Modifier.fillMaxSize(),
                                url = url!!
                            )
                        }
                        composable(NavigationItem.Weather.route) {
                            weatherViewModel.getWeather()
                            Log.i("Navigation -> MainActivity", "Weather")
                            WeatherUI(weatherViewModel = weatherViewModel)
                        }
                    }
                }
            }
        }

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    private fun getLastKnownLocation() {
        Log.i("location -> MainActivity", "getLastKnownLocation")
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val loc = location.latitude to location.longitude
                        weatherViewModel.setLocation("${loc.first}, ${loc.second}")
                        Log.d("location -> MainActivity", loc.toString())
                        getLocationName(loc.first, loc.second)
                    } ?: kotlin.run {
                        Log.d("location -> MainActivity", "Location is null")
                    }
                }
        } else{
            Log.d("location -> MainActivity", "Permission not granted")
        }
    }

    private fun getLocationName(first: Double, second: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(first, second, 1)
        Log.d("location -> MainActivity", addresses.toString())
        if (addresses != null && addresses.isNotEmpty()) {
            val cityName = addresses[0].locality
            weatherViewModel.setLocationName(cityName)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }
}