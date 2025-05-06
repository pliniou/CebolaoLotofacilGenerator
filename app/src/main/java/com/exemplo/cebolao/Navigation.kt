package com.exemplo.cebolao.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.exemplo.cebolao.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Menu : Screen("menu")
    object JogosGerados : Screen("jogos_gerados")
    object Favoritos : Screen("favoritos")
    object Filtros : Screen("filtros")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(navController: NavHostController, mainViewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
 WelcomeScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(Screen.Menu.route) {
 MenuScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(Screen.JogosGerados.route) {
 JogosGeradosScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(Screen.Favoritos.route) {
 FavoritosScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(Screen.Filtros.route) {
 FiltrosScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(Screen.Settings.route) {
 SettingsScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}