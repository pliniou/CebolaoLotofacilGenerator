package com.example.cebolaolotofacilgenerator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cebolaolotofacilgenerator.ui.screens.GeradorScreen // Supondo que esta seja a tela principal
import com.example.cebolaolotofacilgenerator.ui.screens.Screen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.ui.screens.FiltrosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.ConferenciaScreen // Nova tela
import com.example.cebolaolotofacilgenerator.ui.screens.GerenciamentoJogosScreen // Já migrada
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen // Placeholder
import androidx.lifecycle.viewmodel.compose.viewModel // Import para viewModel()
import com.example.cebolaolotofacilgenerator.ui.viewmodel.FiltrosViewModel // Import do FiltrosViewModel
// Importar outros ViewModels se necessário, ex: androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Principal.route,
        modifier = modifier
    ) {
        composable(Screen.Principal.route) {
            GeradorScreen(mainViewModel = mainViewModel, navController = navController)
        }
        composable(Screen.Filtros.route) {
            val filtrosViewModel: FiltrosViewModel = viewModel()
            FiltrosScreen(
                mainViewModel = mainViewModel, 
                filtrosViewModel = filtrosViewModel, 
                navController = navController
            )
        }
        composable(Screen.Conferencia.route) {
            ConferenciaScreen(mainViewModel = mainViewModel, navController = navController)
        }
        composable(Screen.GerenciamentoJogos.route) {
            GerenciamentoJogosScreen(mainViewModel = mainViewModel, navController = navController)
        }
        composable(Screen.Configuracoes.route) {
            SettingsScreen(navController = navController) // Placeholder
        }
        // Adicione outras telas da Bottom Navigation ou outras rotas aqui
        // Exemplo:
        // composable("outra_tela_exemplo") {
        //     OutraTelaExemplo(navController)
        // }
    }
} 