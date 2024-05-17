package com.example.newswithweather.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
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
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newswithweather.listener.CategoryClickListener
import com.example.newswithweather.R
import com.example.newswithweather.adapter.CategoryAdapter
import com.example.newswithweather.adapter.NewsAdapter
import com.example.newswithweather.databinding.FragmentListViewBinding
import com.example.newswithweather.listener.ImageClickListener
import com.example.newswithweather.repository.NewsRepository
import com.example.newswithweather.repository.WeatherRepository
import com.example.newswithweather.viewmodel.NewsViewModel
import com.example.newswithweather.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class ListViewFragment : Fragment(), CategoryClickListener, ImageClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var binding: FragmentListViewBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationName = MutableLiveData<String>()
    private lateinit var newsAdapter: NewsAdapter
    private var currentCategory : String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
            NewsViewModel.NewsViewModelFactory(NewsRepository(requireContext()))
        )[NewsViewModel::class.java]

        Log.i("ListViewFragment -> isNetworkAvailable", isNetworkAvailable(requireContext()).toString())
        if(isNetworkAvailable(requireContext())){
            viewModel.deleteNews()
            for(category in getCategories()){
                viewModel.fetchNews(category)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_view, container, false)

        setHasOptionsMenu(true);
        weatherViewModel = ViewModelProvider(this,
            WeatherViewModel.WeatherViewModelFactory(WeatherRepository(requireContext()))
        )[WeatherViewModel::class.java]

        val categoryList = getCategories()
        val categoryAdapter = CategoryAdapter(categoryList, this)
        binding.horizontalRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = categoryAdapter
        }

        newsAdapter = NewsAdapter(this, this)
        binding.verticalRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = newsAdapter
        }

        viewModel.currentCategory.observe(viewLifecycleOwner, Observer {
            currentCategory = it
            Log.i("ListViewFragment -> currentCategory", it)
        })


        viewModel.isDbDataInsertedLiveData.observe(viewLifecycleOwner, Observer {
            if(it){
                // Initial fetch with all categories
                fetchNews(currentCategory)
            }
            Log.i("ListViewFragment -> isDbDataInsertedLiveData", it.toString())
        })

        // Pagination listener
        binding.verticalRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1) {
                    // Load next page when reaching the end
                    viewModel.incrementPage()
                }
            }
        })

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
            fetchNews(currentCategory)
            true
        }
    }

    override fun onClick(imageUrl: String) {
        val dialog = EnLargeImageDialog(requireContext(), imageUrl)
        dialog.show()
    }

    private fun filterData(query: String?) {
        val filteredList = viewModel.filterNews(query)
        filteredList.observe(viewLifecycleOwner, Observer{
            val mutableFilteredList = it?.toMutableList() ?: mutableListOf()
            newsAdapter.setNews(mutableFilteredList)
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_search -> {
                val searchItem = item
                searchItem.expandActionView()
                val searchView = searchItem.actionView as SearchView
                // Listen for focus change events on the SearchView
                searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        // Hide the keyboard when the SearchView loses focus
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                    }
                }

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

    override fun onCategoryClicked(category: String) {
        // Reset pagination when a new category is selected
        viewModel.resetPage()
        currentCategory = category
        Log.i("ListViewFragment -> onCategoryClicked", category)
        fetchNews(category)
    }

    private fun fetchNews(category: String) {
        viewModel.setCategory(category)
        val currentPage = viewModel.getCurrentPage()
        viewModel.fetchNews()

        Log.i("ListViewFragment", "Fetching news for category: $category, page: $currentPage")
        viewModel.newsListPage.observe(viewLifecycleOwner) { newsList ->
            Log.i("ListViewFragment", "Observed news list: $newsList")
            if (currentPage == 1) {
                newsAdapter.setNews(newsList)
                Log.i("ListViewFragment", "Set news adapter")
            } else {
                newsAdapter.addNews(newsList)
                Log.i("ListViewFragment", "Add news adapter")
            }
        }
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

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }


    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 123
    }

    private fun getWeatherData(location: String){
        if(isNetworkAvailable(requireContext())){
            weatherViewModel.fetchWeatherData(location)
        }
        val currentWeather = weatherViewModel.getWeather()
        currentWeather.observe(viewLifecycleOwner, Observer { weather ->
            if (weather != null) {
                binding.fab.text = formatTemperature(weather.temperature.toString())
            } else {
                // Handle the null case, maybe show an error message or use a default value
                binding.fab.text = "N/A" // Assume you have a default value in strings.xml
                Log.e("ListViewFragment", "CurrentWeather is null")
            }
        })
    }
}