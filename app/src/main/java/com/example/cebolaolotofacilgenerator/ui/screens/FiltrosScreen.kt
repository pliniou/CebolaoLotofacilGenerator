package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModelFactory
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.AppDataStore

data class FiltroConfigItem(
    val tituloRes: Int,
    val isChecked: Boolean,
    val valorLabelFormatRes: Int,
    val onUpdate: (Boolean, Int, Int) -> Unit,
    val onUpdateSimples: ((Boolean) -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val application = LocalContext.current.applicationContext as Application

    val filtrosViewModel: FiltrosViewModel = mainViewModel.filtrosViewModel

    val configuracaoFiltros by filtrosViewModel.configuracaoFiltros.observeAsState(ConfiguracaoFiltros())
    val mensagemFiltros by filtrosViewModel.mensagem.observeAsState()

    LaunchedEffect(mensagemFiltros) {
        mensagemFiltros?.let {
            if (it.isNotEmpty()) {
                mainViewModel.showSnackbar(it)
                filtrosViewModel.limparMensagem()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Filtros") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FiltrosBasicosCard(configuracaoFiltros, filtrosViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            NumerosEspeciaisCard(configuracaoFiltros, filtrosViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            FiltrosEstatisticosSection(configuracaoFiltros, filtrosViewModel)
            Spacer(modifier = Modifier.height(24.dp))
            AcoesFiltrosButtons(filtrosViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosBasicosCard(config: ConfiguracaoFiltros, viewModel: FiltrosViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.filtros_basicos_titulo), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))

            OutlinedTextField(
                value = config.quantidadeJogos.toString(),
                onValueChange = {
                    val novoValor = it.toIntOrNull() ?: 1
                    viewModel.atualizarFiltro(config.copy(quantidadeJogos = novoValor.coerceIn(1, 100)))
                },
                label = { Text(stringResource(R.string.quantidade_de_jogos_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = config.quantidadeNumerosPorJogo.toString(),
                onValueChange = {
                    val novoValor = it.toIntOrNull() ?: 15
                    if (novoValor in 15..20) {
                        viewModel.atualizarFiltro(config.copy(quantidadeNumerosPorJogo = novoValor))
                    }
                },
                label = { Text(stringResource(R.string.quantidade_de_numeros_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumerosEspeciaisCard(config: ConfiguracaoFiltros, viewModel: FiltrosViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.numeros_especiais_titulo), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))

            OutlinedTextField(
                value = config.numerosFixos.joinToString(","),
                onValueChange = {
                    val numeros = it.split(Regex("[^\\d]+")).filter { s -> s.isNotBlank() }.mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }.distinct().sorted()
                    if (numeros.size <= 10) {
                       viewModel.atualizarFiltro(config.copy(numerosFixos = numeros))
                    }
                 },
                label = { Text(stringResource(R.string.numeros_fixos_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.numeros_fixos_placeholder)) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = config.numerosExcluidos.joinToString(","),
                onValueChange = {
                    val numeros = it.split(Regex("[^\\d]+")).filter { s -> s.isNotBlank() }.mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }.distinct().sorted()
                     if (numeros.size <= 10) {
                        viewModel.atualizarFiltro(config.copy(numerosExcluidos = numeros))
                     }
                },
                label = { Text(stringResource(R.string.numeros_excluidos_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.numeros_excluidos_placeholder)) },
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosEstatisticosSection(
    config: ConfiguracaoFiltros,
    viewModel: FiltrosViewModel
) {
    Text(stringResource(R.string.filtros_estatisticos_titulo), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))

    val filtroItems = listOf(
        FiltroConfigItem(R.string.filtro_pares_impares_titulo, config.filtroParesImpares, R.string.valor_filtro_pares_impares,
            onUpdate = { ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroParesImpares = ativo, minImpares = min, maxImpares = max)) }),
        FiltroConfigItem(R.string.filtro_soma_total_titulo, config.filtroSomaTotal, R.string.valor_filtro_soma_total,
            onUpdate = { ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroSomaTotal = ativo, minSoma = min, maxSoma = max)) }),
        FiltroConfigItem(R.string.filtro_primos_titulo, config.filtroPrimos, R.string.valor_filtro_primos,
            onUpdate = { ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroPrimos = ativo, minPrimos = min, maxPrimos = max)) }),
        FiltroConfigItem(R.string.filtro_fibonacci_titulo, config.filtroFibonacci, R.string.valor_filtro_fibonacci,
            onUpdate = { ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroFibonacci = ativo, minFibonacci = min, maxFibonacci = max)) }),
        FiltroConfigItem(R.string.filtro_miolo_moldura_titulo, config.filtroMioloMoldura, R.string.valor_filtro_miolo_moldura,
            onUpdate = { ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroMioloMoldura = ativo, minMiolo = min, maxMiolo = max)) }),
        FiltroConfigItem(R.string.filtro_multiplos_de_tres_titulo, config.filtroMultiplosDeTres, R.string.valor_filtro_multiplos_tres,
            onUpdate = { ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroMultiplosDeTres = ativo, minMultiplos = min, maxMultiplos = max)) }),
        FiltroConfigItem(R.string.filtro_repeticao_anterior_titulo, config.filtroRepeticaoConcursoAnterior, R.string.valor_filtro_repeticao_anterior,
            onUpdate = { ativo, min, max -> 
                viewModel.atualizarFiltro(config.copy(filtroRepeticaoConcursoAnterior = ativo, minRepeticaoConcursoAnterior = min, maxRepeticaoConcursoAnterior = max))
            })
    )

    val ranges = listOf(
        config.minImpares to config.maxImpares,
        config.minSoma to config.maxSoma,
        config.minPrimos to config.maxPrimos,
        config.minFibonacci to config.maxFibonacci,
        config.minMiolo to config.maxMiolo,
        config.minMultiplos to config.maxMultiplos,
        config.minRepeticaoConcursoAnterior to config.maxRepeticaoConcursoAnterior
    )

    val sliderBounds = listOf(
        0f to 15f,
        120f to 270f,
        0f to 9f,
        0f to 7f,
        0f to 9f,
        0f to 8f,
        0f to 15f
    )

    filtroItems.forEachIndexed { index, filtroItem ->
        val (currentMin, currentMax) = ranges[index]
        val (sliderFrom, sliderTo) = sliderBounds[index]

        if (filtroItem.tituloRes == R.string.filtro_repeticao_anterior_titulo) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val novoEstado = !filtroItem.isChecked
                            filtroItem.onUpdate(novoEstado, currentMin, currentMax)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = filtroItem.isChecked,
                        onCheckedChange = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(filtroItem.tituloRes), style = MaterialTheme.typography.titleMedium)
                }

                OutlinedTextField(
                    value = config.dezenasConcursoAnterior.joinToString(","),
                    onValueChange = {
                        val numeros = it.split(Regex("[^\\d]+"))
                            .filter { s -> s.isNotBlank() }
                            .mapNotNull { s -> s.toIntOrNull()?.coerceIn(1, 25) }
                            .distinct().sorted()
                        if (numeros.size <= 15) {
                            viewModel.atualizarFiltro(config.copy(dezenasConcursoAnterior = numeros))
                        }
                    },
                    label = { Text(stringResource(R.string.label_dezenas_concurso_anterior)) },
                    placeholder = { Text(stringResource(R.string.placeholder_dezenas_concurso_anterior)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    singleLine = true,
                    enabled = filtroItem.isChecked
                )
            }
        }

        FiltroSliderComInputs(
            mostrarControlesCheckboxTitulo = (filtroItem.tituloRes != R.string.filtro_repeticao_anterior_titulo),
            titulo = stringResource(filtroItem.tituloRes),
            isChecked = filtroItem.isChecked,
            range = currentMin.toFloat()..currentMax.toFloat(),
            onRangeChange = { novoRange ->
                filtroItem.onUpdate(filtroItem.isChecked, novoRange.start.toInt(), novoRange.endInclusive.toInt())
            },
            sliderValueRange = sliderFrom..sliderTo,
            valueLabelFormat = stringResource(filtroItem.valorLabelFormatRes),
            onCheckedChange = { novoEstado ->
                filtroItem.onUpdate(novoEstado, currentMin, currentMax)
            },
            enabled = filtroItem.isChecked
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroSliderComInputs(
    mostrarControlesCheckboxTitulo: Boolean,
    titulo: String,
    isChecked: Boolean,
    range: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    sliderValueRange: ClosedFloatingPointRange<Float>,
    valueLabelFormat: String,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (mostrarControlesCheckboxTitulo) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCheckedChange(!isChecked) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(titulo, style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isChecked) {
                    valueLabelFormat
                        .replace("%1\$d", range.start.toInt().toString())
                        .replace("%2\$d", range.endInclusive.toInt().toString())
                } else {
                    stringResource(id = R.string.na_aplicado)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
        RangeSlider(
            value = range,
            onValueChange = onRangeChange,
            valueRange = sliderValueRange,
            steps = ((sliderValueRange.endInclusive - sliderValueRange.start) / 1).toInt().coerceAtLeast(0) - 1,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AcoesFiltrosButtons(viewModel: FiltrosViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = { viewModel.salvarConfiguracaoFiltros() }) {
            Text("Salvar Filtros")
        }
        Button(onClick = { viewModel.resetarConfiguracaoFiltros() }) {
            Text(stringResource(R.string.resetar_filtros_botao))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiltrosScreenPreview() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val jogoRepository = JogoRepository(AppDatabase.getDatabase(application).jogoDao())
    val appDataStore = AppDataStore(application)
    val mainViewModel = MainViewModel(application, jogoRepository, appDataStore)

    FiltrosScreen(mainViewModel = mainViewModel, navController = NavController(context))
}

@Preview(showBackground = true, name = "Filtro Estatístico Item")
@Composable
fun PreviewFiltroEstatisticoItem() {
    FiltroSliderComInputs(
        mostrarControlesCheckboxTitulo = true,
        titulo = "Teste Pares/Ímpares",
        isChecked = true,
        range = 5f..10f,
        onRangeChange = { _ -> },
        sliderValueRange = 0f..15f,
        valueLabelFormat = "Ímpares: %1\$d-%2\$d",
        onCheckedChange = {},
        enabled = true
    )
} 