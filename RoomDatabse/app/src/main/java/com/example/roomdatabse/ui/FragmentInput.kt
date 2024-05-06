package com.example.roomdatabse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.roomdatabse.R
import com.example.roomdatabse.database.PersonRepository
import com.example.roomdatabse.databinding.FragmentInputBinding
import com.example.roomdatabse.model.PersonViewModel
import com.example.roomdatabse.model.PersonViewModelFactory


class FragmentInput : Fragment() {

    private lateinit var viewModel: PersonViewModel
    private lateinit var binding: FragmentInputBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input, container, false)
        binding.lifecycleOwner = this // Set the lifecycle owner for observing LiveData

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this, PersonViewModelFactory(PersonRepository(requireContext())))[PersonViewModel::class.java]

        // Set the ViewModel in the binding
        binding.viewModel = viewModel

        // Set up click listener for the add button
        binding.addButton.setOnClickListener {
            viewModel.addPersonFromInput()
            clearFields() // Clear the input fields after adding a person
            Toast.makeText(requireContext(), "Person added successfully", Toast.LENGTH_SHORT).show()
            // Navigate to the display-delete fragment after adding a person
            findNavController().navigate(FragmentInputDirections.actionFragmentInputToFragmentDisplayDelete())
        }

        return binding.root
    }

    private fun clearFields() {
        // Clear the input fields
        binding.apply {
            nameEditText.text.clear()
            ageEditText.text.clear()
            phoneEditText.text.clear()
            emailEditText.text.clear()
        }
    }
}

