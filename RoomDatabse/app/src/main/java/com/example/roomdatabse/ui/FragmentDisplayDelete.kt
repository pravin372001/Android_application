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
import com.example.roomdatabse.model.PersonViewModel
import com.example.roomdatabse.databinding.FragmentDisplayDeleteBinding
import com.example.roomdatabse.model.PersonViewModelFactory

class FragmentDisplayDelete : Fragment() {

    private lateinit var viewModel: PersonViewModel
    private lateinit var binding: FragmentDisplayDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display_delete, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, PersonViewModelFactory(PersonRepository(requireContext()))).get(PersonViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.getPerson().observe(viewLifecycleOwner) { person ->
            if (person != null) {
                binding.displayTextView.text =
                    "Name: ${person.name}\nAge: ${person.age}\nPhone: ${person.phone}\nEmail: ${person.email}"
            } else {
                binding.displayTextView.text = "No person data available"
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deletePerson() // Pass the specific Person object to deletePerson
            Toast.makeText(requireContext(), "Person deleted successfully", Toast.LENGTH_SHORT).show()
            // Optionally, navigate to another destination after deleting the person
            findNavController().navigate(FragmentDisplayDeleteDirections.actionFragmentDisplayDeleteToFragmentInput())

        }

        return binding.root
    }
}

