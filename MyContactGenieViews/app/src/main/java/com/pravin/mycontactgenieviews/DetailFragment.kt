package com.pravin.mycontactgenieviews

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.window.layout.FoldingFeature
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
            if (contact != null) {
                result = contact
            }
            showProfileDetails()
        }

        val toolbar = binding.detailToolbar
        mainActivity.setSupportActionBar(toolbar)
//        binding.root.post {
//            configureToolbar()
//        }
//        viewModel.foldState.observe(viewLifecycleOwner){
//            if(it?.state == FoldingFeature.State.HALF_OPENED)
//            {
//                Log.d("DetailFragment", mainActivity.supportActionBar.toString())
//                mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
//                mainActivity.supportActionBar?.setDisplayShowHomeEnabled(false)
//                mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
//            }
//            Log.d("DetailFragment", "Fold state: ${it?.state}")
//        }

        toolbar.setNavigationOnClickListener {
            mainActivity.binding.slidingPaneLayout.closePane()
            viewModel.setCurrentContact(null)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureToolbar() {
        val isHorizontal = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (mainActivity.binding.slidingPaneLayout.isOpen && isHorizontal) {
            Log.d("DetailFragment", "In configureToolbar: ${mainActivity.supportActionBar.toString()}")

            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            mainActivity.supportActionBar?.setDisplayShowHomeEnabled(false)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        } else {
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(true)
        }
    }

    fun showProfileDetails() {
        if(result == null) {
            return
        }
        if(viewModel.foldState.value?.state == FoldingFeature.State.HALF_OPENED || (mainActivity.binding.slidingPaneLayout.isOpen && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)){
            binding.detailToolbar.title = ""
            binding.detailToolbar.navigationIcon = null
            Log.d("DetailFragment", "In showProfileDetails: if")
        } else{
            Log.d("DetailFragment", "In showProfileDetails: else")
            binding.detailToolbar.title = resources.getString(R.string.app_name)
            binding.detailToolbar.navigationIcon = resources.getDrawable(R.drawable.baseline_arrow_back_24)
        }
        Picasso.get().load(result.picture.large).into(binding.detailImage)
        binding.detailName.text = "${result.name.title} ${result.name.first} ${result.name.last}"
        binding.detailPhone.text = "Phone: ${result.phone}"
        binding.detailMobile.text = "Mobile: ${result.cell}"
        binding.detailEmail.text = "Email: ${result.email}"
    }
}
