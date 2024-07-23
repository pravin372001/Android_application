package com.example.news24

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.news24.databinding.FragmentLoginBinding
import com.example.news24.viewmodel.NewsViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            val idToken = getString(R.string.default_web_client_id) // Replace with actual token retrieval logic

            newsViewModel.onGoogleSignIn(
                idToken,
                onSuccess = {
                    findNavController().navigate(R.id.action_loginFragment_to_newsListFragment)
                },
                onFailure = { exception ->
                    Log.e("LoginFragment", "Google sign-in failed", exception)
                }
            )
        }

        return binding.root
    }
}
