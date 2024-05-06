package com.example.roomdatabaseassignment.display

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.roomdatabaseassignment.R
import com.example.roomdatabaseassignment.databinding.FragmentDisplayBinding
import com.example.roomdatabaseassignment.viewmodel.DataViewModel

class DisplayFragment : Fragment() {
    private lateinit var binding: FragmentDisplayBinding
    private lateinit var viewModel: DataViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display, container, false)
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }
}