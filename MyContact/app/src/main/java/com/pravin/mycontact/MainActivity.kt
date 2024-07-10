package com.pravin.mycontact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.pravin.mycontact.screens.ContactHome
import com.pravin.mycontact.screens.ContactList
import com.pravin.mycontact.ui.theme.MyContactTheme
import com.pravin.mycontact.viewmodel.ContactViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ContactViewModel.ContactViewModelFactory(application))[ContactViewModel::class.java]
        setContent {
            MyContactTheme {
                ContactHome(viewModel = viewModel)
            }
        }
    }
}


