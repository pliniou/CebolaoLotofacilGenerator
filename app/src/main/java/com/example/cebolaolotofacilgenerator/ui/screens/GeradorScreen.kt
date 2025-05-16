package com.example.cebolaolotofacilgenerator.ui.screens

// único import de
// OperacaoStatus
// import androidx.compose.foundation.layout.wrapContentHeight // Não é mais necessário com
// LazyVerticalGrid
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.Screen
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.ui.components.BotaoGerarJogos
import com.example.cebolaolotofacilgenerator.ui.components.SnackbarManager
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class) // Adicionar OptIn aqui e FlowPreview
@Composable
fun GeradorScreen(
        navController: NavHostController,
        dezenasFixasArg: String?, // Recebe as dezenas como String da navegação
        mainViewModel: MainViewModel // Alterado nome do parâmetro para mainViewModel
) {
        val geradorViewModel: GeradorViewModel = mainViewModel.geradorViewModel
        val filtrosViewModel: FiltrosViewModel = mainViewModel.filtrosViewModel // Acesso ao FiltrosViewModel

        LaunchedEffect(key1 = mainViewModel) {
            geradorViewModel.setMainViewModelRef(mainViewModel)
        }

        // Coletar o estado de configuracaoFiltros do FiltrosViewModel
        val configFiltrosGlobais by filtrosViewModel.configuracaoFiltros.observeAsState(ConfiguracaoFiltros())

        // Observar se tem último resultado salvo para o botão de carregar dezenas
        val temUltimoResultado by filtrosViewModel.temUltimoResultadoSalvo.collectAsState()

        // Observar o status da operação e os jogos gerados (se necessário aqui)
        val operacaoStatus by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
        // val mensagem by geradorViewModel.mensagem.observeAsState("") // Mensagem agora é via SnackbarManager ou MainViewModel

        // Coletar estados das dezenas fixas para o BotaoGerarJogos
        val dezenasFixasParaGeracao by geradorViewModel.numerosFixosState.collectAsState()

        // Observar configurações de filtro para passar para BotaoGerarJogos
        // val configFiltros by mainViewModel.filtrosViewModel.configuracaoFiltros.observeAsState(ConfiguracaoFiltros()) // Substituído por configFiltrosGlobais
        val quantidadeJogos = configFiltrosGlobais.quantidadeJogos
        val quantidadeNumeros = configFiltrosGlobais.quantidadeNumerosPorJogo

        // Determinar se algum filtro estatístico está ativo
        val filtrosEstatisticosAtivos = remember(configFiltrosGlobais) {
            configFiltrosGlobais.filtroParesImpares ||
            configFiltrosGlobais.filtroSomaTotal ||
            configFiltrosGlobais.filtroPrimos ||
            configFiltrosGlobais.filtroFibonacci ||
            configFiltrosGlobais.filtroMioloMoldura ||
            configFiltrosGlobais.filtroMultiplosDeTres ||
            configFiltrosGlobais.filtroRepeticaoConcursoAnterior
        }

        LaunchedEffect(dezenasFixasArg) {
                val dezenas = dezenasFixasArg?.split(",")?.mapNotNull { it.toIntOrNull() }
                geradorViewModel.inicializarComNumerosFixos(dezenas)
        }

        // Observar mensagens para exibir Snackbars
        LaunchedEffect(geradorViewModel.mensagem.value) {
                val mensagemAtual = geradorViewModel.mensagem.value
                if (!mensagemAtual.isNullOrEmpty()) {
                        SnackbarManager.mostrarMensagem(mensagemAtual)
                }
        }

        // Observar mudanças de operação para exibir mensagens específicas
        LaunchedEffect(operacaoStatus) {
                when(operacaoStatus) {
                        OperacaoStatus.SUCESSO -> {
                                val jogosGerados = geradorViewModel.jogosGerados.value
                                if (!jogosGerados.isNullOrEmpty()) {
                                        SnackbarManager.mostrarMensagem("${jogosGerados.size} jogos gerados com sucesso!")
                                }
                        }
                        OperacaoStatus.ERRO -> {
                                SnackbarManager.mostrarMensagem("Erro ao gerar jogos. Verifique os filtros e tente novamente.")
                        }
                        else -> {} // Não exibir mensagem para outros estados
                }
        }

        // Efeito para observar o status da operação e navegar ou mostrar mensagens
        LaunchedEffect(operacaoStatus) {
                when (operacaoStatus) {
                        OperacaoStatus.SUCESSO -> {
                                navController.navigate(Screen.JogosGerados.route)
                                geradorViewModel.resetarStatusOperacao()
                        }
                        OperacaoStatus.ERRO -> {
                                geradorViewModel.resetarStatusOperacao()
                        }
                        OperacaoStatus.CARREGANDO -> {
                                // Já tratado pelo CircularProgressIndicator
                        }
                        OperacaoStatus.OCIOSO -> {
                                // Nada a fazer
                        }
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Gerar Jogos") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary,
                                                navigationIconContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        ),
                                navigationIcon = {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                                Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = stringResource(R.string.voltar)
                                                )
                                        }
                                }
                        )
                }
        ) { paddingValues ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(paddingValues)
                                        .padding(
                                                horizontal = 16.dp
                                        ) // Padding horizontal para a coluna principal
                                        .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Novas seções de configuração
                        ConfiguracoesGeraisCard(
                            configFiltros = configFiltrosGlobais,
                            onConfigChange = { novaConfig ->
                                filtrosViewModel.atualizarFiltro(novaConfig)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DefinicaoDezenasCard(
                            configFiltros = configFiltrosGlobais,
                            onConfigChange = { novaConfig ->
                                filtrosViewModel.atualizarFiltro(novaConfig)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        RepeticaoConcursoAnteriorCard(
                            configFiltros = configFiltrosGlobais,
                            temUltimoResultado = temUltimoResultado,
                            onConfigChange = { novaConfig ->
                                filtrosViewModel.atualizarFiltro(novaConfig)
                            },
                            onCarregarDezenasClick = {
                                filtrosViewModel.carregarDezenasDoUltimoResultadoSalvo()
                            },
                            geradorViewModel = geradorViewModel // Passando para o FiltroCardMinMax interno
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Fim das novas seções

                        Text(
                                "Filtros Estatísticos:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Conteúdo dos filtros refatorado
                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_pares_impares_titulo),
                            subtitulo = stringResource(R.string.filtro_pares_impares_subtitulo),
                            checado = configFiltrosGlobais.filtroParesImpares,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroParesImpares(ativado, configFiltrosGlobais.minImpares, configFiltrosGlobais.maxImpares)
                            },
                            minInput = configFiltrosGlobais.minImpares.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                if (novoMin != null) {
                                    geradorViewModel.atualizarFiltroParesImpares(configFiltrosGlobais.filtroParesImpares, novoMin, configFiltrosGlobais.maxImpares)
                                } else if (novoMinStr.isEmpty()) {
                                     geradorViewModel.atualizarFiltroParesImpares(configFiltrosGlobais.filtroParesImpares, ConfiguracaoFiltros().minImpares, configFiltrosGlobais.maxImpares)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxImpares.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                if (novoMax != null) {
                                    geradorViewModel.atualizarFiltroParesImpares(configFiltrosGlobais.filtroParesImpares, configFiltrosGlobais.minImpares, novoMax)
                                } else if (novoMaxStr.isEmpty()) {
                                     geradorViewModel.atualizarFiltroParesImpares(configFiltrosGlobais.filtroParesImpares, configFiltrosGlobais.minImpares, ConfiguracaoFiltros().maxImpares)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_min_impares_label),
                            labelMax = stringResource(R.string.filtro_max_impares_label),
                            rangeSliderValue = configFiltrosGlobais.minImpares.toFloat()..configFiltrosGlobais.maxImpares.toFloat(),
                            onRangeSliderValueChange = { novaRange ->
                                geradorViewModel.atualizarFiltroParesImpares(
                                    configFiltrosGlobais.filtroParesImpares,
                                    novaRange.start.toInt().coerceIn(0,15),
                                    novaRange.endInclusive.toInt().coerceIn(0,15)
                                )
                            },
                            valueRangeSlider = 0f..15f,
                            stepsSlider = 14
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_soma_total_titulo),
                            subtitulo = stringResource(R.string.filtro_soma_total_subtitulo),
                            checado = configFiltrosGlobais.filtroSomaTotal,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroSomaTotal(ativado, configFiltrosGlobais.minSoma, configFiltrosGlobais.maxSoma)
                            },
                            minInput = configFiltrosGlobais.minSoma.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minSoma else novoMin
                                if (minToSet != null) {
                                   geradorViewModel.atualizarFiltroSomaTotal(configFiltrosGlobais.filtroSomaTotal, minToSet, configFiltrosGlobais.maxSoma)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxSoma.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxSoma else novoMax
                                if (maxToSet != null) {
                                    geradorViewModel.atualizarFiltroSomaTotal(configFiltrosGlobais.filtroSomaTotal, configFiltrosGlobais.minSoma, maxToSet)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_soma_min_label),
                            labelMax = stringResource(R.string.filtro_soma_max_label),
                            rangeSliderValue = configFiltrosGlobais.minSoma.toFloat()..configFiltrosGlobais.maxSoma.toFloat(),
                            onRangeSliderValueChange = { novaRange ->
                                geradorViewModel.atualizarFiltroSomaTotal(
                                    configFiltrosGlobais.filtroSomaTotal,
                                    novaRange.start.toInt(),
                                    novaRange.endInclusive.toInt()
                                )
                            },
                            valueRangeSlider = 120f..270f,
                            stepsSlider = 149
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_primos_titulo),
                            subtitulo = stringResource(R.string.filtro_primos_subtitulo),
                            checado = configFiltrosGlobais.filtroPrimos,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroPrimos(ativado, configFiltrosGlobais.minPrimos, configFiltrosGlobais.maxPrimos)
                            },
                            minInput = configFiltrosGlobais.minPrimos.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minPrimos else novoMin
                                if (minToSet != null) {
                                    geradorViewModel.atualizarFiltroPrimos(configFiltrosGlobais.filtroPrimos, minToSet, configFiltrosGlobais.maxPrimos)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxPrimos.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxPrimos else novoMax
                                if (maxToSet != null) {
                                   geradorViewModel.atualizarFiltroPrimos(configFiltrosGlobais.filtroPrimos, configFiltrosGlobais.minPrimos, maxToSet)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_min_primos_label),
                            labelMax = stringResource(R.string.filtro_max_primos_label),
                            rangeSliderValue = configFiltrosGlobais.minPrimos.toFloat()..configFiltrosGlobais.maxPrimos.toFloat(),
                            onRangeSliderValueChange = { novaRange ->
                                geradorViewModel.atualizarFiltroPrimos(
                                    configFiltrosGlobais.filtroPrimos,
                                    novaRange.start.toInt(),
                                    novaRange.endInclusive.toInt()
                                )
                            },
                            valueRangeSlider = 0f..9f,
                            stepsSlider = 8
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_fibonacci_titulo),
                            subtitulo = stringResource(R.string.filtro_fibonacci_subtitulo),
                            checado = configFiltrosGlobais.filtroFibonacci,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroFibonacci(ativado, configFiltrosGlobais.minFibonacci, configFiltrosGlobais.maxFibonacci)
                            },
                            minInput = configFiltrosGlobais.minFibonacci.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minFibonacci else novoMin
                                if (minToSet != null) {
                                   geradorViewModel.atualizarFiltroFibonacci(configFiltrosGlobais.filtroFibonacci, minToSet, configFiltrosGlobais.maxFibonacci)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxFibonacci.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxFibonacci else novoMax
                                if (maxToSet != null) {
                                    geradorViewModel.atualizarFiltroFibonacci(configFiltrosGlobais.filtroFibonacci, configFiltrosGlobais.minFibonacci, maxToSet)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_min_fibonacci_label),
                            labelMax = stringResource(R.string.filtro_max_fibonacci_label),
                            rangeSliderValue = configFiltrosGlobais.minFibonacci.toFloat()..configFiltrosGlobais.maxFibonacci.toFloat(),
                            onRangeSliderValueChange = { novaRange ->
                                geradorViewModel.atualizarFiltroFibonacci(
                                    configFiltrosGlobais.filtroFibonacci,
                                    novaRange.start.toInt(),
                                    novaRange.endInclusive.toInt()
                                )
                            },
                            valueRangeSlider = 0f..7f,
                            stepsSlider = 6
                        )
                        
                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_miolo_moldura_titulo),
                            subtitulo = stringResource(R.string.filtro_miolo_moldura_subtitulo),
                            checado = configFiltrosGlobais.filtroMioloMoldura,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroMioloMoldura(ativado, configFiltrosGlobais.minMiolo, configFiltrosGlobais.maxMiolo)
                            },
                            minInput = configFiltrosGlobais.minMiolo.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minMiolo else novoMin
                                if (minToSet != null) {
                                    geradorViewModel.atualizarFiltroMioloMoldura(configFiltrosGlobais.filtroMioloMoldura, minToSet, configFiltrosGlobais.maxMiolo)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxMiolo.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxMiolo else novoMax
                                if (maxToSet != null) {
                                    geradorViewModel.atualizarFiltroMioloMoldura(configFiltrosGlobais.filtroMioloMoldura, configFiltrosGlobais.minMiolo, maxToSet)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_min_miolo_label),
                            labelMax = stringResource(R.string.filtro_max_miolo_label),
                            rangeSliderValue = configFiltrosGlobais.minMiolo.toFloat()..configFiltrosGlobais.maxMiolo.toFloat(),
                            onRangeSliderValueChange = { novaRange ->
                                geradorViewModel.atualizarFiltroMioloMoldura(
                                    configFiltrosGlobais.filtroMioloMoldura,
                                    novaRange.start.toInt(),
                                    novaRange.endInclusive.toInt()
                                )
                            },
                            valueRangeSlider = 0f..9f,
                            stepsSlider = 8
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_multiplos_de_tres_titulo),
                            subtitulo = stringResource(R.string.filtro_multiplos_de_tres_subtitulo),
                            checado = configFiltrosGlobais.filtroMultiplosDeTres,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroMultiplosDeTres(ativado, configFiltrosGlobais.minMultiplos, configFiltrosGlobais.maxMultiplos)
                            },
                            minInput = configFiltrosGlobais.minMultiplos.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minMultiplos else novoMin
                                if (minToSet != null) {
                                    geradorViewModel.atualizarFiltroMultiplosDeTres(configFiltrosGlobais.filtroMultiplosDeTres, minToSet, configFiltrosGlobais.maxMultiplos)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxMultiplos.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxMultiplos else novoMax
                                if (maxToSet != null) {
                                    geradorViewModel.atualizarFiltroMultiplosDeTres(configFiltrosGlobais.filtroMultiplosDeTres, configFiltrosGlobais.minMultiplos, maxToSet)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_min_multiplos_de_tres_label),
                            labelMax = stringResource(R.string.filtro_max_multiplos_de_tres_label),
                            rangeSliderValue = configFiltrosGlobais.minMultiplos.toFloat()..configFiltrosGlobais.maxMultiplos.toFloat(),
                            onRangeSliderValueChange = { novaRange ->
                                geradorViewModel.atualizarFiltroMultiplosDeTres(
                                    configFiltrosGlobais.filtroMultiplosDeTres,
                                    novaRange.start.toInt(),
                                    novaRange.endInclusive.toInt()
                                )
                            },
                            valueRangeSlider = 0f..8f,
                            stepsSlider = 7
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botões de Ação para Configuração de Filtros
                        AcoesConfiguracaoFiltrosButtons(
                            onSalvarClick = {
                                filtrosViewModel.salvarConfiguracaoFiltrosAtual() // Método a ser implementado/verificado no ViewModel
                                mainViewModel.showSnackbar("Configurações de filtro salvas!") // Exemplo de feedback
                            },
                            onResetarClick = {
                                filtrosViewModel.resetarConfiguracaoFiltrosParaPadrao() // Método a ser implementado/verificado no ViewModel
                                mainViewModel.showSnackbar("Configurações de filtro restauradas para o padrão!") // Exemplo de feedback
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (operacaoStatus == OperacaoStatus.CARREGANDO) {
                                CircularProgressIndicator(
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )
                        }

                        BotaoGerarJogos(
                            status = operacaoStatus,
                            quantidadeJogos = quantidadeJogos,
                            quantidadeNumeros = quantidadeNumeros,
                            filtrosAtivos = filtrosEstatisticosAtivos,
                            dezenasFixas = dezenasFixasParaGeracao, 
                            onGerarClick = { geradorViewModel.gerarJogosComConfiguracaoAtual() }
                        )
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroSwitchItem(
        titulo: String,
        subtitulo: String,
        checado: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
        Row(
                modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(titulo, style = MaterialTheme.typography.titleMedium)
                        Text(subtitulo, style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                        checked = checado,
                        onCheckedChange = onCheckedChange,
                        colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroCardMinMax(
        titulo: String,
        subtitulo: String,
        checado: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        minInput: String,
        onMinInputChange: (String) -> Unit,
        maxInput: String,
        onMaxInputChange: (String) -> Unit,
        labelMin: String,
        labelMax: String,
        rangeSliderValue: ClosedFloatingPointRange<Float>,
        onRangeSliderValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRangeSlider: ClosedFloatingPointRange<Float>,
        stepsSlider: Int,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.medium
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        FiltroSwitchItem(
                                titulo = titulo,
                                subtitulo = subtitulo,
                                checado = checado,
                                onCheckedChange = onCheckedChange
                        )
                        if (checado) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        OutlinedTextField(
                                                value = minInput,
                                                onValueChange = onMinInputChange,
                                                label = { Text(labelMin) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.weight(1f),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxInput,
                                                onValueChange = onMaxInputChange,
                                                label = { Text(labelMax) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.weight(1f),
                                                singleLine = true
                                        )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                RangeSlider(
                                    value = rangeSliderValue,
                                    onValueChange = onRangeSliderValueChange,
                                    valueRange = valueRangeSlider,
                                    steps = stepsSlider,
                                    modifier = Modifier.fillMaxWidth()
                                )
                        }
                }
        }
}

// Composable para a grade de seleção de dezenas
@Composable
fun SeletorDezenasGrid(
        titulo: String,
        dezenasSelecionadas: List<Int>,
        dezenasDesabilitadas: List<Int>,
        onDezenaClick: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
        val todasDezenas = (1..25).toList()

        Column(modifier = modifier.fillMaxWidth()) {
                Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                        modifier = Modifier.fillMaxWidth()
                ) {
                        LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                contentPadding =
                                        PaddingValues(
                                                0.dp
                                        ),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier =
                                        Modifier.align(Alignment.Center)
                                                .height(
                                                        280.dp
                                                )
                                ) {
                                items(todasDezenas) { dezena ->
                                        val isSelected = dezena in dezenasSelecionadas
                                        val isEnabled = dezena !in dezenasDesabilitadas

                                        val buttonColors =
                                                when {
                                                        isSelected ->
                                                                ButtonDefaults.outlinedButtonColors(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        contentColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onPrimary
                                                                )
                                                        !isEnabled ->
                                                                ButtonDefaults.outlinedButtonColors(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurface
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.12f
                                                                                        ),
                                                                                contentColor =
                                                                                        MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurface
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.38f
                                                                                        )
                                                                )
                                                        else ->
                                                                ButtonDefaults
                                                                        .outlinedButtonColors(
                                                                                containerColor =
                                                                                        Color.Transparent,
                                                                                contentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        )
                                                }
                                        val borderColor =
                                                when {
                                                        isSelected ->
                                                                BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                )
                                                        !isEnabled ->
                                                                BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme
                                                                                .onSurface.copy(
                                                                                alpha = 0.12f
                                                                        )
                                                                )
                                                        else ->
                                                                BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme
                                                                                .outline
                                                                )
                                                }

                                        OutlinedButton(
                                                onClick = { if (isEnabled) onDezenaClick(dezena) },
                                                modifier =
                                                        Modifier.aspectRatio(
                                                                        1f
                                                                )
                                                                .padding(
                                                                        2.dp
                                                                ),
                                                enabled = isEnabled,
                                                shape =
                                                        MaterialTheme.shapes
                                                                .small,
                                                colors = buttonColors,
                                                border = borderColor,
                                                contentPadding =
                                                        PaddingValues(
                                                                0.dp
                                                        )
                                                ) {
                                                Text(
                                                        text =
                                                                dezena.toString()
                                                                        .padStart(
                                                                                2,
                                                                                '0'
                                                                        ),
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                }
                Spacer(modifier = Modifier.height(8.dp))
        }
}

// Novo Composable para Configurações Gerais
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesGeraisCard(
    configFiltros: ConfiguracaoFiltros,
    onConfigChange: (ConfiguracaoFiltros) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.configuracoes_gerais_titulo), // Adicionar string resource
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = configFiltros.quantidadeJogos.toString(),
                onValueChange = { novoValorStr ->
                    val novoValor = novoValorStr.toIntOrNull() ?: 1
                    onConfigChange(configFiltros.copy(quantidadeJogos = novoValor.coerceIn(1, 100)))
                },
                label = { Text(stringResource(R.string.quantidade_de_jogos_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = configFiltros.quantidadeNumerosPorJogo.toString(),
                onValueChange = { novoValorStr ->
                    val novoValor = novoValorStr.toIntOrNull() ?: 15
                    if (novoValor in 15..20) { // Limite da Lotofácil
                        onConfigChange(configFiltros.copy(quantidadeNumerosPorJogo = novoValor))
                    }
                },
                label = { Text(stringResource(R.string.quantidade_de_numeros_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                // Informação adicional de que é fixo em 15 para Lotofácil, mas o app permite mais.
                // supportingText = { Text(stringResource(R.string.info_qtd_numeros_lotofacil)) } // Adicionar string
            )
        }
    }
}

// Novo Composable para Definição de Dezenas (Fixas e Excluídas)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefinicaoDezenasCard(
    configFiltros: ConfiguracaoFiltros,
    onConfigChange: (ConfiguracaoFiltros) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.definicao_dezenas_titulo), // Adicionar string resource
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dezenas Fixas
            Text(
                text = stringResource(R.string.numeros_fixos_label_long), // Adicionar string: "Dezenas que DEVEM estar no jogo:"
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Text(
                text = stringResource(R.string.numeros_fixos_explicacao), // Adicionar string: "Escolha até 10 dezenas."
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SeletorDezenasGrid(
                titulo = "", // O título já está acima
                dezenasSelecionadas = configFiltros.numerosFixos,
                dezenasDesabilitadas = configFiltros.numerosExcluidos, // Não pode fixar uma dezena já excluída
                onDezenaClick = { dezenaClicada ->
                    val fixosAtuais = configFiltros.numerosFixos.toMutableList()
                    if (dezenaClicada in fixosAtuais) {
                        fixosAtuais.remove(dezenaClicada)
                    } else {
                        if (fixosAtuais.size < 10) { // Limite de 10 dezenas fixas
                           fixosAtuais.add(dezenaClicada)
                        }
                    }
                    onConfigChange(configFiltros.copy(numerosFixos = fixosAtuais.sorted()))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dezenas Excluídas
            Text(
                text = stringResource(R.string.numeros_excluidos_label_long), // Adicionar string: "Dezenas que NÃO DEVEM estar no jogo:"
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Text(
                text = stringResource(R.string.numeros_excluidos_explicacao), // Adicionar string: "Escolha dezenas a serem evitadas."
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SeletorDezenasGrid(
                titulo = "", // O título já está acima
                dezenasSelecionadas = configFiltros.numerosExcluidos,
                dezenasDesabilitadas = configFiltros.numerosFixos, // Não pode excluir uma dezena já fixada
                onDezenaClick = { dezenaClicada ->
                    val excluidosAtuais = configFiltros.numerosExcluidos.toMutableList()
                    if (dezenaClicada in excluidosAtuais) {
                        excluidosAtuais.remove(dezenaClicada)
                    } else {
                         // Não há limite explícito para excluídos, mas não deve exceder um número razoável (ex: 10 também)
                         // Para Lotofácil, se fixar muitas, excluir muitas pode levar a zero combinações.
                         // O limite de 10 para fixos e 10 para excluídos, não sendo os mesmos, parece ok.
                        if (excluidosAtuais.size < 10) { // Aplicando um limite similar aos fixos por segurança
                            excluidosAtuais.add(dezenaClicada)
                        }
                    }
                    onConfigChange(configFiltros.copy(numerosExcluidos = excluidosAtuais.sorted()))
                }
            )
        }
    }
}

// Novo Composable para Repetição do Concurso Anterior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeticaoConcursoAnteriorCard(
    configFiltros: ConfiguracaoFiltros,
    temUltimoResultado: Boolean,
    onConfigChange: (ConfiguracaoFiltros) -> Unit,
    onCarregarDezenasClick: () -> Unit,
    geradorViewModel: GeradorViewModel // Necessário para o FiltroCardMinMax
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.repeticao_concurso_anterior_titulo), // Adicionar string
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = configFiltros.dezenasConcursoAnterior.joinToString(","),
                onValueChange = {
                    val numeros = it.split(Regex("[^\\d]+"))
                        .filter { s -> s.isNotBlank() }
                        .mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }
                        .distinct()
                        .sorted()
                    if (numeros.size <= 15) { // Lotofácil tem 15 dezenas sorteadas
                        onConfigChange(configFiltros.copy(dezenasConcursoAnterior = numeros))
                    }
                },
                label = { Text(stringResource(R.string.label_dezenas_concurso_anterior)) }, // Reutilizar string existente
                placeholder = { Text(stringResource(R.string.placeholder_dezenas_concurso_anterior)) }, // Reutilizar string existente
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCarregarDezenasClick,
                enabled = temUltimoResultado,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.carregar_dezenas_salvas_botao)) // Reutilizar string existente
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Usar o FiltroCardMinMax para a quantidade de dezenas a repetir
            FiltroCardMinMax(
                titulo = stringResource(R.string.filtro_repeticao_dezenas_titulo), // Reutilizar string
                subtitulo = stringResource(R.string.filtro_repeticao_dezenas_subtitulo), // Reutilizar string
                checado = configFiltros.filtroRepeticaoConcursoAnterior,
                onCheckedChange = { ativado ->
                    // Aqui, onConfigChange atualiza o filtro global. 
                    // O geradorViewModel.atualizarFiltroRepeticaoDezenas é mais específico.
                    // Para consistência, deveríamos ter um método no filtrosViewModel para atualizar partes do config,
                    // ou o geradorViewModel deveria operar sobre o mesmo objeto configFiltros.
                    // Por ora, mantendo a chamada ao geradorViewModel conforme estava antes da refatoração.
                    geradorViewModel.atualizarFiltroRepeticaoDezenas(ativado, configFiltros.minRepeticaoConcursoAnterior, configFiltros.maxRepeticaoConcursoAnterior)
                },
                minInput = configFiltros.minRepeticaoConcursoAnterior.toString(),
                onMinInputChange = { novoMinStr ->
                    val novoMin = novoMinStr.toIntOrNull()
                    val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minRepeticaoConcursoAnterior else novoMin
                    if (minToSet != null) {
                        geradorViewModel.atualizarFiltroRepeticaoDezenas(configFiltros.filtroRepeticaoConcursoAnterior, minToSet, configFiltros.maxRepeticaoConcursoAnterior)
                    }
                },
                maxInput = configFiltros.maxRepeticaoConcursoAnterior.toString(),
                onMaxInputChange = { novoMaxStr ->
                    val novoMax = novoMaxStr.toIntOrNull()
                    val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxRepeticaoConcursoAnterior else novoMax
                    if (maxToSet != null) {
                       geradorViewModel.atualizarFiltroRepeticaoDezenas(configFiltros.filtroRepeticaoConcursoAnterior, configFiltros.minRepeticaoConcursoAnterior, maxToSet)
                    }
                },
                labelMin = stringResource(R.string.filtro_min_repeticao_label), // Reutilizar string
                labelMax = stringResource(R.string.filtro_max_repeticao_label), // Reutilizar string
                rangeSliderValue = configFiltros.minRepeticaoConcursoAnterior.toFloat()..configFiltros.maxRepeticaoConcursoAnterior.toFloat(),
                onRangeSliderValueChange = { novaRange ->
                    geradorViewModel.atualizarFiltroRepeticaoDezenas(
                        configFiltros.filtroRepeticaoConcursoAnterior,
                        novaRange.start.toInt().coerceIn(0,15),
                        novaRange.endInclusive.toInt().coerceIn(0,15)
                    )
                },
                valueRangeSlider = 0f..15f,
                stepsSlider = 14
            )
        }
    }
}

// Novo Composable para Botões de Ação de Filtros
@Composable
fun AcoesConfiguracaoFiltrosButtons(
    onSalvarClick: () -> Unit,
    onResetarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onSalvarClick) {
            Text(stringResource(R.string.salvar_filtros_botao)) // Reutilizar string de FiltrosScreen ou criar nova
        }
        Button(onClick = onResetarClick) {
            Text(stringResource(R.string.resetar_filtros_botao)) // Reutilizar string de FiltrosScreen ou criar nova
        }
    }
}
