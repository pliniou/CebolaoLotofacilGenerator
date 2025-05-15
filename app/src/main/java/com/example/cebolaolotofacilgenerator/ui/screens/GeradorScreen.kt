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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp // Adicionado import para dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.Screen // Import para Screen
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus // Garantir que este é o
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview
import com.example.cebolaolotofacilgenerator.ui.components.BotaoGerarJogos
import com.example.cebolaolotofacilgenerator.ui.components.SnackbarManager

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class) // Adicionar OptIn aqui e FlowPreview
@Composable
fun GeradorScreen(
        navController: NavController,
        dezenasFixasArg: String?, // Recebe as dezenas como String da navegação
        viewModel: MainViewModel // Novo parâmetro para acessar o último resultado
) {
        val geradorViewModel: GeradorViewModel = viewModel()

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

        // Novos StateFlows para inputs dos filtros restantes
        val minPrimosInput by geradorViewModel.minPrimosInput.collectAsState()
        val maxPrimosInput by geradorViewModel.maxPrimosInput.collectAsState()
        val minFibonacciInput by geradorViewModel.minFibonacciInput.collectAsState()
        val maxFibonacciInput by geradorViewModel.maxFibonacciInput.collectAsState()
        val minMioloInput by geradorViewModel.minMioloInput.collectAsState()
        val maxMioloInput by geradorViewModel.maxMioloInput.collectAsState()
        val minMultiplosDeTresInput by geradorViewModel.minMultiplosDeTresInput.collectAsState()
        val maxMultiplosDeTresInput by geradorViewModel.maxMultiplosDeTresInput.collectAsState()

        // Observar o status da operação e os jogos gerados (se necessário aqui)
        val operacaoStatus by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
        val mensagem by geradorViewModel.mensagem.observeAsState("")
        val quantidadeJogosInput by geradorViewModel.quantidadeJogosInput.collectAsState()
        val quantidadeNumerosInput by geradorViewModel.quantidadeNumerosInput.collectAsState()

        // Coletar estados das dezenas fixas e excluídas
        val numerosFixos by geradorViewModel.numerosFixosState.collectAsState()
        val numerosExcluidos by geradorViewModel.numerosExcluidosState.collectAsState()

        // Novo: Observar o último resultado
        val ultimoResultado by viewModel.ultimoResultado.collectAsState()

        // Estado local para dezenas selecionadas do último resultado
        val (dezenasSelecionadas, setDezenasSelecionadas) =
                androidx.compose.runtime.remember {
                        androidx.compose.runtime.mutableStateOf(mutableSetOf<Int>())
                }
        val (concurso, setConcurso) =
                androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        val (data, setData) =
                androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

        val context = androidx.compose.ui.platform.LocalContext.current

        LaunchedEffect(dezenasFixasArg) {
                val dezenas = dezenasFixasArg?.split(",")?.mapNotNull { it.toIntOrNull() }
                geradorViewModel.inicializarComNumerosFixos(dezenas)
        }

        // Observar mensagens para exibir Snackbars
        LaunchedEffect(mensagem) {
                if (!mensagem.isNullOrEmpty()) {
                        SnackbarManager.mostrarMensagem(mensagem)
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
                        com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.SUCESSO -> {
                                navController.navigate(Screen.JogosGerados.route)
                                geradorViewModel.resetarStatusOperacao()
                        }
                        com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.ERRO -> {
                                geradorViewModel.resetarStatusOperacao()
                        }
                        com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
                                .CARREGANDO -> {
                                // Já tratado pelo CircularProgressIndicator
                        }
                        com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.OCIOSO -> {
                                // Nada a fazer
                        }
                        null -> {
                                // Estado inicial, nada a fazer
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
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Voltar"
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
                                                viewModel.salvarUltimoResultado(
                                                        dezenasSelecionadas.toList()
                                                )
                                                viewModel.showSnackbar("Último resultado salvo com ${dezenasSelecionadas.size} dezenas!")
                                        } else {
                                                viewModel.showSnackbar("Selecione exatamente 15 dezenas para o resultado.")
                                        }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                enabled = dezenasSelecionadas.size == 15
                        ) { Text("Salvar Último Resultado") }
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                                "Filtros Estatísticos:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                        )

                        // Conteúdo dos filtros (anteriormente em FiltrosScreen)
                        // Filtro Pares e Ímpares
                        FiltroSwitchItem(
                                titulo = "Filtrar Pares/Ímpares",
                                subtitulo = "Definir quantidades de números pares e ímpares.",
                                checado = filtroParesImparesAtivado,
                                onCheckedChange = geradorViewModel::setFiltroParesImparesAtivado
                        )
                        if (filtroParesImparesAtivado) {
                                Column(
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedTextField(
                                                value = minParesInput,
                                                onValueChange = {
                                                        geradorViewModel.setMinParesInput(it)
                                                },
                                                label = { Text("Mínimo de Pares") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxParesInput,
                                                onValueChange = {
                                                        geradorViewModel.setMaxParesInput(it)
                                                },
                                                label = { Text("Máximo de Pares") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                }
                        }

                        // Filtro Soma Total
                        FiltroSwitchItem(
                                titulo = "Filtrar por Soma Total",
                                subtitulo = "Definir faixa para a soma das dezenas.",
                                checado = filtroSomaTotalAtivado,
                                onCheckedChange = geradorViewModel::setFiltroSomaTotalAtivado
                        )
                        if (filtroSomaTotalAtivado) {
                                Column(
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedTextField(
                                                value = minSomaInput,
                                                onValueChange = {
                                                        geradorViewModel.setMinSomaInput(it)
                                                },
                                                label = { Text("Soma Mínima") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxSomaInput,
                                                onValueChange = {
                                                        geradorViewModel.setMaxSomaInput(it)
                                                },
                                                label = { Text("Soma Máxima") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                }
                        }

                        // Filtro Números Primos
                        FiltroSwitchItem(
                                titulo = "Filtrar por Números Primos",
                                subtitulo = "Definir quantidade de números primos.",
                                checado = filtroPrimosAtivado,
                                onCheckedChange = geradorViewModel::setFiltroPrimosAtivado
                        )
                        if (filtroPrimosAtivado) {
                                Column(
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedTextField(
                                                value = minPrimosInput,
                                                onValueChange = {
                                                        geradorViewModel.setMinPrimosInput(it)
                                                },
                                                label = { Text("Mín. Primos") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxPrimosInput,
                                                onValueChange = {
                                                        geradorViewModel.setMaxPrimosInput(it)
                                                },
                                                label = { Text("Máx. Primos") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                }
                        }

                        // Filtro Números de Fibonacci
                        FiltroSwitchItem(
                                titulo = "Filtrar por Números de Fibonacci",
                                subtitulo = "Definir quantidade de números de Fibonacci.",
                                checado = filtroFibonacciAtivado,
                                onCheckedChange = geradorViewModel::setFiltroFibonacciAtivado
                        )
                        if (filtroFibonacciAtivado) {
                                Column(
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedTextField(
                                                value = minFibonacciInput,
                                                onValueChange = {
                                                        geradorViewModel.setMinFibonacciInput(it)
                                                },
                                                label = { Text("Mín. Fibonacci") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxFibonacciInput,
                                                onValueChange = {
                                                        geradorViewModel.setMaxFibonacciInput(it)
                                                },
                                                label = { Text("Máx. Fibonacci") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                }
                        }

                        // Filtro Miolo e Moldura
                        FiltroSwitchItem(
                                titulo = "Filtrar por Miolo e Moldura",
                                subtitulo = "Definir balanceamento entre miolo e moldura.",
                                checado = filtroMioloMolduraAtivado,
                                onCheckedChange = geradorViewModel::setFiltroMioloMolduraAtivado
                        )
                        if (filtroMioloMolduraAtivado) {
                                Column(
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedTextField(
                                                value = minMioloInput,
                                                onValueChange = {
                                                        geradorViewModel.setMinMioloInput(it)
                                                },
                                                label = { Text("Mín. Dezenas Miolo") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxMioloInput,
                                                onValueChange = {
                                                        geradorViewModel.setMaxMioloInput(it)
                                                },
                                                label = { Text("Máx. Dezenas Miolo") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                }
                        }

                        // Filtro Múltiplos de 3
                        FiltroSwitchItem(
                                titulo = "Filtrar por Múltiplos de 3",
                                subtitulo = "Definir quantidade de múltiplos de 3.",
                                checado = filtroMultiplosDeTresAtivado,
                                onCheckedChange = geradorViewModel::setFiltroMultiplosDeTresAtivado
                        )
                        if (filtroMultiplosDeTresAtivado) {
                                Column(
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedTextField(
                                                value = minMultiplosDeTresInput,
                                                onValueChange = {
                                                        geradorViewModel.setMinMultiplosDeTresInput(
                                                                it
                                                        )
                                                },
                                                label = { Text("Mín. Múltiplos de 3") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                        OutlinedTextField(
                                                value = maxMultiplosDeTresInput,
                                                onValueChange = {
                                                        geradorViewModel.setMaxMultiplosDeTresInput(
                                                                it
                                                        )
                                                },
                                                label = { Text("Máx. Múltiplos de 3") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp)) // Espaço antes do botão

                        // TODO: Exibir status de carregamento do geradorViewModel.operacaoStatus
                        if (operacaoStatus ==
                                        com.example.cebolaolotofacilgenerator.data.model
                                                .OperacaoStatus.CARREGANDO
                        ) {
                                CircularProgressIndicator(
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )
                        }

                        mensagem?.let { msg ->
                                Text(
                                        text = msg,
                                        color =
                                                if (operacaoStatus ==
                                                                com.example
                                                                        .cebolaolotofacilgenerator
                                                                        .data.model.OperacaoStatus
                                                                        .ERRO
                                                )
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
                                filtroMioloMolduraAtivado || filtroMultiplosDeTresAtivado,
                            dezenasFixas = geradorViewModel.numerosFixosState.collectAsState().value,
                            onGerarClick = { geradorViewModel.gerarJogos() }
                        )

                        // TODO: Exibir mensagens de erro/sucesso do geradorViewModel.mensagem
                        // TODO: Exibir status de carregamento do geradorViewModel.operacaoStatus
                        // (parcialmente feito acima)
                }
        }
}

// Composable FiltroSwitchItem (copiado de FiltrosScreen.kt)
@Composable
fun FiltroSwitchItem(
        titulo: String,
        subtitulo: String,
        checado: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
        Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(titulo, style = MaterialTheme.typography.bodyLarge)
                                Text(subtitulo, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = checado, onCheckedChange = onCheckedChange)
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
                                // .height((5 * 56).dp) // Altura aproximada para 5 linhas de botões
                                // com padding
                                // A altura dinâmica pode ser complicada com LazyVerticalGrid dentro
                                // de um VerticalScroll
                                // Por enquanto, deixaremos a altura ser determinada pelo conteúdo.
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
