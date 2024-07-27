package com.example.znews.fragments

import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.znews.MainActivity
import com.example.znews.database.NewsOneModel
import com.example.znews.databinding.FragmentAddNewsBinding
import com.example.znews.viewmodel.NewsViewModel
import com.example.znews.viewmodel.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.util.Date
import java.util.Random

class AddNewsFragment : Fragment() {

    private lateinit var binding: FragmentAddNewsBinding
    private lateinit var mainActivity: MainActivity
    private val viewModel: NewsViewModel by activityViewModels {
        NewsViewModelFactory(requireContext())
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            Log.d(TAG, "Selected image URI: $it")
            // Persist permissions for the URI
            context?.contentResolver?.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            displaySelectedImage(it)
        } ?: run {
            Log.d(TAG, "No image selected")
        }
    }

    private fun displaySelectedImage(uri: Uri) {
        binding.newsImage.imageTintList = null
        Picasso.get().load(uri).into(binding.newsImage)
        binding.newsImage.tag = uri.toString()
        Log.d(TAG, "Image URI set to ImageView: ${binding.newsImage.tag.toString()}")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewsBinding.inflate(inflater, container, false)

        binding.newsImage.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.addNewsToolBar.setNavigationOnClickListener {
            mainActivity.getBinding().slidingPaneLayout.visibility = View.VISIBLE
            mainActivity.getBinding().addNewsButton.visibility = View.GONE
        }

        val categories = resources.getStringArray(com.example.znews.R.array.categories)
        Log.d(TAG, "Categories: ${categories.toList()}")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, categories)
        binding.menuAutoCompleteTextView.setAdapter(adapter)
        Log.d(TAG, "Adapter set to AutoCompleteTextView")
        binding.menuAutoCompleteTextView.threshold = 1

        binding.menuAutoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.menuAutoCompleteTextView.showDropDown()
                Log.d(TAG, "AutoCompleteTextView gained focus, showing dropdown")
            }
        }

        // Handle item click events
        binding.menuAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            Log.d(TAG, "Selected category: $selectedCategory")
        }

        binding.addNewsButton.setOnClickListener {
            if(
                binding.newsTitle.text.toString().isEmpty() ||
                binding.newsContent.text.toString().isEmpty() ||
                binding.newsDescription.text.toString().isEmpty() ||
                binding.newsImage.tag == null ||
                binding.menuAutoCompleteTextView.text.toString().isEmpty()
            ) {
                mainActivity.getString(com.example.znews.R.string.fill_all_fields)
                    .let { it1 -> Snackbar.make(binding.root, it1, Snackbar.LENGTH_SHORT).show() }
            } else {
                addNewsAndNavigate()
            }
        }

        return binding.root
    }

    companion object {
        private const val TAG = "AddNewsFragment"
    }

    private fun addNewsAndNavigate() {
        val newsOne = NewsOneModel(
            articleId = Date().toString() + Random().nextInt(1000000).toString(),
            title = binding.newsTitle.text.toString(),
            category = binding.menuAutoCompleteTextView.text.toString(),
            content = binding.newsContent.text.toString(),
            country = "India",
            creator = viewModel.user.value?.displayName.toString(),
            description = binding.newsDescription.text.toString(),
            image_url = binding.newsImage.tag.toString() ?: "",
            pubDate = Date().toString(),
            link = ""
        )
        viewModel.setCustomNewsOne(newsOne)
        mainActivity.getBinding().slidingPaneLayout.visibility = View.VISIBLE
        mainActivity.getBinding().addNewsButton.visibility = View.GONE
        binding.newsTitle.text?.clear()
        binding.newsContent.text?.clear()
        binding.newsDescription.text?.clear()
        binding.newsImage.setImageResource(com.example.znews.R.drawable.rounded_add_24)
        binding.menuAutoCompleteTextView.setText("")

    }
}
