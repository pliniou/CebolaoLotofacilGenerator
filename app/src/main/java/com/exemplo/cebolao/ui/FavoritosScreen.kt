package com.exemplo.cebolao.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.ui.formatJogo
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun FavoritosScreen(navController: NavHostController, viewModel: MainViewModel = viewModel()) {
 val favoritos by viewModel.jogosFavoritos.collectAsState()
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
            items(favoritos) { jogo ->
                JogoItem(
                    numeros = jogo.numeros,
                    isFavorite = jogo.isFavorite,
                    onFavoriteClick = {
                        // TODO: Implement favorite click logic
                    }
                )
            }
        }
    }
}

fun formatJogo(numbers: List<Int>): String {
    return numbers.joinToString(", ")
}