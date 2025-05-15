package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* // Explicitamente importar ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp // Adicionado import para dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel

@OptIn(ExperimentalMaterial3Api::class) // Adicionar OptIn aqui
@Composable
fun GeradorScreen(
        navController: NavController,
        dezenasFixasArg: String? // Recebe as dezenas como String da navegação
) {
        val geradorViewModel: GeradorViewModel = viewModel() // Instanciação simplificada

        LaunchedEffect(dezenasFixasArg) {
                val dezenas = dezenasFixasArg?.split(",")?.mapNotNull { it.toIntOrNull() }
                geradorViewModel.inicializarComNumerosFixos(dezenas)
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Gerar Jogos") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary,
                                                navigationIconContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        ),
                                navigationIcon = {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Voltar"
                                                )
                                        }
                                }
                        )
                }
        ) { paddingValues ->
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                        contentAlignment = Alignment.Center
                ) {
                        // Conteúdo da tela do gerador será adicionado aqui
                        Column {
                                Text("Tela de Geração de Jogos - Em Construção")
                                Text("Dezenas fixas recebidas: ${dezenasFixasArg ?: "Nenhuma"}")
                                // TODO: Adicionar UI para quantidade de jogos, números, fixos,
                                // excluídos, filtros,
                                // etc.
                                // TODO: Botão para chamar geradorViewModel.gerarJogos()
                                // TODO: Exibir mensagens e status de operação
                                // TODO: Exibir jogos gerados
                        }
                }
        }
}
