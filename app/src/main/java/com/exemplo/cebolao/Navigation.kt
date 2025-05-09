package com.exemplo.cebolao.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.exemplo.cebolao.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String) {
 data object Welcome : Screen("welcome", "Welcome")
 data object Menu : Screen("menu", "Menu")
 data object JogosGerados : Screen("jogos_gerados", "Generated Games")
 data object Favoritos : Screen("favoritos", "Favorites")
 data object Filtros : Screen("filtros", "Filters")
 data object Settings : Screen("settings", "Settings")
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
 WelcomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Menu.route) {
 MenuScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.JogosGerados.route) {
 JogosGeradosScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Favoritos.route) {
 FavoritosScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Filtros.route) {
 FiltrosScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
 SettingsScreen(navController = navController, viewModel = viewModel)
        }
    }
}