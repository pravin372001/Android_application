package com.pravin.mygallery.screens

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(imageUri: Uri, onBack: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isActionBarVisible by remember { mutableStateOf(true) }
    var isInfoVisible by remember { mutableStateOf(false) }
    var doubleTap by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as ComponentActivity

    // Control status bar and navigation bar visibility
    fun setSystemBarsVisibility(visible: Boolean) {
        activity.window?.let { window ->
            if (visible) {
                WindowCompat.getInsetsController(window, window.decorView).show(android.view.WindowInsets.Type.systemBars())
            } else {
                WindowCompat.getInsetsController(window, window.decorView).hide(android.view.WindowInsets.Type.systemBars())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isActionBarVisible = !isActionBarVisible
                        setSystemBarsVisibility(isActionBarVisible)
                    },
                    onDoubleTap = {
                        doubleTap = !doubleTap
                        if(doubleTap || (scale > 1f && scale < 10f) ) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = 2f
                            offsetX = 0f
                            offsetY = 0f
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 4f) // Set the zoom limit
                    scale = newScale
                    offsetX = (offsetX + pan.x).coerceIn(-1000f, 1000f) // Adjust pan limits
                    offsetY = (offsetY + pan.y).coerceIn(-1000f, 1000f)
                }
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .fillMaxSize()
        )

        // Animate the appearance of the info panel
        val infoOffset by animateDpAsState(targetValue = if (isInfoVisible) 0.dp else 300.dp)

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = infoOffset)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Image Details",
                fontSize = 18.sp,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )
            Text(
                text = "Here is some information about the image.",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (isActionBarVisible) {
            TopAppBar(
                title = { Text("Image Preview") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            isInfoVisible = !isInfoVisible
                        }
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Show Info")
                    }
                }
            )
        }
    }
}
