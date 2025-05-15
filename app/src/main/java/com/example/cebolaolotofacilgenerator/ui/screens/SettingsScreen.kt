package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel.TemaAplicativo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, navController: NavController) {
        val temaAtual by viewModel.temaAplicativo.collectAsState()
        var showSobreDialog by remember { mutableStateOf(false) }

        if (showSobreDialog) {
                AlertDialog(
                        onDismissRequest = { showSobreDialog = false },
                        title = { Text("Sobre o Cebolão Lotofácil") },
                        text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                                "Versão: 1.0.0"
                                        ) // Pode ser obtida dinamicamente no futuro
                                        Text("Desenvolvido por: Cebola Studios")
                                        Text(
                                                "Seu assistente inteligente para criar e analisar " +
                                                        "jogos da Lotofácil com base em estatísticas e suas preferências!"
                                        )
                                        // Adicionar mais informações se desejar, como links, etc.
                                }
                        },
                        confirmButton = {
                                TextButton(onClick = { showSobreDialog = false }) { Text("Fechar") }
                        }
                )
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Configurações") },
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
                                                        contentDescription = "Voltar",
                                                )
                                        }
                                }
                        )
                }
        ) { paddingValues ->
                Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Text(
                                "Preferências de Aparência",
                                style = MaterialTheme.typography.titleMedium
                        )
                        ThemeSettingsGroup(
                                temaAtual = temaAtual,
                                onThemeSelected = { viewModel.salvarTemaAplicativo(it) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Outras Preferências", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Informações", style = MaterialTheme.typography.titleMedium)
                        InfoItem(
                                icon = Icons.Filled.Info,
                                title = "Sobre o App",
                                subtitle = "Versão 1.0.0 - Cebola Studios"
                        ) { showSobreDialog = true }
                }
        }
}

@Composable
fun ThemeSettingsGroup(temaAtual: TemaAplicativo, onThemeSelected: (TemaAplicativo) -> Unit) {
        val temas = TemaAplicativo.values()

        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                                "Tema do Aplicativo",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        temas.forEach { tema ->
                                Row(
                                        Modifier.fillMaxWidth()
                                                .clickable { onThemeSelected(tema) }
                                                .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        RadioButton(
                                                selected = (tema == temaAtual),
                                                onClick = { onThemeSelected(tema) }
                                        )
                                        Text(
                                                text =
                                                        when (tema) {
                                                                TemaAplicativo.CLARO -> "Claro"
                                                                TemaAplicativo.ESCURO -> "Escuro"
                                                                TemaAplicativo.SISTEMA ->
                                                                        "Padrão do Sistema"
                                                        },
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.padding(start = 8.dp)
                                        )
                                }
                        }
                }
        }
}

@Composable
fun InfoItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                icon,
                                contentDescription = title,
                                modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                                Text(title, style = MaterialTheme.typography.bodyLarge)
                                Text(subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                }
        }
}
