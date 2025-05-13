// Arquivo legado, esvaziado para evitar conflitos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.viewmodel.MainViewModel

@Composable fun JogosGeradosScreen(viewModel: MainViewModel) {
    val currentJogos by viewModel.jogosGerados.collectAsState()
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Jogos Gerados", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.size(16.dp))

        if (currentJogos.isEmpty()) {
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
                items(currentJogos) { jogo ->
                    JogoItem(jogo = jogo, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun JogoItem(jogo: Jogo, viewModel: MainViewModel) {
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
                    text = "Jogo: ${jogo.numeros.joinToString(", ")}",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { viewModel.updateJogo(jogo.copy(favorito = !jogo.favorito)) }) {
                 Icon(
                    imageVector = if (jogo.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (jogo.favorito) "Remover dos Favoritos" else "Adicionar aos Favoritos",
                    tint = if (jogo.favorito) Color.Yellow else Color.Gray
                )
            }
        }
    }
}
