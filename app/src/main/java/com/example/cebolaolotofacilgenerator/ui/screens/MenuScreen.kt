package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.ui.Screen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

/** Tela de menu principal do aplicativo */
@Composable
fun MenuScreen(navController: NavHostController, viewModel: MainViewModel) {
        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Menu Principal") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color(0xFF1976D2),
                                                titleContentColor = Color.White
                                        )
                        )
                }
        ) { paddingValues ->
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) {
                        Column(
                                modifier = Modifier.fillMaxWidth(0.8f).padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Text(
                                        text = "Cebolão Lotofácil Generator",
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                        onClick = {
                                                navController.navigate(Screen.Principal.route)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Gerador de Jogos") }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                        onClick = { navController.navigate(Screen.Filtros.route) },
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Filtros") }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                        onClick = {
                                                navController.navigate(Screen.JogosGerados.route)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Jogos Gerados") }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                        onClick = {
                                                navController.navigate(Screen.Favoritos.route)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Favoritos") }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                        onClick = {
                                                navController.navigate(Screen.SettingsNav.route)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Configurações") }
                        }
                }
        }
}
