package com.example.cebolaolotofacilgenerator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.example.cebolaolotofacilgenerator.data.model.Jogo
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

        // Observar o status da operação e os jogos gerados (se necessário aqui)
        val operacaoStatus by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
        // val mensagem by geradorViewModel.mensagem.observeAsState("") // Mensagem agora é via SnackbarManager ou MainViewModel

        // Coletar estados das dezenas fixas para o BotaoGerarJogos
        val dezenasFixasParaGeracao by geradorViewModel.numerosFixosState.collectAsState()

        // Observar configurações de filtro para passar para BotaoGerarJogos
        // val configFiltros by mainViewModel.filtrosViewModel.configuracaoFiltros.observeAsState(ConfiguracaoFiltros()) // Substituído por configFiltrosGlobais
        // val quantidadeJogos = configFiltrosGlobais.quantidadeJogos
        // val quantidadeNumeros = configFiltrosGlobais.quantidadeNumerosPorJogo

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
                                // Não navegar para JogosGerados mais
                                // navController.navigate(Screen.JogosGerados.route)
                                // Apenas resetar o status para permitir novas operações
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
                            onConfigChange = { novaConfig ->
                                filtrosViewModel.atualizarFiltro(novaConfig)
                            },
                            geradorViewModel = geradorViewModel
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
                            subtitulo = "Defina a quantidade de números pares/ímpares.",
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
                            labelMin = "Mín. Ímpares",
                            labelMax = "Máx. Ímpares",
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
                            subtitulo = "Defina a faixa de soma total das dezenas.",
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
                            labelMin = "Soma Mín.",
                            labelMax = "Soma Máx.",
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
                            subtitulo = "Defina a quantidade de números primos.",
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
                            labelMin = "Mín. Primos",
                            labelMax = "Máx. Primos",
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
                            subtitulo = "Defina a quantidade de números de Fibonacci.",
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
                            labelMin = "Mín. Fibonacci",
                            labelMax = "Máx. Fibonacci",
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
                            subtitulo = "Defina a quantidade de números no miolo.",
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
                            labelMin = "Mín. Miolo",
                            labelMax = "Máx. Miolo",
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
                            subtitulo = "Defina a quantidade de múltiplos de três.",
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
                            labelMin = "Mín. Múltiplos de 3",
                            labelMax = "Máx. Múltiplos de 3",
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
                                geradorViewModel.salvarFiltrosConfigAtual()
                            },
                            onResetarClick = {
                                geradorViewModel.resetarConfiguracaoFiltrosParaPadrao()
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (operacaoStatus == OperacaoStatus.CARREGANDO) {
                                CircularProgressIndicator(
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp)) // Espaço antes do botão

                        BotaoGerarJogos(
                            status = operacaoStatus,
                            quantidadeJogos = configFiltrosGlobais.quantidadeJogos,
                            quantidadeNumeros = configFiltrosGlobais.quantidadeNumerosPorJogo,
                            filtrosAtivos = filtrosEstatisticosAtivos,
                            dezenasFixas = dezenasFixasParaGeracao,
                            onGerarClick = {
                                geradorViewModel.gerarJogos()
                            },
                            enabled = operacaoStatus != OperacaoStatus.CARREGANDO
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Espaço adicional no final

                        // Lista de jogos gerados
                        val jogosGerados by geradorViewModel.jogosGerados.observeAsState(emptyList())
                        if (jogosGerados.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Jogos Gerados (${jogosGerados.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    
                                    // Lista limitada a 10 jogos para não sobrecarregar a UI
                                    // Com opção de "Ver Todos" que navega para tela de jogos gerados
                                    val jogosExibidos = jogosGerados.take(10)
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(320.dp), // Altura fixa para não expandir demais
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(jogosExibidos) { jogo ->
                                            JogoItem(jogo = jogo)
                                        }
                                    }
                                    
                                    if (jogosGerados.size > 10) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(
                                            onClick = { navController.navigate(Screen.JogosGerados.route) },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Ver todos os ${jogosGerados.size} jogos")
                                        }
                                    }
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        OutlinedButton(
                                            onClick = { geradorViewModel.limparJogosGerados() }
                                        ) {
                                            Text("Limpar")
                                        }
                                        
                                        Button(
                                            onClick = {
                                                geradorViewModel.salvarJogosGerados()
                                                SnackbarManager.mostrarMensagem("Jogos salvos com sucesso!")
                                            }
                                        ) {
                                            Text("Salvar Jogos")
                                        }
                                    }
                                }
                            }
                        }
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Configurações Gerais", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Definição de Dezenas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))

            Text("Números Fixos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = configFiltros.numerosFixos.joinToString(","),
                onValueChange = {
                    val numeros = it.split(Regex("[^\\d]+"))
                        .filter { s -> s.isNotBlank() }
                        .mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }
                        .distinct()
                        .sorted()
                    if (numeros.size <= 10) {
                        onConfigChange(configFiltros.copy(numerosFixos = numeros))
                    }
                },
                label = { Text("Dezenas que DEVEM estar no jogo") },
                placeholder = { Text("Separe por vírgulas. Ex: 1,5,10") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Números Excluídos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = configFiltros.numerosExcluidos.joinToString(","),
                onValueChange = {
                    val excluidos = it.split(Regex("[^\\d]+"))
                        .filter { s -> s.isNotBlank() }
                        .mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }
                        .distinct()
                        .sorted()
                    if (excluidos.size <= 10) {
                        onConfigChange(configFiltros.copy(numerosExcluidos = excluidos))
                    }
                },
                label = { Text("Dezenas que NÃO DEVEM estar no jogo") },
                placeholder = { Text("Separe por vírgulas. Ex: 2,7,12") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

// Novo Composable para Repetição do Concurso Anterior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeticaoConcursoAnteriorCard(
    configFiltros: ConfiguracaoFiltros,
    onConfigChange: (ConfiguracaoFiltros) -> Unit,
    geradorViewModel: GeradorViewModel
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Repetição do Concurso Anterior", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))

            OutlinedTextField(
                value = configFiltros.dezenasConcursoAnterior.joinToString(","),
                onValueChange = {
                    val numeros = it.split(Regex("[^\\d]+"))
                        .filter { s -> s.isNotBlank() }
                        .mapNotNull { s -> s.toIntOrNull()?.coerceIn(1,25) }
                        .distinct()
                        .sorted()
                    if (numeros.size <= 15) {
                        onConfigChange(configFiltros.copy(dezenasConcursoAnterior = numeros))
                    }
                },
                label = { Text(stringResource(R.string.label_dezenas_concurso_anterior)) },
                placeholder = { Text(stringResource(R.string.placeholder_dezenas_concurso_anterior)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            FiltroCardMinMax(
                titulo = "Repetição de Dezenas",
                subtitulo = "Quantas dezenas do concurso anterior devem se repetir?",
                checado = configFiltros.filtroRepeticaoConcursoAnterior,
                onCheckedChange = { ativado ->
                    geradorViewModel.atualizarFiltroRepeticao(ativado, configFiltros.minRepeticaoConcursoAnterior, configFiltros.maxRepeticaoConcursoAnterior)
                },
                minInput = configFiltros.minRepeticaoConcursoAnterior.toString(),
                onMinInputChange = { novoMinStr ->
                    val novoMin = novoMinStr.toIntOrNull()
                    if (novoMin != null) {
                        geradorViewModel.atualizarFiltroRepeticao(configFiltros.filtroRepeticaoConcursoAnterior, novoMin, configFiltros.maxRepeticaoConcursoAnterior)
                    } else if (novoMinStr.isEmpty()) {
                        geradorViewModel.atualizarFiltroRepeticao(configFiltros.filtroRepeticaoConcursoAnterior, ConfiguracaoFiltros().minRepeticaoConcursoAnterior, configFiltros.maxRepeticaoConcursoAnterior)
                    }
                },
                maxInput = configFiltros.maxRepeticaoConcursoAnterior.toString(),
                onMaxInputChange = { novoMaxStr ->
                    val novoMax = novoMaxStr.toIntOrNull()
                    if (novoMax != null) {
                        geradorViewModel.atualizarFiltroRepeticao(configFiltros.filtroRepeticaoConcursoAnterior, configFiltros.minRepeticaoConcursoAnterior, novoMax)
                    } else if (novoMaxStr.isEmpty()) {
                        geradorViewModel.atualizarFiltroRepeticao(configFiltros.filtroRepeticaoConcursoAnterior, configFiltros.minRepeticaoConcursoAnterior, ConfiguracaoFiltros().maxRepeticaoConcursoAnterior)
                    }
                },
                labelMin = "Mín. Repetição",
                labelMax = "Máx. Repetição",
                rangeSliderValue = configFiltros.minRepeticaoConcursoAnterior.toFloat()..configFiltros.maxRepeticaoConcursoAnterior.toFloat(),
                onRangeSliderValueChange = { novaRange ->
                    geradorViewModel.atualizarFiltroRepeticao(
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
            Text(stringResource(R.string.salvar_filtros_botao))
        }
        Button(onClick = onResetarClick) {
            Text(stringResource(R.string.resetar_filtros_botao))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JogoItem(jogo: Jogo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                jogo.numeros.forEach { numero ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(28.dp)
                            .aspectRatio(1f)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = numero.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}