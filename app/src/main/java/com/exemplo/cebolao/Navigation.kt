package com.exemplo.cebolao.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.ui.WelcomeScreen
import com.exemplo.cebolao.ui.MenuScreen
import com.exemplo.cebolao.ui.JogosGeradosScreen
import com.exemplo.cebolao.ui.FavoritosScreen
import com.exemplo.cebolao.ui.FiltrosScreen
import com.exemplo.cebolao.ui.SettingsScreen

sealed class Screen(val route: String, val title: String) {
 object Welcome : Screen("welcome", "Welcome")
 object Menu : Screen("menu", "Menu")
 object JogosGerados : Screen("jogos_gerados", "Generated Games")
 object Favoritos : Screen("favoritos", "Favorites")
 object Filtros : Screen("filtros", "Filters")
 object Settings : Screen("settings", "Settings")
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