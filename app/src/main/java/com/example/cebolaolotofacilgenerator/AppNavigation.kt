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
// import com.example.cebolaolotofacilgenerator.ui.screens.HomeScreen // Não será mais usada como rota principal
import com.example.cebolaolotofacilgenerator.ui.screens.PrincipalScreen // Nova tela principal
import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen
import com.example.cebolaolotofacilgenerator.ui.screens.FiltrosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.GerenciamentoJogosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.GeradorScreen
import com.example.cebolaolotofacilgenerator.ui.screens.ConferenciaScreen
import com.example.cebolaolotofacilgenerator.ui.screens.InstrucoesScreen
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
                navController = navController,
                mainViewModel = mainViewModel
            )
        }

        composable(
            route = Screen.Gerador.route, // Atualizado para usar a rota que inclui o argumento opcional
            arguments = listOf(navArgument("dezenasFixas") {
                type = NavType.StringType
                nullable = true
                defaultValue = null // Ou "" dependendo de como GeradorScreen trata
            }),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry -> // Adicionado backStackEntry para acessar argumentos
            val dezenasFixas = backStackEntry.arguments?.getString("dezenasFixas")
            GeradorScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                dezenasFixasArg = dezenasFixas // Passa o argumento extraído
            )
        }

        composable(
            route = Screen.Filtros.route, // Rota para a tela de Filtros - já está correta
            arguments = listOf(navArgument("dezenasFixas") { // Adicionando argumento para Filtros também
                type = NavType.StringType
                nullable = true
            }),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry -> // Adicionado backStackEntry para Filtros
            // val dezenasFixasFiltro = backStackEntry.arguments?.getString("dezenasFixas") // Exemplo se FiltrosScreen precisasse
            FiltrosScreen(mainViewModel = mainViewModel, navController = navController)
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
            GerenciamentoJogosScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }

        composable(
            Screen.Conferencia.route, // Rota para a tela de Conferência
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            ConferenciaScreen(
                mainViewModel = mainViewModel
                // conferenciaViewModel é obtido via viewModel() dentro da tela
            )
        }
        
        composable(
            Screen.Instrucoes.route, // Rota para a tela de Instruções
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            InstrucoesScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }
}
