package com.example.mediagallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mediagallery.ui.theme.MediaGalleryTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun PhotoGallery(photoList: List<String>) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
    ) {
        items(photoList) { photo ->
            PhotoCard(photo)
        }
    }
}

@Composable
fun PhotoCard(photoPath: String) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp), // Use CardDefaults for elevation
        colors = CardDefaults.cardColors(Color.White), // Use CardDefaults for colors
    ) {
        GlideImage(
            imageModel = { photoPath }, // Correct usage for imageModel as a lambda
            modifier =
                Modifier.size(200.dp)
                    .fillMaxWidth().padding(4.dp).align(Alignment.CenterHorizontally),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MediaGalleryTheme {
        PhotoGallery(photoList = listOf("https://via.placeholder.com/200x300"))
    }
}
