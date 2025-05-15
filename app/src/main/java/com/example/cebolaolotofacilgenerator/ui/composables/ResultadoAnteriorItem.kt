package com.example.cebolaolotofacilgenerator.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultadoAnteriorItem(
    resultado: Resultado,
    onResultadoClick: (Resultado) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault()) // Formato mais curto para economizar espaço

    Card(
        modifier = modifier
            .width(150.dp) // Largura similar ao XML
            .padding(4.dp) // Margem menor que no XML (8dp) para compensar o padding interno do card
            .clickable { onResultadoClick(resultado) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium // Equivalente a cardCornerRadius="8dp"
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp) // Padding interno
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.concurso) + ":", // "Concurso:"
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = resultado.id.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp)) // Ajuste fino no espaçamento

            Text(
                text = resultado.dataSorteio?.let { dateFormat.format(it) }
                    ?: stringResource(id = R.string.data_nao_disponivel),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Exibe os primeiros números do resultado para economizar espaço
            val primeirosNumeros = resultado.numeros.take(5).joinToString(" - ")
            Text(
                text = stringResource(R.string.resultado_numeros_parciais, primeirosNumeros),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultadoAnteriorItemPreview() {
    val sampleResultado = Resultado(
        id = 2500,
        dataSorteio = Date(),
        numeros = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
        acumulado = false,
        valorAcumulado = 0.0,
        ganhadores15Acertos = 1,
        cidadeUF15Acertos = "São Paulo/SP",
        rateio15Acertos = 1500000.0,
        ganhadores14Acertos = 100,
        rateio14Acertos = 1500.0,
        ganhadores13Acertos = 1000,
        rateio13Acertos = 30.0,
        ganhadores12Acertos = 10000,
        rateio12Acertos = 12.0,
        ganhadores11Acertos = 100000,
        rateio11Acertos = 6.0,
        arrecadacaoTotal = 30000000.0,
        estimativaPremio = 1700000.0,
        acumuladoValorEspecial = 0.0,
        dataProxConcurso = Date(),
        valorEstimadoProxConcurso = 2000000.0,
        concursoEspecial = false,
        nomeConcursoEspecial = ""
    )
    ResultadoAnteriorItem(
        resultado = sampleResultado,
        onResultadoClick = {}
    )
} 