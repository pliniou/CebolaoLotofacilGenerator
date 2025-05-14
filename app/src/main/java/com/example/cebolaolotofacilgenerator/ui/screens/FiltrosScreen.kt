package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

/** Tela de filtros para configuração de jogos */
@Composable
fun FiltrosScreen(navController: NavHostController, viewModel: MainViewModel) {
        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Filtros") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color(0xFF1976D2),
                                                titleContentColor = Color.White
                                        ),
                                navigationIcon = {
                                        // TODO: Adicionar botão de navegação para voltar quando
                                        // implementarmos
                                        // a navegação
                                }
                        )
                }
        ) { paddingValues ->
                // Conteúdo da tela - por enquanto um placeholder até implementação completa
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) { Text(text = "Tela de Filtros - Em implementação") }
        }
}
