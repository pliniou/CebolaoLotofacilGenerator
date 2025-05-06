package com.exemplo.cebolao.ui

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.utils.Utils
import com.exemplo.cebolao.viewmodel.MainViewModel
import androidx.compose.material.icons.Icons.Filled

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.filled.DateRange

@Composable
fun JogosGeradosScreen(mainViewModel: MainViewModel) {

    val jogos by mainViewModel.jogos.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var numberOfGames by remember { mutableStateOf("10") }

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),

        ) {
            items(jogos) { jogo ->
                JogoItem(jogo = jogo, mainViewModel)
            }
            
            
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Quantos jogos deseja gerar?") },
                text = {
                    OutlinedTextField(
                        value = numberOfGames,
                        onValueChange = { if (it.all { char -> char.isDigit() }) numberOfGames = it },
                        label = { Text("Número de jogos") },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            val numJogos = numberOfGames.toIntOrNull() ?: 10
                            mainViewModel.generateGames(
                                numJogos,
                                mainViewModel.selectedFilters.value
                            ).forEach { jogo ->
                                mainViewModel.insertJogo(jogo)

                            }
                        }
                    ) {
                        Text("Gerar")
                    }
                }
            )
        }
    }
}


@Composable
fun JogoItem(jogo: Jogo, mainViewModel: MainViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Jogo ${jogo.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = Utils.numbersToString(jogo.numbers),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Date Icon", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Data: ${jogo.date}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val newJogo = jogo.copy(favorito = !jogo.favorito)
                    mainViewModel.updateJogo(newJogo)
                }) {
                    Icon(
                        imageVector = if (jogo.favorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (jogo.favorito) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
        }
    }
}

