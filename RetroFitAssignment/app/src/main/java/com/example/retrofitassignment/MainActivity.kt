package com.example.retrofitassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.retrofitassignment.databinding.ActivityMainBinding
import com.example.retrofitassignment.viewmodel.MarsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MarsViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MarsViewModel::class.java]
        binding.button.setOnClickListener {
            binding.response.text = viewModel.getMarsPhotos().toString()
        }
    }

}