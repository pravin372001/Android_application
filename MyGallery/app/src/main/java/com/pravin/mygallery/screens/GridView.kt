package com.pravin.mygallery.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pravin.mygallery.MainViewModel

@Composable
fun ImageGrid(images: List<Uri>, onImageClick: (Uri) -> Unit, lazyGridState: LazyGridState) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState
    ) {
        items(images) { imageUri ->
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .clickable { onImageClick(imageUri) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Usage
@Composable
fun MyGalleryApp(galleryViewModel: MainViewModel) {
    val images by galleryViewModel.images.observeAsState(emptyList())
    val selectedImageUri = galleryViewModel.selectedImage
    val lazyListState = rememberLazyGridState()

    Scaffold (
        modifier = Modifier.fillMaxSize()
    ) { it ->
        if (selectedImageUri == null) {
            ImageGrid(images = images, onImageClick = { uri -> galleryViewModel.setSelectedImage(uri) }, lazyGridState = lazyListState)
        } else {
            ImagePreviewScreen(
                imageUri = selectedImageUri,
                onBack = {
                    galleryViewModel.setSelectedImage(null)
                }
            )
        }
    }
}




