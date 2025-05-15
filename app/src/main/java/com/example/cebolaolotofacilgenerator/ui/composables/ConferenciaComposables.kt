package com.example.cebolaolotofacilgenerator.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConferenciaViewModel

// Cores para os acertos (podem ser movidas para Theme.kt ou um arquivo de Colors.kt)
val Acertos15Color = Color(0xFF4CAF50) // Verde
val Acertos14Color = Color(0xFF8BC34A) // Verde claro
val Acertos13Color = Color(0xFFCDDC39) // Lima
val Acertos12Color = Color(0xFFFFEB3B) // Amarelo
val Acertos11Color = Color(0xFFFFC107) // Âmbar
val AcertosMenor11Color = Color(0xFFF44336) // Vermelho
val AcertoNumeroColor = Color.Black // Exemplo, pode ser ajustado
val AcertoNumeroBackgroundColor = Color.Yellow // Exemplo para destacar número acertado

@Composable
fun ListaJogosConferidos(
    jogosConferidos: List<ConferenciaViewModel.JogoConferido>,
    resultadoSorteado: Resultado?,
    modifier: Modifier = Modifier
) {
    if (resultadoSorteado == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum resultado selecionado para conferência.")
        }
        return
    }

    if (jogosConferidos.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum jogo conferido para exibir.")
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(jogosConferidos) { jogoConferido ->
            JogoConferidoItemView(
                item = jogoConferido,
                numerosSorteados = resultadoSorteado.numeros
            )
        }
    }
}

@Composable
fun JogoConferidoItemView(
    item: ConferenciaViewModel.JogoConferido,
    numerosSorteados: List<Int>,
    modifier: Modifier = Modifier
) {
    val corFundo = when {
        item.acertos >= 15 -> Acertos15Color
        item.acertos >= 14 -> Acertos14Color
        item.acertos >= 13 -> Acertos13Color
        item.acertos >= 12 -> Acertos12Color
        item.acertos >= 11 -> Acertos11Color
        else -> AcertosMenor11Color
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Jogo ID: ${item.jogo.id ?: "N/A"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer // Ajustar conforme o fundo
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Exibir números do jogo, destacando os acertados
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item.jogo.numeros.sorted().forEach { numero ->
                    val isAcerto = numero in numerosSorteados
                    Text(
                        text = numero.toString().padStart(2, '0'),
                        fontWeight = if (isAcerto) FontWeight.Bold else FontWeight.Normal,
                        color = if (isAcerto) AcertoNumeroColor else MaterialTheme.colorScheme.onPrimaryContainer, // Ajustar
                        fontSize = 16.sp,
                        modifier = if (isAcerto) Modifier.background(AcertoNumeroBackgroundColor).padding(2.dp) else Modifier.padding(2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Acertos: ${item.acertos}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer // Ajustar
            )
            // Adicionar mais informações se necessário (ex: prêmio)
            // val premio = VerificadorJogos.calcularPremio(item.acertos, /* precisa do objeto Resultado completo com valores de prêmio */)
            // Text(text = "Prêmio: R$ ${premio}")
        }
    }
} 