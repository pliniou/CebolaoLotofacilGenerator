package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.ui.composables.ListaJogosConferidos
import com.example.cebolaolotofacilgenerator.ui.composables.ResultadoAnteriorItem
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConferenciaViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConferenciaScreen(
    mainViewModel: MainViewModel,
    conferenciaViewModel: ConferenciaViewModel = viewModel()
) {
    val todosResultados by conferenciaViewModel.todosResultados.collectAsState()
    val resultadoAtual by conferenciaViewModel.resultadoAtual.collectAsState()
    val jogosConferidos by conferenciaViewModel.jogosConferidos.collectAsState()
    val statusConferencia by conferenciaViewModel.statusConferencia.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        conferenciaViewModel.carregarUltimoResultado()
        conferenciaViewModel.carregarTodosResultados()
    }

    Scaffold(
        topBar = {
            Text(
                text = stringResource(id = R.string.titulo_tela_conferencia),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultadosAnterioresSection(todosResultados, conferenciaViewModel)

            Spacer(modifier = Modifier.height(16.dp))

            ResultadoAtualSection(resultadoAtual, dateFormatter, statusConferencia, conferenciaViewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                ListaJogosConferidos(
                    jogosConferidos = jogosConferidos,
                    resultadoSorteado = resultadoAtual,
                    mainViewModel = mainViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Exibir snackbar em caso de erro na conferência
    LaunchedEffect(statusConferencia) {
        if (statusConferencia == ConferenciaViewModel.StatusConferencia.ERRO) {
            mainViewModel.showSnackbar(context.getString(R.string.erro_conferir_jogos))
        }
    }
}

@Composable
private fun ResultadosAnterioresSection(
    todosResultados: List<Resultado>,
    viewModel: ConferenciaViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.resultados_anteriores),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (todosResultados.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.nenhum_resultado_anterior),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(0.dp), // Os itens já tem padding
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    items(todosResultados) { resultado ->
                        ResultadoAnteriorItem(
                            resultado = resultado,
                            onResultadoClick = { viewModel.carregarResultado(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultadoAtualSection(
    resultadoAtual: Resultado?,
    dateFormatter: SimpleDateFormat,
    statusConferencia: ConferenciaViewModel.StatusConferencia,
    viewModel: ConferenciaViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.resultado_atual),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (resultadoAtual != null) {
                Text(
                    text = stringResource(id = R.string.concurso_numero_formatado, resultadoAtual.id),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = resultadoAtual.dataSorteio?.let { dateFormatter.format(it) }
                        ?: stringResource(id = R.string.data_nao_disponivel),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resultadoAtual.numeros.joinToString(" - "),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = stringResource(id = R.string.concurso_nao_selecionado),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.conferirJogos() },
                enabled = resultadoAtual != null && statusConferencia != ConferenciaViewModel.StatusConferencia.CONFERINDO,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (statusConferencia == ConferenciaViewModel.StatusConferencia.CONFERINDO) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(text = stringResource(id = R.string.conferir_jogos))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ConferenciaScreen - Sem Resultado")
@Composable
fun ConferenciaScreenSemResultadoPreview() {
    CebolaoLotofacilGeneratorTheme {
        val mockMainViewModel: MainViewModel = viewModel()
        val mockConferenciaViewModel: ConferenciaViewModel = viewModel()
        // Mock ViewModel para não ter resultado
        LaunchedEffect(Unit) {
            mockConferenciaViewModel.clearResultadoAtualForPreview()
        }

        ConferenciaScreen(
            mainViewModel = mockMainViewModel,
            conferenciaViewModel = mockConferenciaViewModel
        )
    }
}

@Preview(showBackground = true, name = "ConferenciaScreen - Com Resultado")
@Composable
fun ConferenciaScreenComResultadoPreview() {
    CebolaoLotofacilGeneratorTheme {
        val mockMainViewModel: MainViewModel = viewModel()
        val mockConferenciaViewModel: ConferenciaViewModel = viewModel()

        // Mock ViewModel para ter um resultado
        val sampleResultado = Resultado(
            id = 2500L,
            concurso = 2500L,
            dataSorteio = Date(),
            dezenas = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            ganhadores15 = 1,
            premiacao15Acertos = 1500000.0,
            ganhadores14 = 100,
            premiacao14Acertos = 1500.0,
            ganhadores13 = 1000,
            premiacao13Acertos = 30.0,
            ganhadores12 = 10000,
            premiacao12Acertos = 12.0,
            ganhadores11 = 100000,
            premiacao11Acertos = 6.0
        )
        LaunchedEffect(Unit) {
            mockConferenciaViewModel.setResultadoAtualForPreview(sampleResultado)
            mockConferenciaViewModel.setTodosResultadosForPreview(listOf(sampleResultado, sampleResultado.copy(id = 2501)))
        }

        ConferenciaScreen(
            mainViewModel = mockMainViewModel,
            conferenciaViewModel = mockConferenciaViewModel
        )
    }
}

// Adicionar ao ConferenciaViewModel para os previews
/*
fun clearResultadoAtualForPreview() {
    _resultadoAtual.value = null
}

fun setResultadoAtualForPreview(resultado: Resultado) {
    _resultadoAtual.value = resultado
}

fun setTodosResultadosForPreview(resultados: List<Resultado>) {
    _todosResultados.value = resultados
}
*/ 