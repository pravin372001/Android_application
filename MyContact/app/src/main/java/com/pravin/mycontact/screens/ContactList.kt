package com.pravin.mycontact.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pravin.mycontact.remote.model.Contact
import com.pravin.mycontact.ui.theme.MyContactTheme

@Composable
fun ContactList(
    modifier: Modifier = Modifier,
    contact: Contact
) {
    ContactListContent(contact = contact)
}

@Composable
fun ContactListContent(
    contact: Contact
) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
            .fillMaxSize()
    ){
        items(contact.results.size) {
            ContactItem(
                imageUrl = contact.results[it].picture.medium,
                name = contact.results[it].name.first + " " + contact.results[it].name.last
            )
        }
    }
}

@Preview
@Composable
fun ContactListPreview() {
    MyContactTheme {
//        ContactListContent(
//
//        )
    }
}