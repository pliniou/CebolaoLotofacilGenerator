package com.exemplo.cebolao.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.utils.Utils
import com.exemplo.cebolao.viewmodel.MainViewModel

fun LazyListScope.jogoItens(jogos: List<Jogo>, mainViewModel: MainViewModel) {
    games.forEach {jogo ->
        JogoItem(jogo = jogo, mainViewModel)

    }
}


@Composable
fun JogoItem(jogo: Jogo, mainViewModel: MainViewModel) {
    Card(
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
            Row (verticalAlignment = Alignment.CenterVertically){
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

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            jogoItens(jogos, mainViewModel)
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

fun LazyListScope.jogoItens(jogos: List<Jogo>, mainViewModel: MainViewModel) {
    items(jogos) { jogo ->
        JogoItem(jogo = jogo, mainViewModel)
    }
}

@Composable
fun JogoItem(jogo: Jogo, mainViewModel: MainViewModel) {
    Card(
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
            Row (verticalAlignment = Alignment.CenterVertically){
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
                        imageVector = if (jogo.favorito) Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (jogo.favorito) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
        }
    }
}
