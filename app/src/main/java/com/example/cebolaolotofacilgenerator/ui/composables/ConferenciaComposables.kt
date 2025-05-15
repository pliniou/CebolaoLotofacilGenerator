package com.example.cebolaolotofacilgenerator.ui.composables

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConferenciaViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

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
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (resultadoSorteado == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.nenhum_resultado_selecionado))
        }
        return
    }

    if (jogosConferidos.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.nenhum_jogo_conferido_para_exibir))
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(jogosConferidos, key = { it.jogo.id }) { jogoConferido ->
            var isFavoritoState by remember { mutableStateOf(jogoConferido.jogo.favorito) }
            JogoConferidoItemView(
                item = jogoConferido,
                numerosSorteados = resultadoSorteado.numeros,
                isFavorito = isFavoritoState,
                onFavoritoClick = {
                    val novoEstadoFavorito = !isFavoritoState
                    mainViewModel.marcarComoFavorito(jogoConferido.jogo, novoEstadoFavorito)
                    isFavoritoState = novoEstadoFavorito
                    val msgResId = if (novoEstadoFavorito) R.string.jogo_adicionado_favoritos
                                  else R.string.jogo_removido_favoritos
                    mainViewModel.showSnackbar(mainViewModel.getApplication<Application>().getString(msgResId))
                },
                resultadoCompleto = resultadoSorteado
            )
        }
    }
}

@Composable
fun JogoConferidoItemView(
    item: ConferenciaViewModel.JogoConferido,
    numerosSorteados: List<Int>,
    isFavorito: Boolean,
    onFavoritoClick: () -> Unit,
    resultadoCompleto: Resultado,
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
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.jogo_id_label_prefix) + (item.jogo.id?.toString() ?: stringResource(R.string.na_maiusculo)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                IconButton(onClick = onFavoritoClick) {
                    Icon(
                        imageVector = if (isFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = stringResource(if (isFavorito) R.string.desmarcar_como_favorito else R.string.marcar_como_favorito),
                        tint = if (isFavorito) Color(0xFFFFD700) else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
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
                        color = if (isAcerto) AcertoNumeroColor else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp,
                        modifier = if (isAcerto) Modifier.background(AcertoNumeroBackgroundColor).padding(2.dp) else Modifier.padding(2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.acertos_label_com_valor, item.acertos),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            // val premio = VerificadorJogos.calcularPremio(item.acertos, resultadoCompleto)
            // Text(text = stringResource(R.string.premio_label, premio.toString()), color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
} 