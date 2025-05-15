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
fun ConferenciaScreen(\n    mainViewModel: MainViewModel,\n    conferenciaViewModel: ConferenciaViewModel = viewModel()\n) {\n    val todosResultados by conferenciaViewModel.todosResultados.collectAsState()
    val resultadoAtual by conferenciaViewModel.resultadoAtual.collectAsState()
    val jogosConferidos by conferenciaViewModel.jogosConferidos.collectAsState()
    val statusConferencia by conferenciaViewModel.statusConferencia.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(Unit) {\n        conferenciaViewModel.carregarUltimoResultado()
        conferenciaViewModel.carregarTodosResultados()
    }

    Scaffold(\n        topBar = {\n            Text(\n                text = stringResource(id = R.string.titulo_tela_conferencia),\n                style = MaterialTheme.typography.headlineSmall,\n                textAlign = TextAlign.Center,\n                modifier = Modifier\n                    .fillMaxWidth()\n                    .padding(16.dp)\n            )\n        }\n    ) {\ paddingValues ->
        Column(\n            modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n                .padding(horizontal = 16.dp),\n            horizontalAlignment = Alignment.CenterHorizontally\n        ) {\n            ResultadosAnterioresSection(todosResultados, conferenciaViewModel)

            Spacer(modifier = Modifier.height(16.dp))

            ResultadoAtualSection(resultadoAtual, dateFormatter, statusConferencia, conferenciaViewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {\n                ListaJogosConferidos(\n                    jogosConferidos = jogosConferidos,\n                    resultadoSorteado = resultadoAtual,\n                    mainViewModel = mainViewModel,\n                    modifier = Modifier.fillMaxSize()\n                )\n            }\n        }\n    }

    // Exibir snackbar em caso de erro na conferência
    LaunchedEffect(statusConferencia) {\n        if (statusConferencia == ConferenciaViewModel.StatusConferencia.ERRO) {\n            mainViewModel.showSnackbar(context.getString(R.string.erro_conferir_jogos))\n        }\n    }
}

@Composable
private fun ResultadosAnterioresSection(\n    todosResultados: List<Resultado>,\n    viewModel: ConferenciaViewModel\n) {\n    Card(\n        modifier = Modifier.fillMaxWidth(),\n        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)\n    ) {\n        Column(modifier = Modifier.padding(16.dp)) {\n            Text(\n                text = stringResource(id = R.string.resultados_anteriores),\n                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)\n            )\n            Spacer(modifier = Modifier.height(8.dp))\n            if (todosResultados.isEmpty()) {\n                Text(\n                    text = stringResource(id = R.string.nenhum_resultado_anterior),\n                    style = MaterialTheme.typography.bodyMedium,\n                    textAlign = TextAlign.Center,\n                    modifier = Modifier.fillMaxWidth()\n                )\n            } else {\n                LazyRow(\n                    horizontalArrangement = Arrangement.spacedBy(0.dp), // Os itens já tem padding\n                    contentPadding = PaddingValues(horizontal = 0.dp)\n                ) {\n                    items(todosResultados) { resultado ->\n                        ResultadoAnteriorItem(\n                            resultado = resultado,\n                            onResultadoClick = { viewModel.carregarResultado(it) }\n                        )\n                    }\n                }\n            }\n        }\n    }\n}

@Composable
private fun ResultadoAtualSection(\n    resultadoAtual: Resultado?,\n    dateFormatter: SimpleDateFormat,\n    statusConferencia: ConferenciaViewModel.StatusConferencia,\n    viewModel: ConferenciaViewModel\n) {\n    Card(\n        modifier = Modifier.fillMaxWidth(),\n        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)\n    ) {\n        Column(\n            modifier = Modifier.padding(16.dp),\n            horizontalAlignment = Alignment.Start\n        ) {\n            Text(\n                text = stringResource(id = R.string.resultado_atual),\n                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)\n            )\n            Spacer(modifier = Modifier.height(8.dp))\n

            if (resultadoAtual != null) {\n                Text(\n                    text = stringResource(id = R.string.concurso_numero_formatado, resultadoAtual.id),\n                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)\n                )\n                Spacer(modifier = Modifier.height(4.dp))\n                Text(\n                    text = resultadoAtual.dataSorteio?.let { dateFormatter.format(it) }\n                        ?: stringResource(id = R.string.data_nao_disponivel),\n                    style = MaterialTheme.typography.bodyMedium\n                )\n                Spacer(modifier = Modifier.height(8.dp))\n                Text(\n                    text = resultadoAtual.numeros.joinToString(" - "),\n                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),\n                    textAlign = TextAlign.Center,\n                    modifier = Modifier.fillMaxWidth()\n                )\n            } else {\n                Text(\n                    text = stringResource(id = R.string.concurso_nao_selecionado),\n                    style = MaterialTheme.typography.bodyMedium,\n                    textAlign = TextAlign.Center,\n                    modifier = Modifier.fillMaxWidth()\n                )\n            }\n

            Spacer(modifier = Modifier.height(16.dp))

            Button(\n                onClick = { viewModel.conferirJogos() },\n                enabled = resultadoAtual != null && statusConferencia != ConferenciaViewModel.StatusConferencia.CONFERINDO,\n                modifier = Modifier.fillMaxWidth()\n            ) {\n                if (statusConferencia == ConferenciaViewModel.StatusConferencia.CONFERINDO) {\n                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)\n                } else {\n                    Text(text = stringResource(id = R.string.conferir_jogos))\n                }\n            }\n        }\n    }\n}

// Função remember para SimpleDateFormat, pode ser útil se usada em múltiplos locais
@Composable
fun remember(calculation: () -> SimpleDateFormat): SimpleDateFormat {\n    return androidx.compose.runtime.remember(calculation)\n}

@Preview(showBackground = true, name = "ConferenciaScreen - Sem Resultado")
@Composable
fun ConferenciaScreenSemResultadoPreview() {\n    CebolaoLotofacilGeneratorTheme {\n        val mockMainViewModel: MainViewModel = viewModel()
        val mockConferenciaViewModel: ConferenciaViewModel = viewModel()
        // Mock ViewModel para não ter resultado
        LaunchedEffect(Unit) {\n            mockConferenciaViewModel.clearResultadoAtualForPreview()
        }\n

        ConferenciaScreen(\n            mainViewModel = mockMainViewModel,\n            conferenciaViewModel = mockConferenciaViewModel\n        )\n    }\n}

@Preview(showBackground = true, name = "ConferenciaScreen - Com Resultado")
@Composable
fun ConferenciaScreenComResultadoPreview() {\n    CebolaoLotofacilGeneratorTheme {\n        val mockMainViewModel: MainViewModel = viewModel()
        val mockConferenciaViewModel: ConferenciaViewModel = viewModel()

        // Mock ViewModel para ter um resultado
        val sampleResultado = Resultado(\n            id = 2500,\n            dataSorteio = Date(),\n            numeros = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),\n            acumulado = false,\n            valorAcumulado = 0.0,\n            ganhadores15Acertos = 1,\n            cidadeUF15Acertos = "São Paulo/SP",\n            rateio15Acertos = 1500000.0,\n            ganhadores14Acertos = 100,\n            rateio14Acertos = 1500.0,\n            ganhadores13Acertos = 1000,\n            rateio13Acertos = 30.0,\n            ganhadores12Acertos = 10000,\n            rateio12Acertos = 12.0,\n            ganhadores11Acertos = 100000,\n            rateio11Acertos = 6.0,\n            arrecadacaoTotal = 30000000.0,\n            estimativaPremio = 1700000.0,\n            acumuladoValorEspecial = 0.0,\n            dataProxConcurso = Date(),\n            valorEstimadoProxConcurso = 2000000.0,\n            concursoEspecial = false,\n            nomeConcursoEspecial = ""\n        )\n        LaunchedEffect(Unit) {\n            mockConferenciaViewModel.setResultadoAtualForPreview(sampleResultado)
            mockConferenciaViewModel.setTodosResultadosForPreview(listOf(sampleResultado, sampleResultado.copy(id = 2501)))\n        }\n

        ConferenciaScreen(\n            mainViewModel = mockMainViewModel,\n            conferenciaViewModel = mockConferenciaViewModel\n        )\n    }\n}

// Adicionar ao ConferenciaViewModel para os previews
/*
fun clearResultadoAtualForPreview() {\n    _resultadoAtual.value = null
}

fun setResultadoAtualForPreview(resultado: Resultado) {\n    _resultadoAtual.value = resultado
}

fun setTodosResultadosForPreview(resultados: List<Resultado>) {\n    _todosResultados.value = resultados
}
*/ 