package com.example.roomretrofitrecycler

import GenderViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomretrofitrecycler.databinding.FragmentHistoryListBinding
import com.example.roomretrofitrecycler.model.Gender
import com.example.roomretrofitrecycler.repository.GenderRepository

class HistoryList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentHistoryListBinding
    private lateinit var viewModel: GenderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this,
            GenderViewModel.Factory(GenderRepository(requireContext())))[GenderViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_history_list, container, false)
        val genderList = viewModel.getAllGendersList()
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        genderList.observe(viewLifecycleOwner, Observer { genderList->
            recyclerView.adapter = GenderAdapter(genderList)
        })

        binding.clear.setOnClickListener {
            viewModel.deleteAllGenders()
            viewModel.resetSeq()
            Toast.makeText(requireContext(), "Successfully deleted in database", Toast.LENGTH_SHORT).show()
        }

        binding.back.setOnClickListener {
            findNavController().navigate(HistoryListDirections.actionHistoryList2ToDataEntry2())
        }
        return binding.root
    }
}