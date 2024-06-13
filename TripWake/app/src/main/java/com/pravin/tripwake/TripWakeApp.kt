package com.pravin.tripwake

import android.content.res.Resources
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pravin.tripwake.ui.theme.TripWakeTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pravin.tripwake.screens.login.LoginScreen
import com.pravin.tripwake.util.snackbar.SnackbarManager
import com.pravin.tripwake.util.snackbar.SnackbarMessage
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
    TripwakeAppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

enum class Screen(val route: String) {
    Splash("splash"),
    Main("main"),
    Login("login"),
    Register("register")
}
@Composable
fun TripWakeApp() {
    TripWakeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()

            Column {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = appState.snackbarHostState,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                ) { innerPaddingModifier ->
                    NavHost(
                        navController = appState.navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPaddingModifier)
                    ) {
                        tripWakeNavGraph(appState)
                    }
                }
            }
        }
    }
}


fun NavGraphBuilder.tripWakeNavGraph(appState: TripwakeAppState) {
        composable(Screen.Splash.route) {
            SplashScreen(
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
            )
        }
        composable(Screen.Main.route) { TripListScreen() }
        composable(Screen.Login.route) {
            LoginScreen(
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
            )
        }
        composable(Screen.Register.route) {  }
}

@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    ) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TripListScreen",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    TripWakeTheme {
        TripWakeApp()
    }
}