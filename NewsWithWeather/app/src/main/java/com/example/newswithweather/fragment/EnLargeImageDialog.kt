package com.example.newswithweather.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.example.newswithweather.R
import com.example.newswithweather.databinding.EnLargeImageDialogBinding
import com.squareup.picasso.Picasso


class EnLargeImageDialog(context: Context, private val imageUrl: String) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = EnLargeImageDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        // Load image into ImageView using Picasso
        Picasso.get().load(imageUrl).into(binding.imageView)

        // Close button click listener
        binding.closeButton.setOnClickListener {
            dismiss() // Dismiss the dialog
        }
    }
}