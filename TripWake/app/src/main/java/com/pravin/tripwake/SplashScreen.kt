package com.pravin.tripwake

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    openAndPopUp: (String, String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center

    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.location_loading))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true
            )

        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition,
            progress = { progress }
        )

        LaunchedEffect(Unit) {
            delay(3000)
//            if(viewModel.isLoggedIn.value) {
                openAndPopUp(Screen.Login.route, Screen.Splash.route)
//            } else {
//                openAndPopUp(Screen.Login.route, Screen.Splash.route)
//            }
        }
    }
}