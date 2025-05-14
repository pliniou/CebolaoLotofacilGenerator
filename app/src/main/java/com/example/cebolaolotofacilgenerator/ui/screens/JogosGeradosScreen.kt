package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.model.common.OperacaoStatus
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel

/** Tela que exibe os jogos gerados pelo app */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JogosGeradosScreen(navController: NavHostController, geradorViewModel: GeradorViewModel) {
    val jogosGerados by geradorViewModel.jogosGerados.observeAsState(emptyList())
    val operacaoStatus by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Jogos Gerados") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color(0xFF1976D2),
                                        titleContentColor = Color.White
                                ),
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Voltar",
                                        tint = Color.White
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        when (operacaoStatus) {
            OperacaoStatus.CARREGANDO -> {
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            else -> {
                if (jogosGerados.isEmpty()) {
                    Box(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentAlignment = Alignment.Center
                    ) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                    text = "Nenhum jogo gerado ainda",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                            )
                            Button(
                                    onClick = { navController.navigate("principal") },
                                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(0.7f)
                            ) { Text(text = "Gerar Jogos") }
                        }
                    }
                } else {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentPadding = PaddingValues(16.dp)
                    ) {
                        items(jogosGerados) { jogo ->
                            JogoItem(jogo = jogo)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JogoItem(jogo: Jogo) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = "Jogo ${jogo.id.toString()}", style = MaterialTheme.typography.titleMedium)
        Text(
                text = jogo.numeros.joinToString(" - "),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
        )
        if (jogo.favorito) {
            Text(
                    text = "⭐ Favorito",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
