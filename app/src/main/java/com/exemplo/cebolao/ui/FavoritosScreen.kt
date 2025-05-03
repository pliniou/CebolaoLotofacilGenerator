package com.exemplo.cebolao.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.utils.numbersToString
import com.exemplo.cebolao.utils.stringToNumbers
import com.exemplo.cebolao.viewmodel.MainViewModel

@Composable
fun FavoritosScreen(navController: NavHostController, viewModel: MainViewModel = viewModel()) {
    var jogos by remember { mutableStateOf<List<Jogo>>(emptyList()) }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadFavoritos()
    }

    jogos = viewModel.favoritos.collectAsState().value
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Jogos Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            favoritesItems(jogos, viewModel)
        }
    }
}

fun LazyListScope.favoritesItems(jogos: List<Jogo>, viewModel: MainViewModel) {
    items(jogos) { jogo ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Números: ${numbersToString(jogo.numbers)}")
                    IconButton(onClick = {
                        viewModel.updateJogo(jogo.copy(favorito = !jogo.favorito))
                    }) {
                        Icon(
                            imageVector = if (jogo.favorito) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = if (jogo.favorito) "Remover dos Favoritos" else "Adicionar aos Favoritos"
                        )
                        }
                    }
                Text(text = "Data: ${jogo.date}")
            }
        }
    }
}