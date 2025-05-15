package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.Screen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Lotofácil Generator 1.0") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Mensagem de Boas-vindas
                        Text(
                                text = "Bem-vindo ao Cebolão Lotofácil!",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                                text = "Seu assistente para gerar jogos inteligentes.",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Cartões de funcionalidades principais
                        HomeCard(
                                title = "Gerar Novos Jogos",
                                description = "Crie novos jogos com base nas suas preferências",
                                onClick = {
                                        // Navega para GeradorScreen sem dezenas fixas
                                        navController.navigate(Screen.Gerador.createRoute(null))
                                },
                                icon = Icons.Default.Add
                        )

                        HomeCard(
                                title = "Meus Favoritos",
                                description = "Acesse os jogos que você salvou como favoritos",
                                onClick = { navController.navigate(Screen.Favoritos.route) },
                                icon = Icons.Default.Favorite
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Informações sobre o App e Como Usar (integrado)
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme
                                                                .surfaceVariant // Cor diferenciada
                                        )
                        ) {
                                Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment =
                                                Alignment.Start // Alinhar à esquerda para melhor
                                        // leitura
                                        ) {
                                        Text(
                                                text = "Sobre o Aplicativo",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Text(
                                                text =
                                                        "Este app ajuda você a gerar jogos da Lotofácil, " +
                                                                "gerenciar seus jogos favoritos e conferir os resultados.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        Text(
                                                text = "Como Usar",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Text(
                                                text =
                                                        "• Use os botões acima para: Gerar novos jogos, acessar Favoritos ou ver Resultados.\n" +
                                                                "• Explore as Configurações (ícone no topo) para personalizar sua experiência.",
                                                style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp)) // Espaço no final da coluna
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(
        title: String,
        description: String,
        onClick: () -> Unit,
        icon: androidx.compose.ui.graphics.vector.ImageVector
) {
        Card(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                                Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyMedium
                                )
                        }
                }
        }
}
