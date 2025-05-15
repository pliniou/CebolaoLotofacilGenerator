package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.AppDatabase
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModelFactory
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosScreen(
    mainViewModel: MainViewModel,
    navController: NavController // Mantido para consistência, pode ser usado no futuro
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // Obter ResultadoRepository para a factory do FiltrosViewModel
    val resultadoDao = AppDatabase.getDatabase(application).resultadoDao()
    val resultadoRepository = ResultadoRepository(resultadoDao)

    val filtrosViewModel: FiltrosViewModel = viewModel(
        factory = FiltrosViewModelFactory(application, resultadoRepository)
    )

    val configuracaoFiltros by filtrosViewModel.configuracaoFiltros.observeAsState(ConfiguracaoFiltros())
    val mensagemFiltros by filtrosViewModel.mensagem.observeAsState()
    val temUltimoResultado by filtrosViewModel.temUltimoResultadoSalvo.collectAsState()

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
                title = { Text(stringResource(R.string.titulo_tela_filtros)) } // Usar string mais descritiva
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
            FiltrosEstatisticosSection(configuracaoFiltros, filtrosViewModel, temUltimoResultado)
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
                    viewModel.atualizarFiltro(config.copy(quantidadeJogos = novoValor.coerceIn(1, 100))) // Coerce para evitar valores absurdos
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
                    if (novoValor in 15..20) { // Validar faixa permitida para Lotofácil
                        viewModel.atualizarFiltro(config.copy(quantidadeNumerosPorJogo = novoValor))
                    }
                },
                label = { Text(stringResource(R.string.quantidade_de_numeros_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                // enabled = false // A Lotofácil padrão é 15, mas pode ser configurável para futuras modalidades.
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
                    if (numeros.size <= 10) { // Limite de exemplo para números fixos
                       viewModel.atualizarFiltro(config.copy(numerosFixos = numeros))
                    }
                 },
                label = { Text(stringResource(R.string.numeros_fixos_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), // Aceita vírgulas
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.numeros_fixos_placeholder)) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = config.numerosExcluidos.joinToString(","),
                onValueChange = {
                    val numeros = it.split(Regex("[^\\d]+")).filter { s -> s.isNotBlank() }.mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }.distinct().sorted()
                     if (numeros.size <= 10) { // Limite de exemplo para números excluídos
                        viewModel.atualizarFiltro(config.copy(numerosExcluidos = numeros))
                     }
                },
                label = { Text(stringResource(R.string.numeros_excluidos_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), // Aceita vírgulas
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
    viewModel: FiltrosViewModel, 
    temUltimoResultado: Boolean
) {
    Text(stringResource(R.string.filtros_estatisticos_titulo), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))

    val filtroItems = listOf(
        Triple(R.string.filtro_pares_impares_titulo, config.filtroParesImpares, R.string.valor_filtro_pares_impares) {
                ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroParesImpares = ativo, minImpares = min, maxImpares = max)) },
        Triple(R.string.filtro_soma_total_titulo, config.filtroSomaTotal, R.string.valor_filtro_soma_total) {
                ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroSomaTotal = ativo, minSoma = min, maxSoma = max)) },
        Triple(R.string.filtro_primos_titulo, config.filtroPrimos, R.string.valor_filtro_primos) {
                ativo, min, max -> viewModel.setFiltrarPrimos(ativo, min, max) },
        Triple(R.string.filtro_fibonacci_titulo, config.filtroFibonacci, R.string.valor_filtro_fibonacci) {
                ativo, min, max -> viewModel.setFiltrarFibonacci(ativo, min, max) },
        Triple(R.string.filtro_miolo_moldura_titulo, config.filtroMioloMoldura, R.string.valor_filtro_miolo_moldura) {
                ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroMioloMoldura = ativo, minMiolo = min, maxMiolo = max)) },
        Triple(R.string.filtro_multiplos_de_tres_titulo, config.filtroMultiplosDeTres, R.string.valor_filtro_multiplos_de_tres) {
                ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroMultiplosDeTres = ativo, minMultiplos = min, maxMultiplos = max)) },
        Triple(R.string.filtro_repeticao_anterior_titulo, config.filtroRepeticaoConcursoAnterior, R.string.valor_filtro_repeticao_anterior) {
            ativo, min, max -> viewModel.atualizarFiltro(config.copy(filtroRepeticaoConcursoAnterior = ativo, minRepeticaoConcursoAnterior = min, maxRepeticaoConcursoAnterior = max))
        }
    )

    val ranges = listOf(
        config.minImpares to config.maxImpares,  // Pares/Ímpares (slider de ímpares)
        config.minSoma to config.maxSoma,          // Soma Total
        config.minPrimos to config.maxPrimos,      // Primos
        config.minFibonacci to config.maxFibonacci, // Fibonacci
        config.minMiolo to config.maxMiolo,        // Miolo/Moldura (slider de miolo)
        config.minMultiplos to config.maxMultiplos, // Múltiplos de 3
        config.minRepeticaoConcursoAnterior to config.maxRepeticaoConcursoAnterior
    )

    val sliderBounds = listOf(
        0f to 15f,    // Pares/Ímpares (Ímpares)
        120f to 270f, // Soma Total (valores típicos, podem ser ajustados)
        0f to 9f,     // Primos (Lotofácil tem 9 primos de 1 a 25)
        0f to 7f,     // Fibonacci (Lotofácil tem 7 de Fibonacci de 1 a 25)
        0f to 9f,     // Miolo (Lotofácil tem 9 no miolo)
        0f to 8f,     // Múltiplos de 3 (Lotofácil tem 8 múltiplos de 3)
        0f to 15f     // Repetição do Anterior
    )

    filtroItems.forEachIndexed { index, (tituloRes, isChecked, valorLabelFormatRes, onUpdate) ->
        val (currentMin, currentMax) = ranges[index]
        val (sliderFrom, sliderTo) = sliderBounds[index]

        // Seção específica para o filtro de repetição
        if (tituloRes == R.string.filtro_repeticao_anterior_titulo) {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = config.dezenasConcursoAnterior.joinToString(","),
                    onValueChange = {
                        val numeros = it.split(Regex("[^\\d]+")).filter { s -> s.isNotBlank() }.mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }.distinct().sorted()
                        if (numeros.size <= 15) { // Permitir até 15 dezenas
                            viewModel.atualizarFiltro(config.copy(dezenasConcursoAnterior = numeros))
                        }
                    },
                    label = { Text(stringResource(R.string.label_dezenas_concurso_anterior)) },
                    placeholder = { Text(stringResource(R.string.placeholder_dezenas_concurso_anterior)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    singleLine = true,
                    enabled = isChecked // Habilitar/desabilitar o text field com o switch
                )
                Button(
                    onClick = { viewModel.carregarDezenasDoUltimoResultadoSalvo() },
                    enabled = isChecked && temUltimoResultado, // Habilitado se o filtro estiver ativo E houver resultado salvo
                    modifier = Modifier.align(Alignment.End).padding(bottom = 8.dp)
                ) {
                    Text(stringResource(R.string.carregar_dezenas_salvas_botao)) // Adicionar esta string
                }
            }
        }

        FiltroEstatisticoItem(
            titulo = stringResource(tituloRes),
            isChecked = isChecked,
            onCheckedChange = { ativo -> onUpdate(ativo, currentMin, currentMax) },
            valorMin = currentMin,
            valorMax = currentMax,
            onRangeChange = { min, max -> onUpdate(isChecked, min.toInt(), max.toInt()) },
            rangeSliderFrom = sliderFrom,
            rangeSliderTo = sliderTo,
            stepSize = 1,
            valorLabelFormat = stringResource(valorLabelFormatRes)
        )
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroEstatisticoItem(
    titulo: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    valorMin: Int,
    valorMax: Int,
    onRangeChange: (Float, Float) -> Unit,
    rangeSliderFrom: Float,
    rangeSliderTo: Float,
    stepSize: Int,
    valorLabelFormat: String // Ex: "Ímpares: %1$d-%2$d"
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
        Text(
            text = if (isChecked) String.format(Locale.getDefault(), valorLabelFormat, valorMin, valorMax)
                     else stringResource(id = R.string.na_aplicado),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        RangeSlider(
            value = valorMin.toFloat()..valorMax.toFloat(),
            onValueChange = { newRange -> onRangeChange(newRange.start, newRange.endInclusive) },
            valueRange = rangeSliderFrom..rangeSliderTo,
            steps = ((rangeSliderTo - rangeSliderFrom) / stepSize).toInt().coerceAtLeast(0) -1 , // steps deve ser não negativo
            enabled = isChecked,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AcoesFiltrosButtons(viewModel: FiltrosViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { viewModel.aplicarFiltros() }) {
            Text(stringResource(R.string.aplicar_filtros_botao))
        }
        Button(onClick = { viewModel.salvarFiltros() }) {
            Text(stringResource(R.string.salvar_filtros_botao))
        }
        Button(onClick = { viewModel.resetarFiltros() }) {
            Text(stringResource(R.string.resetar_filtros_botao))
        }
    }
}

@Preview(showBackground = true, name = "Filtros Screen Completa")
@Composable
fun DefaultPreviewFiltrosScreen() {
    val mockMainViewModel = MainViewModel(Application(), AppDatabase.getDatabase(LocalContext.current.applicationContext as Application).jogoDao().let { com.example.cebolaolotofacilgenerator.data.repository.JogoRepository(it) }, AppDatabase.getDatabase(LocalContext.current.applicationContext as Application).resultadoDao().let { ResultadoRepository(it) }, com.example.cebolaolotofacilgenerator.data.AppDataStore(LocalContext.current.applicationContext as Application)) // Necessita Application para DataStore
    val mockFiltrosViewModel = FiltrosViewModel(LocalContext.current.applicationContext as Application, AppDatabase.getDatabase(LocalContext.current.applicationContext as Application).resultadoDao().let { ResultadoRepository(it) })
    // Popular mockFiltrosViewModel com dados se necessário para o preview
    FiltrosScreen(mainViewModel = mockMainViewModel, navController = NavController(LocalContext.current))
}

@Preview(showBackground = true, name = "Filtro Estatístico Item")
@Composable
fun PreviewFiltroEstatisticoItem() {
    FiltroEstatisticoItem(
        titulo = "Teste Pares/Ímpares",
        isChecked = true,
        onCheckedChange = {},
        valorMin = 5,
        valorMax = 10,
        onRangeChange = { _,_ -> },
        rangeSliderFrom = 0f,
        rangeSliderTo = 15f,
        stepSize = 1,
        valorLabelFormat = "Ímpares: %1$d-%2$d"
    )
}

// TODO: Adicionar strings para "Filtros Básicos", "Números Especiais", "Aplicar", "Salvar", "Resetar"
// e verificar as strings dos filtros estatísticos como R.string.filtro_pares_impares_titulo 