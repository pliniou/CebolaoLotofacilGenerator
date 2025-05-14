package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

// Remover NavController se não for usado diretamente pela OnboardingScreen
// import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel, onComplete: () -> Unit) {
    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Bem-vindo ao Cebolão") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            }
    ) { paddingValues ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Título de boas-vindas
            Text(
                    text = "Bem-vindo ao Gerador de Jogos da Lotofácil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
            )

            // Cartão de informação principal
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
            ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                            text = "Sobre o Aplicativo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text =
                                    "Este aplicativo foi desenvolvido para ajudar você a gerar jogos da Lotofácil, " +
                                            "permitindo criar e gerenciar seus jogos favoritos e acompanhar os resultados.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                    )
                }
            }

            // Cartão de instruções
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                            )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = "Como Usar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text =
                                    "• Gerador de Jogos: Crie jogos randomizados com base em suas preferências.\n" +
                                            "• Favoritos: Salve seus jogos para consultar mais tarde.\n" +
                                            "• Resultados: Acompanhe os últimos resultados da Lotofácil.",
                            style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botão para continuar para a tela principal
            Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                            )
            ) { Text(text = "Começar", modifier = Modifier.padding(8.dp), fontSize = 16.sp) }
        }
    }
}
