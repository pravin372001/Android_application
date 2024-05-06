package com.example.roomdatabaseassignment.get_data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.roomdatabaseassignment.R
import com.example.roomdatabaseassignment.database.AppDatabase
import com.example.roomdatabaseassignment.database.Data
import com.example.roomdatabaseassignment.databinding.FragmentEnterDataBinding
import com.example.roomdatabaseassignment.viewmodel.DataViewModel
import com.example.roomdatabaseassignment.viewmodel.DataViewModelFactory


class EnterDataFragment : Fragment() {
    private lateinit var binding: FragmentEnterDataBinding
    private lateinit var viewModel: DataViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_data, container, false)
        val dao = AppDatabase.getDatabase(requireContext()).dataDao()
        viewModel = ViewModelProvider(this, DataViewModelFactory(dao))[DataViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnSave.setOnClickListener {
            viewModel.addData(Data(1, binding.etName.text.toString(), binding.etAge.text.toString().toInt()
                , binding.etAddress.text.toString()
                , binding.etPhone.text.toString()
                , binding.etEmail.text.toString()))
            findNavController().navigate(EnterDataFragmentDirections.actionEnterDataFragmentToDisplayFragment())
        }
        return binding.root
    }
}