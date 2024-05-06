package com.example.roomdatabaseassignment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.roomdatabaseassignment.R
import com.example.roomdatabaseassignment.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)

        binding.button.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEnterDataFragment())
        }

        return binding.root
    }
}