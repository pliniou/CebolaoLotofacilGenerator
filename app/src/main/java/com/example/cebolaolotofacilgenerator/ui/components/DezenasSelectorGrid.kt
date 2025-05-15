package com.example.cebolaolotofacilgenerator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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

/**
 * Um grid genérico para exibir e selecionar dezenas.
 *
 * @param todasDezenas Lista de todas as dezenas a serem exibidas (normalmente 1 a 25).
 * @param dezenasSelecionadas Conjunto de dezenas atualmente selecionadas pelo usuário.
 * @param dezenasDestacadas Conjunto opcional de dezenas para um destaque secundário (ex: sorteadas
 * no último concurso).
 * @param corSelecionada Cor de fundo para dezenas selecionadas.
 * @param corDestacada Cor de fundo para dezenas destacadas (se não selecionadas).
 * @param corNormal Cor de fundo para dezenas normais.
 * @param onDezenaClick Callback quando uma dezena é clicada.
 * @param maxSelecao Opcional, número máximo de dezenas que podem ser selecionadas. Se atingido,
 * novos cliques não selecionam.
 */
@Composable
fun DezenasSelectorGrid(
        todasDezenas: List<Int> = (1..25).toList(),
        dezenasSelecionadas: Set<Int>,
        dezenasDestacadas: Set<Int>? = null, // Para dezenas sorteadas, por exemplo
        corSelecionada: Color = MaterialTheme.colorScheme.primaryContainer,
        corTextoSelecionada: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        corDestacada: Color = MaterialTheme.colorScheme.secondaryContainer,
        corTextoDestacada: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        corBordaSelecionada: Color = MaterialTheme.colorScheme.primary,
        corNormal: Color = MaterialTheme.colorScheme.surfaceVariant,
        corTextoNormal: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        onDezenaClick: (Int) -> Unit,
        maxSelecao: Int? = null,
        colunas: Int = 5
) {
    LazyVerticalGrid(
            columns = GridCells.Fixed(colunas),
            modifier = Modifier.fillMaxWidth().height(240.dp),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(todasDezenas) { dezena ->
            val isSelected = dezena in dezenasSelecionadas
            val isHighlighted = dezenasDestacadas?.contains(dezena) == true

            val canSelectMore =
                    maxSelecao == null || dezenasSelecionadas.size < maxSelecao || isSelected

            val backgroundColor =
                    when {
                        isSelected -> corSelecionada
                        isHighlighted -> corDestacada
                        else -> corNormal
                    }
            val textColor =
                    when {
                        isSelected -> corTextoSelecionada
                        isHighlighted -> corTextoDestacada
                        else -> corTextoNormal
                    }
            val border = if (isSelected) BorderStroke(2.dp, corBordaSelecionada) else null

            Card(
                    modifier =
                            Modifier.aspectRatio(1f).clickable(enabled = canSelectMore) {
                                onDezenaClick(dezena)
                            },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    border = border,
                    elevation =
                            CardDefaults.cardElevation(
                                    defaultElevation = if (canSelectMore) 2.dp else 0.dp
                            )
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                            text = "%02d".format(dezena),
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
