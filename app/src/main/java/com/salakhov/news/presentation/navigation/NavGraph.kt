package com.salakhov.news.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.salakhov.news.presentation.screen.settings.SettingsScreen
import com.salakhov.news.presentation.screen.subscriptions.SubscriptionsScreen

@Composable
fun NavGraph() {
    val navController= rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(Screen.MainScreen.route) {
            SubscriptionsScreen(
                onSettingNavigate = {
                    navController.navigate(Screen.SettingsScreen.route)
                }
            )
        }
        composable(Screen.SettingsScreen.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object MainScreen : Screen("main")

    data object SettingsScreen : Screen("settings")
}

