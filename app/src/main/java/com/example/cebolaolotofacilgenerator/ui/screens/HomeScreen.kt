package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
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
                                title = { Text("Cebolão Lotofácil") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        ),
                                actions = {
                                        IconButton(
                                                onClick = {
                                                        navController.navigate(
                                                                Screen.Settings.route
                                                        )
                                                }
                                        ) {
                                                Icon(
                                                        Icons.Default.Settings,
                                                        contentDescription = "Configurações",
                                                        tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                        }
                                }
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
                        // Cabeçalho com título do app
                        Text(
                                text = "Gerador de Jogos da Lotofácil",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 16.dp)
                        )

                        // Cartões de funcionalidades principais
                        HomeCard(
                                title = "Gerar Novos Jogos",
                                description = "Crie novos jogos com base nas suas preferências",
                                onClick = { /* TODO: Implementar geração de jogos */},
                                icon = Icons.Default.Add
                        )

                        HomeCard(
                                title = "Meus Favoritos",
                                description = "Acesse os jogos que você salvou como favoritos",
                                onClick = { navController.navigate(Screen.Favoritos.route) },
                                icon = Icons.Default.Favorite
                        )

                        HomeCard(
                                title = "Resultados Anteriores",
                                description =
                                        "Confira os números sorteados em concursos anteriores",
                                onClick = { navController.navigate(Screen.Resultados.route) },
                                icon = Icons.Default.List
                        )

                        // Estatísticas ou informações adicionais
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                        )
                        ) {
                                Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Text(
                                                text = "Dicas para Jogar",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text =
                                                        "A Lotofácil é uma loteria onde você marca de 15 a 20 números dentre os 25 disponíveis. " +
                                                                "Para ganhar, é preciso acertar 11, 12, 13, 14 ou 15 números.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
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
