package com.example.thirukkural

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.thirukkural.databinding.FragmentThirukkuralScreenBinding


class ThirukkuralScreen : Fragment() {

    private lateinit var viewModel : ThirukkuralViewModel

    private lateinit var binding : FragmentThirukkuralScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thirukkural_screen, container, false)

        val factory = ThirukkuralViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ThirukkuralViewModel::class.java]

        binding.kuralViewModel = viewModel
        binding.lifecycleOwner = this
        binding.button.setOnClickListener {
            var index = binding.kuralNumber.text.toString().toInt()
            Log.i("ThirukkuralScreeen", "index = $index")
            if(index in 1..1330){
                viewModel.settKural(index)
            } else {
                Toast.makeText(activity, "Enter between 1 to 1330", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releaseTTS()
    }


}