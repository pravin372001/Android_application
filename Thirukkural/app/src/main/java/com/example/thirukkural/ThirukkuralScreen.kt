package com.example.thirukkural

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thirukkural_screen, container, false)

        val factory = ThirukkuralViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ThirukkuralViewModel::class.java]
        val layout = binding.thirukkural
        layout.setOnTouchListener(HideKeyboardTouchListener(requireContext()))
        binding.kuralViewModel = viewModel
        binding.lifecycleOwner = this
        binding.button.setOnClickListener {
            var index = binding.thirukuralNumber.text.toString()
            Log.i("ThirukkuralScreeen", "index = $index")
            if(index.isEmpty()){
                Toast.makeText(activity, "Input is Blank..", Toast.LENGTH_SHORT).show()
            } else {
                val i = index.toInt()
                Log.i("ThirukkuralScreeen", "index = $index")
                if(i in 1..1330){
                    val imageView = binding.imageView
                    ObjectAnimator.ofFloat(imageView, "translationY", 200f, 0f).apply {
                        duration = 1000
                        start()
                    }
                    ObjectAnimator.ofFloat(binding.Kural, "translationY", 200f, 0f).apply {
                        duration = 1000
                        start()
                    }
                    ObjectAnimator.ofFloat(binding.textView, "translationY", 200f, 0f).apply {
                        duration = 1000
                        start()
                    }
                    viewModel.settKural(i)
                } else {
                    Toast.makeText(activity, "Enter between 1 to 1330", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releaseTTS()
    }


}