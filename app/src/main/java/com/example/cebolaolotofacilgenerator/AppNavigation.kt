package com.example.cebolaolotofacilgenerator

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cebolaolotofacilgenerator.ui.screens.FavoritosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.HomeScreen
import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

/** Componente de navegação principal do aplicativo */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
        navController: NavHostController,
        mainViewModel: MainViewModel,
        modifier: Modifier = Modifier,
        startDestination: String
) {
    AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
    ) {
        composable(
                Screen.Home.route,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it } },
                popEnterTransition = { slideInHorizontally { -it } },
                popExitTransition = { slideOutHorizontally { it } }
        ) { 
            HomeScreen(mainViewModel = mainViewModel, navController = navController) 
        }
        
        composable(
                Screen.Settings.route,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it } },
                popEnterTransition = { slideInHorizontally { -it } },
                popExitTransition = { slideOutHorizontally { it } }
        ) { 
            SettingsScreen(mainViewModel = mainViewModel, navController = navController) 
        }
        
        composable(
                Screen.Favoritos.route,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it } },
                popEnterTransition = { slideInHorizontally { -it } },
                popExitTransition = { slideOutHorizontally { it } }
        ) { 
            FavoritosScreen(mainViewModel = mainViewModel, navController = navController) 
        }
        
        composable(
                Screen.Resultados.route,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it } },
                popEnterTransition = { slideInHorizontally { -it } },
                popExitTransition = { slideOutHorizontally { it } }
        ) { 
            ResultadosScreen(mainViewModel = mainViewModel, navController = navController) 
        }
    }
}
