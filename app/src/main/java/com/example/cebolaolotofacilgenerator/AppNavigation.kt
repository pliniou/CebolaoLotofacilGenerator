package com.example.cebolaolotofacilgenerator

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
// import androidx.navigation.NavType // Não usado no momento
// import androidx.navigation.navArgument // Não usado no momento
import com.example.cebolaolotofacilgenerator.ui.screens.FavoritosScreen
// import com.example.cebolaolotofacilgenerator.ui.screens.HomeScreen // Não será mais usada como rota principal
import com.example.cebolaolotofacilgenerator.ui.screens.PrincipalScreen // Nova tela principal
import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen
import com.example.cebolaolotofacilgenerator.ui.screens.FiltrosScreen
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
            Screen.Principal.route, // Rota principal agora é PrincipalScreen
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            PrincipalScreen(
                mainViewModel = mainViewModel,
                // ViewModels de Gerador e Jogo serão obtidos via `viewModel()` dentro da PrincipalScreen
                onNavigateToFiltros = { navController.navigate(Screen.Filtros.createRoute(null)) }
            )
        }

        composable(
            Screen.Filtros.route, // Rota para a tela de Filtros
            // arguments = listOf(navArgument("dezenasFixas") { type = NavType.StringType; nullable = true }), // Se precisar de argumentos
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            // FiltrosScreen(navController = navController, mainViewModel = mainViewModel) // Será implementada
            // Por enquanto, pode-se colocar um placeholder ou navegar de volta se for acessada diretamente
            // com.example.cebolaolotofacilgenerator.ui.screens.PlaceholderScreen(screenName = "Filtros Screen")
            FiltrosScreen(mainViewModel = mainViewModel) // Navega para a FiltrosScreen implementada
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
        
        // A rota Screen.JogosGerados não está sendo usada na BottomBar ou navegação principal por enquanto.
        // Pode ser uma tela acessada de outro local ou integrada/removida.
        composable(Screen.JogosGerados.route) {
            // com.example.cebolaolotofacilgenerator.ui.screens.PlaceholderScreen(screenName = "Jogos Gerados Screen")
            GerenciamentoJogosScreen(mainViewModel = mainViewModel) // Usar a tela implementada
            // JogosGeradosScreen(mainViewModel = mainViewModel, navController = navController) // Se for usada
        }
    }
}
