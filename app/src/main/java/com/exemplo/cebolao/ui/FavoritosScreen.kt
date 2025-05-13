// Arquivo legado, esvaziado para evitar conflitos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exemplo.cebolao.viewmodel.MainViewModel

@Composable
fun FavoritosScreen(viewModel: MainViewModel) {
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
                JogoItem(jogo = jogo, viewModel = viewModel)
            }
        }
    }
}
