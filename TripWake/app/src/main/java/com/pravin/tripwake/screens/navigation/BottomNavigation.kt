package com.pravin.tripwake.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Portrait
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pravin.tripwake.Screen

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    val currentNavigation = rememberSaveable {
        mutableStateOf(Screen.Main.route)
    }

    NavigationBar(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        val navigationItems = listOf(
            Triple(Screen.Main.route, "Trips", Icons.Filled.DirectionsBus),
            Triple(Screen.Profile.route, "Profile", Icons.Filled.Portrait),
        )

        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentNavigation.value == item.first,
                onClick = {
                    currentNavigation.value = item.first
                    navHostController.popBackStack()
                    navHostController.navigate(item.first) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.third,
                        contentDescription = item.second
                    )
                },
                label = {
                    Text(text = item.second)
                }
            )
        }
    }
}