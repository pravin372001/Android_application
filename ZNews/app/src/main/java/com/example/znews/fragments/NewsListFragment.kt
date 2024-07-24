package com.example.znews.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.znews.MainActivity
import com.example.znews.R
import com.example.znews.adapter.CategoryAdapter
import com.example.znews.adapter.NewsAdapter
import com.example.znews.database.NewsModel
import com.example.znews.databinding.FragmentNewsListBinding
import com.example.znews.listeners.CategoryClickListener
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory

class NewsListFragment() : Fragment(), CategoryClickListener {

    private val viewModel: NewsViewModel by activityViewModels {
        NewsViewModelFactory(requireContext())
    }

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity){
            mainActivity = context
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                viewModel.logout()
                findNavController().navigate(R.id.action_newsListFragment_to_loginFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)

        val toolbar = binding.listToolBar
        mainActivity.setSupportActionBar(toolbar)

        newsAdapter = NewsAdapter(emptyList(), onNewsClick = {onNewsClick(it)})
        binding.newsList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        categoryAdapter = CategoryAdapter(viewModel.getCategories(), this, viewModel.getSelectPosition())
        binding.categoryList.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        viewModel.isDbDataInsertedLiveData.observe(viewLifecycleOwner) { isDbDataInserted ->
            if (isDbDataInserted) {
                fetchNews(viewModel.getCategories()[viewModel.getSelectPosition()])
            }
        }

        return binding.root
    }

    override fun onCategoryClicked(category: String) {
        viewModel.fetchFromDb(category)
        viewModel.newsListPage.observe(viewLifecycleOwner) { newsList ->
            newsAdapter = NewsAdapter(newsList, onNewsClick = {onNewsClick(it)})
            binding.newsList.adapter = newsAdapter
        }
    }

    private fun fetchNews(category: String){
        viewModel.fetchFromDb(category)
        viewModel.newsListPage.observe(viewLifecycleOwner) { newsList ->
            newsAdapter = NewsAdapter(newsList, onNewsClick = {onNewsClick(it)})
            binding.newsList.adapter = newsAdapter
        }
    }

    private fun onNewsClick(news: NewsModel){
        viewModel.setCurrentNews(news)
        findNavController().navigate(R.id.action_newsListFragment_to_detailNewsFragment)
    }

    override fun onCategoryPosition(position: Int) {
        Log.d(TAG, "Category position: $position")
        viewModel.selectPosition(position)
    }

    companion object {
        private const val TAG = "NewsListFragment"
    }

}