package com.pravin.gugan.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pravin.gugan.database.ImageEntity
import com.pravin.gugan.viewmodel.GuganViewModel
import com.pravin.gugan.R.string as AppText

@Composable
fun App(
    viewModel: GuganViewModel
) {
    val navController = rememberNavController()
    val images = viewModel.images.observeAsState()
    Scaffold(
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) { it ->
        NavHost(
            navController = navController,
            startDestination = Screen.ImageEditor.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.ImageMaster.route) {
                ImageMaster(
                    imageSave = {viewModel.insertImage(it)},
                    imageEntity = images.value ?: ImageEntity(0, "")
                    )
            }
            composable(Screen.ImageEditor.route) {
                ImageEditor(
                    imageEntity = images.value ?: ImageEntity(0, "")
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val curNav = rememberSaveable {
        mutableStateOf(Screen.ImageEditor.route)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        val items = listOf(
            Triple(Screen.ImageMaster.route , Icons.Filled.Image , AppText.image_master),
            Triple(Screen.ImageEditor.route , Icons.Filled.EditNote , AppText.image_editor)
        )

        items.forEach { (navItem, icon, labelRes) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(labelRes))
                },
                selected = curNav.value == navItem,
                onClick = {
                    if (curNav.value != navItem) {
                        navController.popBackStack()
                        curNav.value = navItem
                        navController.navigate(navItem)
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object ImageMaster : Screen("image_master")
    data object ImageEditor : Screen("image_editor")
}