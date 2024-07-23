package com.pravin.tripwake

import android.content.Context
import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pravin.tripwake.ui.theme.TripWakeTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.libraries.places.api.Places
import com.pravin.tripwake.listener.TrackingListenerHolder
import com.pravin.tripwake.model.service.workmanager.TripWorkManager
import com.pravin.tripwake.screens.profile.ProfileScreen
import com.pravin.tripwake.screens.login.LoginScreen
import com.pravin.tripwake.screens.map.MapScreen
import com.pravin.tripwake.screens.map.MapScreenViewModel
import com.pravin.tripwake.screens.navigation.BottomNavigation
import com.pravin.tripwake.screens.navigation.TopBar
import com.pravin.tripwake.screens.register.RegisterScreen
import com.pravin.tripwake.screens.map.SearchScreen
import com.pravin.tripwake.screens.splash.SplashScreen
import com.pravin.tripwake.util.snackbar.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import com.pravin.tripwake.screens.triplist.TripListScreen
import java.util.concurrent.TimeUnit
import com.pravin.tripwake.R.string as AppText

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
    Register("register"),
    Profile("profile"),
    Map("map"),
    Search("search")
}
@Composable
fun TripWakeApp() {
    val viewModel: MapScreenViewModel = hiltViewModel()
    TrackingListenerHolder.listener = viewModel
    TripWakeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()
            val navController = appState.navController
            var showBar by rememberSaveable { mutableStateOf(false) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            showBar = when (navBackStackEntry?.destination?.route) {
                Screen.Splash.route -> false
                Screen.Login.route -> false
                Screen.Register.route -> false
                Screen.Map.route -> false
                Screen.Search.route -> false
                else -> true
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = appState.snackbarHostState,
                        modifier = Modifier.padding(16.dp)
                    )
                },
                bottomBar = { if(showBar) BottomNavigation(navHostController = navController) },
                topBar = { if (showBar) { TopBar() } }
            ) { innerPaddingModifier ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    modifier = Modifier.padding(innerPaddingModifier),
                ) {
                    tripWakeNavGraph(appState, viewModel)
                }
            }
        }
    }
}


fun NavGraphBuilder.tripWakeNavGraph(appState: TripwakeAppState, viewModel: MapScreenViewModel) {

    composable(Screen.Splash.route) {
        SplashScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(Screen.Main.route) {
        TripListScreen(
            openAndPopUp = { route -> appState.navigate(route) },
            viewModel = viewModel
        )
    }

    composable(Screen.Login.route) {
        LoginScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            clearAndNavigate = { route -> appState.clearAndNavigate(route) }
        )
    }
    composable(Screen.Register.route) {
        RegisterScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }
    composable(Screen.Profile.route) {
        ProfileScreen(
            clearAndNavigate = { route -> appState.clearAndNavigate(route) }
        )
    }
    composable(Screen.Map.route) {
        MapScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            navigateToSearch = { route -> appState.navigate(route) },
            viewModel = viewModel
        )
    }

    composable(Screen.Search.route) {
        SearchScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            navigateToMap = { route -> appState.navigate(route) },
            viewModel = viewModel
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