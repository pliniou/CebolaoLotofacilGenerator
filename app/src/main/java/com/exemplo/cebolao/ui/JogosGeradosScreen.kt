package com.exemplo.cebolao.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.utils.formatJogo
import com.exemplo.cebolao.utils.Utils

@Composable
fun JogosGeradosScreen(viewModel: MainViewModel) {
 val jogosGerados by viewModel.jogosGerados.collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Jogos Gerados", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.size(16.dp))

        if (jogosGerados.isEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Nenhum jogo encontrado",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nenhum jogo gerado.", color = Color.Red)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(jogosGerados) { jogo ->
                    JogoItem(jogo = jogo, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun JogoItem(jogo: Jogo, viewModel: MainViewModel) {
 val isFavorito by remember { mutableStateOf(jogo.isFavorito) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Jogo: ${formatJogo(jogo.numbers)}",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { viewModel.updateJogo(jogo.copy(isFavorito = !isFavorito)) }) {
                Icon(
                    imageVector = if (isFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorito) "Remover dos Favoritos" else "Adicionar aos Favoritos",
                    tint = if (isFavorito) Color.Yellow else Color.Gray
                )
            }
        }
    }
}
