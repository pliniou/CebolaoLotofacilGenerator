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
                                     // Tratar input vazio, talvez resetar ou usar valor padrão do ConfiguracaoFiltros
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
                            labelMax = stringResource(R.string.filtro_max_impares_label)
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
                                // Permitir input vazio para resetar para o default do ConfiguracaoFiltros
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
                            labelMax = stringResource(R.string.filtro_soma_max_label)
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
                            labelMax = stringResource(R.string.filtro_max_primos_label)
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
                            labelMax = stringResource(R.string.filtro_max_fibonacci_label)
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
                            labelMax = stringResource(R.string.filtro_max_miolo_label)
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
                            labelMax = stringResource(R.string.filtro_max_multiplos_de_tres_label)
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_repeticao_dezenas_titulo),
                            subtitulo = stringResource(R.string.filtro_repeticao_dezenas_subtitulo),
                            checado = configFiltrosGlobais.filtroRepeticaoConcursoAnterior,
                            onCheckedChange = { ativado ->
                                geradorViewModel.atualizarFiltroRepeticaoDezenas(ativado, configFiltrosGlobais.minRepeticaoConcursoAnterior, configFiltrosGlobais.maxRepeticaoConcursoAnterior)
                            },
                            minInput = configFiltrosGlobais.minRepeticaoConcursoAnterior.toString(),
                            onMinInputChange = { novoMinStr ->
                                val novoMin = novoMinStr.toIntOrNull()
                                val minToSet = if (novoMinStr.isEmpty()) ConfiguracaoFiltros().minRepeticaoConcursoAnterior else novoMin
                                if (minToSet != null) {
                                    geradorViewModel.atualizarFiltroRepeticaoDezenas(configFiltrosGlobais.filtroRepeticaoConcursoAnterior, minToSet, configFiltrosGlobais.maxRepeticaoConcursoAnterior)
                                }
                            },
                            maxInput = configFiltrosGlobais.maxRepeticaoConcursoAnterior.toString(),
                            onMaxInputChange = { novoMaxStr ->
                                val novoMax = novoMaxStr.toIntOrNull()
                                val maxToSet = if (novoMaxStr.isEmpty()) ConfiguracaoFiltros().maxRepeticaoConcursoAnterior else novoMax
                                if (maxToSet != null) {
                                   geradorViewModel.atualizarFiltroRepeticaoDezenas(configFiltrosGlobais.filtroRepeticaoConcursoAnterior, configFiltrosGlobais.minRepeticaoConcursoAnterior, maxToSet)
                                }
                            },
                            labelMin = stringResource(R.string.filtro_min_repeticao_label),
                            labelMax = stringResource(R.string.filtro_max_repeticao_label)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // TODO: Exibir status de carregamento do geradorViewModel.operacaoStatus
                        if (operacaoStatus == OperacaoStatus.CARREGANDO) {
                                CircularProgressIndicator(
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )
                        }

                        // Botão dinâmico para geração de jogos
                        BotaoGerarJogos(
                            status = operacaoStatus,
                            quantidadeJogos = quantidadeJogos,
                            quantidadeNumeros = quantidadeNumeros,
                            filtrosAtivos = filtrosEstatisticosAtivos,
                            dezenasFixas = dezenasFixasParaGeracao, // Passar as dezenas fixas corretas para geração
                            onGerarClick = { geradorViewModel.gerarJogosComConfiguracaoAtual() }
                        )

                        // TODO: Exibir mensagens de erro/sucesso do geradorViewModel.mensagem
                        // TODO: Exibir status de carregamento do geradorViewModel.operacaoStatus
                        // (parcialmente feito acima)
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
                ) { // Para centralizar a grade se ela não preencher a largura
                        LazyVerticalGrid(
                                columns = GridCells.Fixed(5), // 5 colunas
                                contentPadding =
                                        PaddingValues(
                                                0.dp
                                        ), // Sem padding interno da LazyVerticalGrid
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier =
                                        Modifier.align(Alignment.Center)
                                                .height(
                                                        280.dp
                                                ) // Centraliza a grade e define altura fixa
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
                                                                                        ), // Cor
                                                                                // desabilitada
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
                                                                        .outlinedButtonColors( // Padrão
                                                                                containerColor =
                                                                                        Color.Transparent, // Fundo transparente para OutlinedButton
                                                                                contentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary // Cor do texto padrão
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
                                                                ) // Borda padrão
                                                }

                                        OutlinedButton(
                                                onClick = { if (isEnabled) onDezenaClick(dezena) },
                                                modifier =
                                                        Modifier.aspectRatio(
                                                                        1f
                                                                ) // Para fazer os botões quadrados
                                                                .padding(
                                                                        2.dp
                                                                ), // Padding mínimo entre botões
                                                enabled = isEnabled,
                                                shape =
                                                        MaterialTheme.shapes
                                                                .small, // Bordas levemente
                                                // arredondadas
                                                colors = buttonColors,
                                                border = borderColor,
                                                contentPadding =
                                                        PaddingValues(
                                                                0.dp
                                                        ) // Remove padding interno do botão para
                                                // centralizar texto
                                                ) {
                                                Text(
                                                        text =
                                                                dezena.toString()
                                                                        .padStart(
                                                                                2,
                                                                                '0'
                                                                        ), // Formata com dois
                                                        // dígitos
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                }
                Spacer(modifier = Modifier.height(8.dp)) // Espaço após a grade
        }
}
