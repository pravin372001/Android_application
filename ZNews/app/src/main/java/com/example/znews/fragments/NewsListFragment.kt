package com.example.znews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.znews.R
import com.example.znews.databinding.FragmentNewsListBinding
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory

class NewsListFragment : Fragment() {

    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(requireContext())
    }

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.userName.text = user?.displayName
        }
        binding.signOut.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_newsListFragment_to_loginFragment)
        }
        return binding.root
    }
}