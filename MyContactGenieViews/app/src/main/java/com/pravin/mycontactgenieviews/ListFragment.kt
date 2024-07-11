package com.pravin.mycontactgenieviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pravin.mycontactgenieviews.adapter.ProfileAdapter
import com.pravin.mycontactgenieviews.databinding.FragmentListBinding
import com.pravin.mycontactgenieviews.viewmodel.ContactViewModel

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: ContactViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val toolbar = binding.toolbar
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        viewModel = ViewModelProvider(mainActivity)[ContactViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is ContactViewModel.ContactUiState.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.linearLayout.visibility = View.GONE
                    binding.errorText.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                }
                is ContactViewModel.ContactUiState.Error -> {
                    binding.errorText.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    binding.linearLayout.visibility = View.GONE
                    binding.retryButton.setOnClickListener {
                        viewModel.getContacts()
                    }
                }
                else -> {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    binding.errorText.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                }
            }
        }

        viewModel.contacts.observe(viewLifecycleOwner) { contact ->
            val context = context
            profileAdapter = ProfileAdapter(contact.results) { result ->
                if (context is MainActivity) {
                    viewModel.setCurrentContact(result)
                    context.showDetail()
                }
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = profileAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
