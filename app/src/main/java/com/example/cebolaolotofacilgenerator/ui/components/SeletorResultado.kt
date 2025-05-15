package com.example.cebolaolotofacilgenerator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Componente para selecionar um resultado da lista de resultados disponíveis
 * 
 * @param resultados Lista de resultados disponíveis
 * @param resultadoSelecionado Resultado atualmente selecionado
 * @param onResultadoSelecionado Callback chamado quando um resultado é selecionado
 */
@Composable
fun SeletorResultado(
    resultados: List<Resultado>,
    resultadoSelecionado: Resultado?,
    onResultadoSelecionado: (Resultado) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
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
                .padding(16.dp)
        ) {
            Text(
                text = "Concurso para Conferência",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (resultados.isEmpty()) {
                Text(
                    text = "Nenhum resultado disponível. Adicione um resultado primeiro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = resultadoSelecionado?.let { 
                                "Concurso ${it.concurso} de ${formatarData(it.data)}" 
                            } ?: "Selecione um concurso",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                    ) {
                        resultados.sortedByDescending { it.concurso }.forEach { resultado ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = "Concurso ${resultado.concurso} (${formatarData(resultado.data)})",
                                        fontWeight = if (resultado.concurso == resultadoSelecionado?.concurso) 
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onResultadoSelecionado(resultado)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                if (resultadoSelecionado != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Dezenas sorteadas:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(resultadoSelecionado.dezenas.sorted()) { dezena ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dezena.toString(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatarData(data: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return formato.format(data)
} 