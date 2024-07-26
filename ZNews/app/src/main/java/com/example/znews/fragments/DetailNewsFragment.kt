package com.example.znews.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.znews.MainActivity
import com.example.znews.R
import com.example.znews.databinding.FragmentDetailNewsBinding
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory
import com.squareup.picasso.Picasso

class DetailNewsFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentDetailNewsBinding
    private val viewModel: NewsViewModel by activityViewModels {
        NewsViewModelFactory(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailNewsBinding.inflate(inflater, container, false)

        val toolbar = binding.detailToolbar
        mainActivity.setSupportActionBar(toolbar)
        viewModel.currentNews.observe(viewLifecycleOwner) { news ->
            Log.d("DetailNewsFragment", "News: $news")
            if (news.image_url.isEmpty()) {
                binding.topImage.setImageResource(R.drawable.znews)
            } else {
                if(news.image_url.contains("content://")){
                    val imageUri = Uri.parse(news.image_url)
                    Log.d("DetailNewsFragment", "Image URI: $imageUri")
                    Picasso.get().load(imageUri).error(R.drawable.znews).into(binding.topImage)
                } else {
                    Picasso.get().load(news.image_url).error(R.drawable.znews).into(binding.topImage)
                }
            }
            binding.categoryBadge.text = news.category
            binding.dateTimeText.text = news.pubDate
            binding.headline.text = news.title
            binding.author.text = if(news.creator.isNotEmpty()) "Author: ${news.creator}" else  "Author: Unknown"
            binding.articleContent.text = news.description.ifEmpty { news.title }
        }
        binding.detailToolbar.setNavigationOnClickListener {
            mainActivity.getBinding().slidingPaneLayout.closePane()
        }

        return binding.root
    }

}