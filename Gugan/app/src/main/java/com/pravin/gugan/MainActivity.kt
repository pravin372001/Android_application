package com.pravin.gugan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.pravin.gugan.repository.ImageRepository
import com.pravin.gugan.screens.App
import com.pravin.gugan.ui.theme.GuganTheme
import com.pravin.gugan.viewmodel.GuganViewModel
import com.pravin.gugan.viewmodel.GuganViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this, GuganViewModelProvider(ImageRepository(context = this)))[GuganViewModel::class.java]
        setContent {
            GuganTheme {
                App(viewModel)
            }
        }
    }
}