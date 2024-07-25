package com.example.znews.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.znews.MainActivity
import com.example.znews.R
import com.example.znews.databinding.FragmentAddNewsBinding
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory

class AddNewsFragment : Fragment() {

    private lateinit var binding: FragmentAddNewsBinding
    private lateinit var mainActivity: MainActivity
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
        binding = FragmentAddNewsBinding.inflate(inflater, container, false)

        return binding.root
    }
}