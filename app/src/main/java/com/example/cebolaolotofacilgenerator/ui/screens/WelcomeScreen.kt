package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.ui.Screen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import kotlinx.coroutines.delay

/** Tela de boas-vindas do aplicativo */
@Composable
fun WelcomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    val autoNavigateToMenu = false // Desabilita navegação automática para debug
    val firstRunCompleted by viewModel.firstRunCompleted.observeAsState(false)

    // Efeito para navegar automaticamente para o menu após alguns segundos
    // Útil quando a tela de boas-vindas é apenas uma splash screen
    if (autoNavigateToMenu) {
        LaunchedEffect(key1 = true) {
            delay(2000) // 2 segundos
            // Se não for primeira execução, navega para o menu
            if (firstRunCompleted) {
                navController.navigate(Screen.Menu.route) {
                    popUpTo(Screen.Welcome.route) { this.inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                    text = "Bem-vindo ao",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
            )

            Text(
                    text = "Cebolão Lotofácil Generator",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                    text =
                            "Gere seus jogos da Lotofácil com facilidade e aumente suas chances de ganhar!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                    onClick = {
                        navController.navigate(Screen.Menu.route) {
                            popUpTo(Screen.Welcome.route) { this.inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.7f).padding(vertical = 8.dp)
            ) { Text(text = "Começar") }
        }
    }
}
