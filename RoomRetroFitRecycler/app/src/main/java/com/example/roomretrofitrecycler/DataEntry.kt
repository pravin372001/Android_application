package com.example.roomretrofitrecycler

import GenderViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.roomretrofitrecycler.databinding.FragmentDataEntryBinding
import com.example.roomretrofitrecycler.repository.GenderRepository

class DataEntry : Fragment() {

    private lateinit var binding: FragmentDataEntryBinding
    private lateinit var viewModel: GenderViewModel
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_entry, container, false)
        viewModel = ViewModelProvider(this,
                        GenderViewModel.Factory(GenderRepository(requireContext())))[GenderViewModel::class.java]

        binding.get.setOnClickListener {
            val name = binding.name.text.toString()
            viewModel.fetchGender(name)
        }

        viewModel.gender.observe(viewLifecycleOwner, Observer { gender ->
            val percentage = gender.probability*100
            binding.output.text = "${gender.name} is ${gender.gender} with  $percentage% Probability"
            viewModel.insertGender(gender)
        })

        binding.show.setOnClickListener {
            findNavController().navigate(DataEntryDirections.actionDataEntry2ToHistoryList2())
        }

        return binding.root
    }

}