package com.example.cebolaolotofacilgenerator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cebolaolotofacilgenerator.ui.screens.FavoritosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.HomeScreen
import com.example.cebolaolotofacilgenerator.ui.screens.OnboardingScreen
import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

/** Rotas de navegação do aplicativo */
sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Settings : Routes("settings")
    object Favoritos : Routes("favoritos")
    object Resultados : Routes("resultados")
    object Onboarding : Routes("onboarding")
}

/** Componente de navegação principal do aplicativo */
@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(
            navController = navController,
            startDestination =
                    if (viewModel.isFirstRun()) Routes.Onboarding.route else Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.Settings.route) {
            SettingsScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.Favoritos.route) {
            FavoritosScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.Resultados.route) {
            ResultadosScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Onboarding.route) { inclusive = true }
                        }
                    }
            )
        }
    }
}
