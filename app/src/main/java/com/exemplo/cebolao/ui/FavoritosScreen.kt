package com.exemplo.cebolao.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.utils.LotofacilUtils
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.viewmodel.MainViewModelFactory

@Composable
fun FavoritosScreen(navController: NavHostController, viewModel: MainViewModel = viewModel()) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadFavoritos()
    }

    val favoritos by viewModel.favoritos.collectAsState(initial = emptyList())
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
            favoritesItems(favoritos, viewModel)
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
                    Text(text = "Números: ${LotofacilUtils.formatJogo(jogo.numbers)}")
                    IconButton(onClick = {
                        val jogoAtualizado = jogo.copy(favorito = !jogo.favorito)
                        viewModel.updateJogo(jogoAtualizado)

                    }) {
                        Icon(
                            imageVector = if (jogo.favorito) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (jogo.favorito) "Remover dos Favoritos" else "Adicionar aos Favoritos"
                        )
                    }
                    }
                Text(text = "Data: ${jogo.date}")
            }
        }
    }
}