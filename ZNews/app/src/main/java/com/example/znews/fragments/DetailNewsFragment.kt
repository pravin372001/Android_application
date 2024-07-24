package com.example.znews.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
        if (context is MainActivity){
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
            Picasso.get().load(news.imageUrl).into(binding.topImage)
            binding.categoryBadge.text = news.category
            binding.dateTimeText.text = "${news.date}  •  ${news.time}"
            binding.headline.text = news.title
            binding.author.text = news.author
            binding.articleContent.text = news.content
        }
        binding.detailToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}