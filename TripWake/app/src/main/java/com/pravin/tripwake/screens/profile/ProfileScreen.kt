package com.pravin.tripwake.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.pravin.tripwake.ui.theme.TripWakeTheme

@Composable
fun ProfileScreen(
    clearAndNavigate: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    ProfileScreenContent(
        currentUser = viewModel.currentUser,
        onSignOutClick = { viewModel.signOut(clearAndNavigate) }
    )
}


@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    currentUser: FirebaseUser?,
    onSignOutClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            currentUser?.let { user ->
                user.photoUrl?.let { url ->
                    AsyncImage(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                user.displayName?.let {
                    Text(text = "Welcome, $it!")
                    Spacer(modifier = Modifier.height(16.dp))
                }
                user.email?.let {
                    Text(text = "Email: $it")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Button(onClick = { onSignOutClick() }) {
                Text("Sign out")
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenContentPreview() {
    TripWakeTheme {
        ProfileScreenContent(
            currentUser = null,
            onSignOutClick = {}
        )
    }
}