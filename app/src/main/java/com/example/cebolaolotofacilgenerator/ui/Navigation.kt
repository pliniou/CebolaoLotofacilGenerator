package com.example.cebolaolotofacilgenerator.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cebolaolotofacilgenerator.ui.screens.PrincipalScreen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String) {
    // Definição das telas principais do app
    data object Principal : Screen("principal", "Gerador Lotofácil")
    data object Filtros : Screen("filtros", "Filtros")
    data object Conferencia : Screen("conferencia", "Conferência")
    data object Gerenciamento : Screen("gerenciamento", "Gerenciamento")
    data object Configuracoes : Screen("configuracoes", "Configurações")
    
    // Para suporte à navegação baseada em fragmentos, podemos usar estas rotas
    data object PrincipalFragment : Screen("principal_fragment", "Principal")
    data object FiltrosFragment : Screen("filtros_fragment", "Filtros")
    data object ConferenciaFragment : Screen("conferencia_fragment", "Conferência")
    data object GerenciamentoFragment : Screen("gerenciamento_fragment", "Gerenciamento")
    data object ConfiguracoesFragment : Screen("configuracoes_fragment", "Configurações")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    // Define a tela inicial como a tela principal
    NavHost(navController = navController, startDestination = Screen.Principal.route) {
        // Define rotas para os composables das telas
        composable(Screen.Principal.route) {
            // Usa o composable da tela principal
            PrincipalScreen(navController = navController, viewModel = viewModel)
        }
        
        // As outras telas serão adicionadas conforme forem implementadas
        composable(Screen.Filtros.route) {
            // Placeholder temporário até que a tela seja implementada
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Tela de Filtros - Em construção")
            }
        }
        
        composable(Screen.Conferencia.route) {
            // Placeholder temporário até que a tela seja implementada
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Tela de Conferência - Em construção")
            }
        }
        
        composable(Screen.Gerenciamento.route) {
            // Placeholder temporário até que a tela seja implementada
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Tela de Gerenciamento - Em construção")
            }
        }
        
        composable(Screen.Configuracoes.route) {
            // Placeholder temporário até que a tela seja implementada
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Tela de Configurações - Em construção")
            }
        }
    }
}
