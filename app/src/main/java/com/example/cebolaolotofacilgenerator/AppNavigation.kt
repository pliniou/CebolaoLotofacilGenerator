package com.example.cebolaolotofacilgenerator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cebolaolotofacilgenerator.ui.screens.FavoritosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.HomeScreen
import com.example.cebolaolotofacilgenerator.ui.screens.OnboardingScreen
import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

/** Componente de navegação principal do aplicativo */
@Composable
fun AppNavigation(
        navController: NavHostController,
        viewModel: MainViewModel,
        modifier: Modifier = Modifier,
        startDestination: String
) {
    NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Favoritos.route) {
            FavoritosScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Resultados.route) {
            ResultadosScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
            )
        }
    }
}
