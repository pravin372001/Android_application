package com.example.znews.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.znews.R
import com.example.znews.databinding.FragmentLoginBinding
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory


class LoginFragment : Fragment() {

    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(requireContext())
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val signInButton = binding.loginButton
        signInButton.setOnClickListener {
            viewModel.login(getString(R.string.client_id))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                Log.d(TAG, "User signed in: ${user.displayName}")
                findNavController().navigate(R.id.action_loginFragment_to_newsListFragment)
            } else {
                Log.d(TAG, "User signed out")
            }
        })
    }
    companion object {
        private const val TAG = "SignInFragment"
    }

}