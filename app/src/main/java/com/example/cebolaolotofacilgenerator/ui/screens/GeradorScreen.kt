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
import androidx.compose.material3.* // Explicitamente importar ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp // Adicionado import para dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.Screen // Import para Screen
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus // Garantir que este é o
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview
import com.example.cebolaolotofacilgenerator.ui.components.BotaoGerarJogos
import com.example.cebolaolotofacilgenerator.ui.components.SnackbarManager
import com.example.cebolaolotofacilgenerator.R // Adicionar import R

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class) // Adicionar OptIn aqui e FlowPreview
@Composable
fun GeradorScreen(
        navController: NavHostController,
        dezenasFixasArg: String?, // Recebe as dezenas como String da navegação
        mainViewModel: MainViewModel // Alterado nome do parâmetro para mainViewModel
) {
        val geradorViewModel: GeradorViewModel = mainViewModel.geradorViewModel
        // Seta a referência do MainViewModel no GeradorViewModel
        LaunchedEffect(key1 = mainViewModel) {
            geradorViewModel.setMainViewModelRef(mainViewModel)
        }

        // Coletar todos os estados dos filtros
        val filtroParesImparesAtivado by geradorViewModel.filtroParesImparesAtivado.collectAsState()
        val minParesInput by geradorViewModel.minParesInput.collectAsState()
        val maxParesInput by geradorViewModel.maxParesInput.collectAsState()
        val filtroSomaTotalAtivado by geradorViewModel.filtroSomaTotalAtivado.collectAsState()
        val minSomaInput by geradorViewModel.minSomaInput.collectAsState()
        val maxSomaInput by geradorViewModel.maxSomaInput.collectAsState()
        val filtroPrimosAtivado by geradorViewModel.filtroPrimosAtivado.collectAsState()
        val filtroFibonacciAtivado by geradorViewModel.filtroFibonacciAtivado.collectAsState()
        val filtroMioloMolduraAtivado by geradorViewModel.filtroMioloMolduraAtivado.collectAsState()
        val filtroMultiplosDeTresAtivado by
                geradorViewModel.filtroMultiplosDeTresAtivado.collectAsState()
        // Novo filtro de Repetição de Dezenas
        val filtroRepeticaoDezenasAtivado by geradorViewModel.filtroRepeticaoDezenasAtivado.collectAsState()

        // Novos StateFlows para inputs dos filtros restantes
        val minPrimosInput by geradorViewModel.minPrimosInput.collectAsState()
        val maxPrimosInput by geradorViewModel.maxPrimosInput.collectAsState()
        val minFibonacciInput by geradorViewModel.minFibonacciInput.collectAsState()
        val maxFibonacciInput by geradorViewModel.maxFibonacciInput.collectAsState()
        val minMioloInput by geradorViewModel.minMioloInput.collectAsState()
        val maxMioloInput by geradorViewModel.maxMioloInput.collectAsState()
        val minMultiplosDeTresInput by geradorViewModel.minMultiplosDeTresInput.collectAsState()
        val maxMultiplosDeTresInput by geradorViewModel.maxMultiplosDeTresInput.collectAsState()
        // Inputs para o novo filtro de Repetição de Dezenas
        val minRepeticaoInput by geradorViewModel.minRepeticaoInput.collectAsState()
        val maxRepeticaoInput by geradorViewModel.maxRepeticaoInput.collectAsState()

        // Observar o status da operação e os jogos gerados (se necessário aqui)
        val operacaoStatus by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
        val mensagem by geradorViewModel.mensagem.observeAsState("")

        // Coletar estados das dezenas fixas e excluídas
        val (dezenasSelecionadas, setDezenasSelecionadas) =
                androidx.compose.runtime.remember {
                        androidx.compose.runtime.mutableStateOf(mutableSetOf<Int>())
                }
        val (concurso, setConcurso) =
                androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        val (data, setData) =
                androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

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
                                "Último Resultado (manual)",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 12.dp)
                        )
                        OutlinedTextField(
                                value = concurso,
                                onValueChange = setConcurso,
                                label = { Text("Concurso") },
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                singleLine = true
                        )
                        OutlinedTextField(
                                value = data,
                                onValueChange = setData,
                                label = { Text("Data (opcional)") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                singleLine = true
                        )
                        Text(
                                "Selecione as dezenas sorteadas:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                contentPadding = PaddingValues(0.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(240.dp).fillMaxWidth()
                        ) {
                                items((1..25).toList()) { dezena ->
                                        val isSelected = dezenasSelecionadas.contains(dezena)
                                        OutlinedButton(
                                                onClick = {
                                                        val novaSelecao =
                                                                dezenasSelecionadas.toMutableSet()
                                                        if (isSelected) novaSelecao.remove(dezena)
                                                        else novaSelecao.add(dezena)
                                                        setDezenasSelecionadas(novaSelecao)
                                                },
                                                shape = CircleShape,
                                                colors =
                                                        if (isSelected)
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
                                                        else ButtonDefaults.outlinedButtonColors(),
                                                border =
                                                        if (isSelected)
                                                                BorderStroke(
                                                                        2.dp,
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                )
                                                        else
                                                                BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme
                                                                                .outline
                                                                ),
                                                modifier = Modifier.aspectRatio(1f)
                                        ) {
                                                Text(
                                                        text = dezena.toString().padStart(2, '0'),
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                        Button(
                                onClick = {
                                        if (dezenasSelecionadas.size == 15) {
                                                mainViewModel.salvarUltimoResultado(
                                                        dezenasSelecionadas.toList()
                                                )
                                                mainViewModel.showSnackbar("Último resultado salvo com ${dezenasSelecionadas.size} dezenas!")
                                        } else {
                                                mainViewModel.showSnackbar("Selecione exatamente 15 dezenas para o resultado.")
                                        }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                enabled = dezenasSelecionadas.size == 15
                        ) { Text("Salvar Último Resultado") }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                                "Filtros Estatísticos:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                        )

                        // Conteúdo dos filtros refatorado
                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_pares_impares_titulo),
                            subtitulo = stringResource(R.string.filtro_pares_impares_subtitulo),
                            checado = filtroParesImparesAtivado,
                            onCheckedChange = geradorViewModel::setFiltroParesImparesAtivado,
                            minInput = minParesInput,
                            onMinInputChange = geradorViewModel::setMinParesInput,
                            maxInput = maxParesInput,
                            onMaxInputChange = geradorViewModel::setMaxParesInput,
                            labelMin = stringResource(R.string.filtro_min_impares_label),
                            labelMax = stringResource(R.string.filtro_max_impares_label)
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_soma_total_titulo),
                            subtitulo = stringResource(R.string.filtro_soma_total_subtitulo),
                            checado = filtroSomaTotalAtivado,
                            onCheckedChange = geradorViewModel::setFiltroSomaTotalAtivado,
                            minInput = minSomaInput,
                            onMinInputChange = geradorViewModel::setMinSomaInput,
                            maxInput = maxSomaInput,
                            onMaxInputChange = geradorViewModel::setMaxSomaInput,
                            labelMin = stringResource(R.string.filtro_soma_min_label),
                            labelMax = stringResource(R.string.filtro_soma_max_label)
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_primos_titulo),
                            subtitulo = stringResource(R.string.filtro_primos_subtitulo),
                            checado = filtroPrimosAtivado,
                            onCheckedChange = geradorViewModel::setFiltroPrimosAtivado,
                            minInput = minPrimosInput,
                            onMinInputChange = geradorViewModel::setMinPrimosInput,
                            maxInput = maxPrimosInput,
                            onMaxInputChange = geradorViewModel::setMaxPrimosInput,
                            labelMin = stringResource(R.string.filtro_min_primos_label),
                            labelMax = stringResource(R.string.filtro_max_primos_label)
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_fibonacci_titulo),
                            subtitulo = stringResource(R.string.filtro_fibonacci_subtitulo),
                            checado = filtroFibonacciAtivado,
                            onCheckedChange = geradorViewModel::setFiltroFibonacciAtivado,
                            minInput = minFibonacciInput,
                            onMinInputChange = geradorViewModel::setMinFibonacciInput,
                            maxInput = maxFibonacciInput,
                            onMaxInputChange = geradorViewModel::setMaxFibonacciInput,
                            labelMin = stringResource(R.string.filtro_min_fibonacci_label),
                            labelMax = stringResource(R.string.filtro_max_fibonacci_label)
                        )
                        
                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_miolo_moldura_titulo),
                            subtitulo = stringResource(R.string.filtro_miolo_moldura_subtitulo),
                            checado = filtroMioloMolduraAtivado,
                            onCheckedChange = geradorViewModel::setFiltroMioloMolduraAtivado,
                            minInput = minMioloInput,
                            onMinInputChange = geradorViewModel::setMinMioloInput,
                            maxInput = maxMioloInput,
                            onMaxInputChange = geradorViewModel::setMaxMioloInput,
                            labelMin = stringResource(R.string.filtro_min_miolo_label),
                            labelMax = stringResource(R.string.filtro_max_miolo_label)
                        )

                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_multiplos_de_tres_titulo),
                            subtitulo = stringResource(R.string.filtro_multiplos_de_tres_subtitulo),
                            checado = filtroMultiplosDeTresAtivado,
                            onCheckedChange = geradorViewModel::setFiltroMultiplosDeTresAtivado,
                            minInput = minMultiplosDeTresInput,
                            onMinInputChange = geradorViewModel::setMinMultiplosDeTresInput,
                            maxInput = maxMultiplosDeTresInput,
                            onMaxInputChange = geradorViewModel::setMaxMultiplosDeTresInput,
                            labelMin = stringResource(R.string.filtro_min_multiplos_de_tres_label),
                            labelMax = stringResource(R.string.filtro_max_multiplos_de_tres_label)
                        )

                        // Novo Filtro: Repetição de Dezenas do Concurso Anterior
                        FiltroCardMinMax(
                            titulo = stringResource(R.string.filtro_repeticao_dezenas_titulo),
                            subtitulo = stringResource(R.string.filtro_repeticao_dezenas_subtitulo),
                            checado = filtroRepeticaoDezenasAtivado,
                            onCheckedChange = geradorViewModel::setFiltroRepeticaoDezenasAtivado,
                            minInput = minRepeticaoInput,
                            onMinInputChange = geradorViewModel::setMinRepeticaoInput,
                            maxInput = maxRepeticaoInput,
                            onMaxInputChange = geradorViewModel::setMaxRepeticaoInput,
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

                        mensagem?.let { msg ->
                                Text(
                                        text = msg,
                                        color =
                                                if (operacaoStatus == OperacaoStatus.ERRO)
                                                        MaterialTheme.colorScheme.error
                                                else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                )
                        }

                        // Botão dinâmico para geração de jogos
                        BotaoGerarJogos(
                            status = operacaoStatus,
                            quantidadeJogos = geradorViewModel.quantidadeJogos,
                            quantidadeNumeros = geradorViewModel.quantidadeNumeros,
                            filtrosAtivos = filtroParesImparesAtivado || filtroSomaTotalAtivado || 
                                filtroPrimosAtivado || filtroFibonacciAtivado || 
                                filtroMioloMolduraAtivado || filtroMultiplosDeTresAtivado ||
                                filtroRepeticaoDezenasAtivado,
                            dezenasFixas = geradorViewModel.numerosFixosState.collectAsState().value,
                            onGerarClick = { geradorViewModel.gerarJogos() }
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
