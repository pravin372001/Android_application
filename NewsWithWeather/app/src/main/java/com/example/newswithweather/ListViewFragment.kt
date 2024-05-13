package com.example.newswithweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newswithweather.adapter.CategoryAdapter
import com.example.newswithweather.adapter.NewsAdapter
import com.example.newswithweather.database.NewsModel
import com.example.newswithweather.databinding.FragmentListViewBinding
import com.example.newswithweather.repository.NewsRepository
import com.example.newswithweather.repository.WeatherRepository
import com.example.newswithweather.viewmodel.NewsViewModel
import com.example.newswithweather.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class ListViewFragment : Fragment(), CategoryClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var binding: FragmentListViewBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationName = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_list_view, container, false)

        viewModel = ViewModelProvider(this,
            NewsViewModel.NewsViewModelFactory(NewsRepository(requireContext()))
        )[NewsViewModel::class.java]
        setHasOptionsMenu(true);
        weatherViewModel = ViewModelProvider(this,
            WeatherViewModel.WeatherViewModelFactory(WeatherRepository(requireContext()))
        )[WeatherViewModel::class.java]

        viewModel.deleteNews()
        recyclerView = binding.horizontalRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        val categoryList = getCategories()
        val adapter = CategoryAdapter(categoryList, this)
        recyclerView.adapter = adapter

        for(category in categoryList){
            viewModel.fetchNews(category)
        }

        setNewsAdapter("all")
        newsRecyclerView = binding.verticalRecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsRecyclerView.setHasFixedSize(true)
        // Check and request location permissions
        if (requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
        locationName.observe(viewLifecycleOwner, Observer {
            getWeatherData(it)
        })
        binding.fab.setOnClickListener{
            locationName.observe(viewLifecycleOwner, Observer {
                getWeatherData(it)
            })
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterData(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text changes
                return false
            }
        })

        searchView.setOnCloseListener {
            // Clear the search query and show category content
            filterData(null)
            false
        }
    }

    private fun filterData(query: String?) {
        val filteredList = viewModel.filterNews(query)
        filteredList.observe(viewLifecycleOwner, Observer{
            newsRecyclerView.adapter = NewsAdapter(this, it)
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_search -> {
                val searchItem = item
                searchItem.expandActionView()
                val searchView = searchItem.actionView as SearchView
                searchView.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun formatTemperature(temperature: String): String {
        return "$temperature °C"
    }
    private fun getCategories(): List<String>{
        val apiCatagoryList: ArrayList<String> = arrayListOf("all","national", "business",
            "sports", "world",
            "politics", "technology", "startup", "entertainment",
            "miscellaneous", "hatke", "science", "automobile")
        return apiCatagoryList
    }

    private fun setNewsAdapter(category: String) {
        var newsList: LiveData<List<NewsModel>> = MutableLiveData<List<NewsModel>>()
        when (category) {
            "all" -> {
                newsList = viewModel.getAllNews()
            }
            else -> {
                newsList = viewModel.getNewsByCategory(category)
            }
        }

        binding.progressBar?.isVisible = true
        binding.progressBar?.setProgress(2, true)

        Handler().postDelayed({
            newsList.observe(viewLifecycleOwner) { it ->
                newsRecyclerView.adapter = NewsAdapter(this, it)
                binding.progressBar?.isVisible = false // Hide progress bar after loading data
            }
        }, 2000)
    }


    override fun onCategoryClicked(category: String) {
        setNewsAdapter(category = category)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        // Get the last known location from Fused Location Provider
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Convert coordinates to location name
                    locationName.value = convertCoordinatesToLocation(latitude, longitude).split(",")[2]
                    Log.d("ListViewFragment -> location", "Location Name: ${locationName.value}")
                } ?: run {
                    Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun convertCoordinatesToLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val locationName = addresses?.get(0)?.getAddressLine(0)
//        Toast.makeText(requireContext(), "Location Name: $locationName", Toast.LENGTH_LONG).show()
        Log.d("ListViewFragment -> location", "Location Name: $locationName")
        return locationName?:"Unknown"
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 123
    }

    private fun getWeatherData(location: String){
        weatherViewModel.fetchWeatherData(location)
        weatherViewModel.weatherData.observe(viewLifecycleOwner, Observer {
            binding.fab.text = formatTemperature(it.data.values.temperature.toString())
            Toast.makeText(requireContext(), "Temperature: ${it.data.values.temperature}", Toast.LENGTH_SHORT).show()
        })
    }
}