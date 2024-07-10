package com.pravin.mycontact.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.pravin.mycontact.ui.theme.MyContactTheme

@Composable
fun ContactItem(
    imageUrl: String,
    name: String
) {
    ContactItemContent(
        imageUrl = imageUrl,
        name = name
    )
}

@Composable
private fun ContactItemContent(
    modifier: Modifier = Modifier,
    imageUrl: String ,
    name: String
) {
    Card(
        modifier = modifier.padding(8.dp)
        ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
            ) {
            Log.d("ContactItemContent", "imageUrl: $imageUrl")
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                modifier = Modifier.size(64.dp)
                    .clip(CircleShape),
                contentDescription = null,
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = name)
            }
        }
    }
}

@Preview
@Composable
private fun ContactItemPreview() {
    MyContactTheme {
        ContactItemContent(
            imageUrl = "",
            name = ""
        )
    }
}