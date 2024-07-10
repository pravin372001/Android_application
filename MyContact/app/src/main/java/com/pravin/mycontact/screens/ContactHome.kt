package com.pravin.mycontact.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pravin.mycontact.ui.theme.MyContactTheme
import com.pravin.mycontact.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactHome(
    modifier: Modifier = Modifier,
    viewModel: ContactViewModel
    ) {
    val contact = viewModel.contacts.observeAsState()
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(text = "Contact List") }) }
    ) {it ->
        contact.value?.let {
            ContactList(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                contact = it
            )
        } ?: run {
            Text(text = "Loading contacts...", modifier = Modifier.padding(it))
        }
    }
}

@Preview
@Composable
private fun ContactHomePreview() {
    MyContactTheme {
//        ContactHome(
//            viewModel = ContactViewModel()
//        )
    }
}