package com.example.cebolaolotofacilgenerator

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cebolaolotofacilgenerator.ui.screens.BoasVindasScreen // Importar a nova tela
// import com.example.cebolaolotofacilgenerator.ui.screens.ConferenciaScreen // REMOVIDO
import com.example.cebolaolotofacilgenerator.ui.screens.FavoritosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.FiltrosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.GeradorScreen
import com.example.cebolaolotofacilgenerator.ui.screens.GerenciamentoJogosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.InstrucoesScreen
// import com.example.cebolaolotofacilgenerator.ui.screens.PrincipalScreen // Não é mais usada diretamente aqui
// import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen // REMOVIDO
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
    startDestination: String // Recebe a startDestination da MainActivity
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination, // Usar a startDestination definida na MainActivity
        modifier = modifier
    ) {
        composable(
            Screen.BoasVindas.route, // Rota principal agora é BoasVindasScreen
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            BoasVindasScreen(
                navController = navController
            )
        }

        composable(
            route = Screen.Gerador.route, 
            arguments = listOf(navArgument("dezenasFixas") {
                type = NavType.StringType
                nullable = true
                defaultValue = null 
            }),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry -> 
            val dezenasFixas = backStackEntry.arguments?.getString("dezenasFixas")
            GeradorScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                dezenasFixasArg = dezenasFixas 
            )
        }

        composable(
            route = Screen.Filtros.route, 
            arguments = listOf(navArgument("dezenasFixas") { 
                type = NavType.StringType
                nullable = true
            }),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry -> 
            FiltrosScreen(mainViewModel = mainViewModel)
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

        composable(Screen.JogosGerados.route) {
            GerenciamentoJogosScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }

        composable(
            Screen.Instrucoes.route, 
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            InstrucoesScreen(
                navController = navController
            )
        }
    }
}
