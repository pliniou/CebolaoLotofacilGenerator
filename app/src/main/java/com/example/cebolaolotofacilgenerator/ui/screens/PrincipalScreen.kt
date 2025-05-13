package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.ui.Screen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun PrincipalScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Observe jogos from the viewModel
    val jogos by viewModel.todosJogos.observeAsState(initial = emptyList())
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gerador Lotofácil Cebolão",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Jogos cadastrados: ${jogos.size}",
            style = MaterialTheme.typography.bodyLarge
        )
        
        // Botões de navegação para as outras telas
        Button(
            onClick = { navController.navigate(Screen.Filtros.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Configurar Filtros")
        }
        
        Button(
            onClick = { navController.navigate(Screen.Gerenciamento.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Gerenciar Jogos")
        }
        
        Button(
            onClick = { navController.navigate(Screen.Conferencia.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Conferir Resultados")
        }
        
        Button(
            onClick = { navController.navigate(Screen.Configuracoes.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Configurações")
        }
    }
}
