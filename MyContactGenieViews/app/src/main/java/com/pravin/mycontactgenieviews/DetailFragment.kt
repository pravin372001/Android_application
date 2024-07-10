package com.pravin.mycontactgenieviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pravin.mycontactgenieviews.databinding.FragmentDetailBinding
import com.squareup.picasso.Picasso
import com.pravin.mycontactgenieviews.remote.model.Result
import com.pravin.mycontactgenieviews.viewmodel.ContactViewModel

class DetailFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var result: Result
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
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(mainActivity)[ContactViewModel::class.java]
        viewModel.currentContact.observe(viewLifecycleOwner) { contact ->
            result = contact
            showProfileDetails()
        }
        val toolbar = binding.detailToolbar
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            mainActivity.slidingPaneLayout.closePane()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showProfileDetails() {
        Picasso.get().load(result.picture.large).into(binding.detailImage)
        binding.detailName.text = "${result.name.title} ${result.name.first} ${result.name.last}"
        binding.detailPhone.text = "Phone: ${result.phone}"
        binding.detailMobile.text = "Mobile: ${result.cell}"
        binding.detailEmail.text = "Email: ${result.email}"

    }
}
