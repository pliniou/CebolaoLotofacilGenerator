package com.example.cebolaolotofacilgenerator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.Resultado

/**
 * Componente para conferir jogos com um resultado específico
 * 
 * @param jogos Lista de jogos a serem conferidos
 * @param resultado Resultado com o qual conferir os jogos
 * @param onConferirClick Callback chamado quando o botão de conferir é clicado
 * @param onConferenciaCompleta Callback chamado quando a conferência é concluída, retornando os jogos conferidos
 * @param isLoading Indica se a conferência está em andamento
 */
@Composable
fun ConferenciaResultados(
    jogos: List<Jogo>,
    resultado: Resultado?,
    onConferirClick: () -> Unit,
    onConferenciaCompleta: (List<Jogo>) -> Unit,
    isLoading: Boolean = false
) {
    var mostrarDetalhes by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Conferência de Jogos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = resultado?.let { "Concurso: ${it.concurso}" } ?: "Nenhum resultado selecionado",
                style = MaterialTheme.typography.bodyMedium,
                color = if (resultado != null) MaterialTheme.colorScheme.onSurface 
                        else MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Conferindo jogos...",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Button(
                    onClick = onConferirClick,
                    enabled = resultado != null && jogos.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = "Conferir ${jogos.size} Jogos",
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = { mostrarDetalhes = !mostrarDetalhes },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    enabled = jogos.any { it.acertos != null }
                ) {
                    Text(
                        text = if (mostrarDetalhes) "Ocultar Detalhes" else "Mostrar Detalhes",
                        fontSize = 14.sp
                    )
                }
            }
            
            AnimatedVisibility(
                visible = mostrarDetalhes && jogos.any { it.acertos != null },
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ResumoConferencia(jogos.filter { it.acertos != null })
            }
        }
    }
}

/**
 * Componente que mostra um resumo da conferência de jogos
 */
@Composable
fun ResumoConferencia(jogosConferidos: List<Jogo>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Divider(thickness = 1.dp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Agrupando jogos por quantidade de acertos
        val agrupadosPorAcertos = jogosConferidos.groupBy { it.acertos ?: 0 }
        val totalJogos = jogosConferidos.size
        
        // Cabeçalho
        Text(
            text = "Resumo da Conferência",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Barra de acertos agrupada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cores para diferentes faixas de acertos
            val cores = mapOf(
                15 to Color(0xFF43A047), // Verde para 15 acertos
                14 to Color(0xFF7CB342), // Verde claro para 14 acertos
                13 to Color(0xFF9CCC65), // Verde mais claro para 13 acertos
                12 to Color(0xFFFFEB3B), // Amarelo para 12 acertos
                11 to Color(0xFFFFC107)  // Âmbar para 11 acertos
            )
            
            // Ordenar de maior para menor número de acertos
            val acertosOrdenados = agrupadosPorAcertos.keys.sortedDescending()
            
            for (acertos in acertosOrdenados) {
                val quantidade = agrupadosPorAcertos[acertos]?.size ?: 0
                val porcentagem = quantidade.toFloat() / totalJogos
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(porcentagem)
                        .background(cores[acertos] ?: Color.Gray)
                )
            }
            
            // Separadores entre as cores
            acertosOrdenados.forEach { acertos ->
                val quantidade = agrupadosPorAcertos[acertos]?.size ?: 0
                val porcentagemAcumulada = jogosConferidos
                    .count { (it.acertos ?: 0) >= acertos }
                    .toFloat() / totalJogos
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth(porcentagemAcumulada)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Legenda das cores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (acertos in (11..15).reversed()) {
                val quantidade = agrupadosPorAcertos[acertos]?.size ?: 0
                val cor = when (acertos) {
                    15 -> Color(0xFF43A047)
                    14 -> Color(0xFF7CB342)
                    13 -> Color(0xFF9CCC65)
                    12 -> Color(0xFFFFEB3B)
                    11 -> Color(0xFFFFC107)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                
                if (quantidade > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(cor)
                        )
                        Text(
                            text = "$acertos",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "$quantidade",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        
        // Lista detalhada de acertos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            for (acertos in (11..15).reversed()) {
                val jogosComAcertos = agrupadosPorAcertos[acertos] ?: emptyList()
                
                if (jogosComAcertos.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val cor = when (acertos) {
                            15 -> Color(0xFF43A047)
                            14 -> Color(0xFF7CB342)
                            13 -> Color(0xFF9CCC65)
                            12 -> Color(0xFFFFEB3B)
                            11 -> Color(0xFFFFC107)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        // Indicador de acertos
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(cor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$acertos",
                                color = if (acertos >= 14) Color.White else Color.Black,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = "$acertos acertos:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                        )
                        
                        Text(
                            text = "${jogosComAcertos.size} ${if (jogosComAcertos.size == 1) "jogo" else "jogos"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        // Barra de progresso para visualizar a proporção
                        val progresso = jogosComAcertos.size.toFloat() / totalJogos
                        val animatedProgress by animateFloatAsState(
                            targetValue = progresso,
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                            label = "progress"
                        )
                        
                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = cor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        
                        Text(
                            text = "${(progresso * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color)
        )
    }
} 