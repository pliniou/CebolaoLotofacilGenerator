package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.viewmodel.JogosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import kotlinx.coroutines.flow.collectLatest

enum class TipoListaJogo {
    TODOS,
    FAVORITOS,
    CONFERIDOS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciamentoJogosScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val jogosViewModel = mainViewModel.jogosViewModel // Acessar via MainViewModel

    val jogosSalvos by jogosViewModel.jogosSalvos.collectAsState(initial = emptyList())
    val operacaoStatus by jogosViewModel.operacaoStatus.observeAsState()
    val snackbarMessage by jogosViewModel.snackbarMessage.collectAsState(initial = null)

    val context = LocalContext.current
    var tipoListaSelecionada by remember { mutableStateOf(TipoListaJogo.TODOS) }

    val todosJogos by jogosViewModel.todosJogos.observeAsState(emptyList())
    val jogosFavoritos by jogosViewModel.jogosFavoritos.observeAsState(emptyList())
    val jogosConferidos by jogosViewModel.jogosConferidos.observeAsState(emptyList())

    val jogosParaExibir = when (tipoListaSelecionada) {
        TipoListaJogo.TODOS -> todosJogos
        TipoListaJogo.FAVORITOS -> jogosFavoritos
        TipoListaJogo.CONFERIDOS -> jogosConferidos
    }

    var showDialogExcluirJogo by remember { mutableStateOf<Jogo?>(null) }
    var showDialogLimparTodos by remember { mutableStateOf(false) }
    var showDialogDetalhesJogo by remember { mutableStateOf<Jogo?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        mainViewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_tela_gerenciamento)) },
                actions = {
                    IconButton(onClick = { showDialogLimparTodos = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.limpar_todos))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = tipoListaSelecionada.ordinal) {
                TipoListaJogo.values().forEach { tipo ->
                    Tab(
                        selected = tipoListaSelecionada == tipo,
                        onClick = { tipoListaSelecionada = tipo },
                        text = {
                            Text(
                                when (tipo) {
                                    TipoListaJogo.TODOS -> stringResource(R.string.todos)
                                    TipoListaJogo.FAVORITOS -> stringResource(R.string.favoritos)
                                    TipoListaJogo.CONFERIDOS -> stringResource(R.string.conferidos)
                                }
                            )
                        }
                    )
                }
            }

            if (jogosParaExibir.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.nenhum_jogo_encontrado), textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(jogosParaExibir) { jogo ->
                        JogoGerenciamentoItem(
                            jogo = jogo,
                            onFavoritoClick = { favorito -> jogosViewModel.marcarComoFavorito(jogo, favorito) },
                            onExcluirClick = { showDialogExcluirJogo = jogo },
                            onJogoClick = { showDialogDetalhesJogo = jogo }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    if (showDialogExcluirJogo != null) {
        val jogoParaExcluir = showDialogExcluirJogo!!
        AlertDialog(
            onDismissRequest = { showDialogExcluirJogo = null },
            title = { Text(stringResource(R.string.confirmar_exclusao_titulo)) },
            text = { Text(stringResource(R.string.mensagem_confirmar_exclusao_jogo, jogoParaExcluir.numeros.joinToString(" - "))) },
            confirmButton = {
                Button(
                    onClick = {
                        jogosViewModel.deletarJogo(jogoParaExcluir)
                        mainViewModel.showSnackbar(context.getString(R.string.jogo_excluido))
                        showDialogExcluirJogo = null
                    }
                ) { Text(stringResource(R.string.excluir_jogo)) }
            },
            dismissButton = {
                Button(onClick = { showDialogExcluirJogo = null }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }

    if (showDialogLimparTodos) {
        AlertDialog(
            onDismissRequest = { showDialogLimparTodos = false },
            title = { Text(stringResource(R.string.confirmar_limpar_todos)) },
            text = { Text(stringResource(R.string.mensagem_confirmar_limpar_todos)) },
            confirmButton = {
                Button(
                    onClick = {
                        jogosViewModel.limparTodosJogos()
                        mainViewModel.showSnackbar(context.getString(R.string.todos_jogos_excluidos))
                        showDialogLimparTodos = false
                    }
                ) { Text(stringResource(R.string.sim)) }
            },
            dismissButton = {
                Button(onClick = { showDialogLimparTodos = false }) { Text(stringResource(R.string.nao)) }
            }
        )
    }
    
    if (showDialogDetalhesJogo != null) {
        val jogoParaDetalhes = showDialogDetalhesJogo!!
        val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
        val detalhesText = buildString {
            append(stringResource(R.string.numeros_jogados, jogoParaDetalhes.numeros.joinToString(" - ")))
            append("\n\n")
            append(stringResource(R.string.data_geracao, dateFormat.format(jogoParaDetalhes.dataCriacao)))
            append("\n\n")
            append(stringResource(R.string.caracteristicas_jogo))
            append("\n")
            append(stringResource(R.string.pares_impares, jogoParaDetalhes.quantidadePares, jogoParaDetalhes.quantidadeImpares))
            append("\n")
            append(stringResource(R.string.soma_total, jogoParaDetalhes.soma))
            append("\n")
            append(stringResource(R.string.numeros_primos, jogoParaDetalhes.quantidadePrimos))
            append("\n")
            append(stringResource(R.string.numeros_fibonacci, jogoParaDetalhes.quantidadeFibonacci))
            append("\n")
            append(stringResource(R.string.miolo_moldura, jogoParaDetalhes.quantidadeMiolo, jogoParaDetalhes.quantidadeMoldura))
            append("\n")
            append(stringResource(R.string.multiplos_tres, jogoParaDetalhes.quantidadeMultiplosDeTres))
            if (jogoParaDetalhes.acertos != null) {
                append("\n\n")
                append(stringResource(R.string.resultado_conferencia, jogoParaDetalhes.acertos!!))
            }
        }

        AlertDialog(
            onDismissRequest = { showDialogDetalhesJogo = null },
            title = { Text(stringResource(R.string.detalhes_do_jogo)) },
            text = { ScrollableText(text = detalhesText) },
            confirmButton = {
                Button(onClick = { showDialogDetalhesJogo = null }) { Text(stringResource(R.string.fechar)) }
            }
        )
    }
}

@Composable
fun ScrollableText(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.heightIn(max = 200.dp)) {
        val scrollState = rememberScrollState()
        Text(
            text = text,
            modifier = Modifier.verticalScroll(scrollState)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JogoGerenciamentoItem(
    jogo: Jogo,
    onFavoritoClick: (Boolean) -> Unit,
    onExcluirClick: () -> Unit,
    onJogoClick: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJogoClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = jogo.numeros.joinToString(" - "),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onFavoritoClick(!jogo.favorito) }) {
                    Icon(
                        imageVector = if (jogo.favorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(if (jogo.favorito) R.string.desmarcar_como_favorito else R.string.marcar_como_favorito),
                        tint = if (jogo.favorito) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
                IconButton(onClick = onExcluirClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.excluir_jogo),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.jogo_data_criacao, dateFormat.format(jogo.dataCriacao)),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.jogo_caracteristicas_compacto,
                    jogo.quantidadePares,
                    jogo.quantidadeImpares,
                    jogo.soma,
                    jogo.quantidadePrimos,
                    jogo.quantidadeFibonacci
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            if (jogo.acertos != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.jogo_acertos, jogo.acertos!!),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Gerenciamento Jogos Screen - Vazia")
@Composable
fun PreviewGerenciamentoJogosScreenEmpty() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val mockMainViewModel = MainViewModel(
        application = application,
        jogoRepository = JogoRepository(AppDatabase.getDatabase(application).jogoDao()),
        resultadoRepository = ResultadoRepository(AppDatabase.getDatabase(application).resultadoDao()),
        appDataStore = AppDataStore(application)
    )
    GerenciamentoJogosScreen(mainViewModel = mockMainViewModel, navController = NavHostController(context))
}

@Preview(showBackground = true, name = "Gerenciamento Jogos Screen - Com Jogos")
@Composable
fun PreviewGerenciamentoJogosScreenWithData() {
    val mockMainViewModel = MainViewModel(Application())
    // Mock JogosViewModel com dados
    val mockJogosViewModel = JogosViewModel(Application()) // Para preview, idealmente injetar um mock com LiveData populado

    // Exemplo de como popular LiveData para preview (requer acesso ao LiveData no ViewModel)
    // LifecycleOwner é necessário para observar LiveData, complicado em previews. 
    // Uma forma mais simples é ter ViewModels que aceitem dados iniciais para preview.

    GerenciamentoJogosScreen(mainViewModel = mockMainViewModel, navController = NavHostController())
}

@Preview(showBackground = true, name = "JogoGerenciamentoItem")
@Composable
fun PreviewJogoGerenciamentoItem() {
    val jogo = Jogo(
        id = 1,
        numeros = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
        dataCriacao = Date(),
        favorito = true,
        acertos = 14,
        quantidadePares = 7, quantidadeImpares = 8, soma = 180, quantidadePrimos = 6, quantidadeFibonacci = 5, quantidadeMiolo = 5, quantidadeMoldura = 10, quantidadeMultiplosDeTres = 5
    )
    JogoGerenciamentoItem(jogo = jogo, onFavoritoClick = {}, onExcluirClick = {}, onJogoClick = {})
}

// TODO: Verificar se todas as strings usadas já existem em strings.xml:
// R.string.titulo_tela_gerenciamento, R.string.limpar_todos, R.string.todos, R.string.favoritos,
// R.string.conferidos, R.string.nenhum_jogo_encontrado, R.string.confirmar_exclusao_titulo,
// R.string.mensagem_confirmar_exclusao_jogo, R.string.excluir_jogo, R.string.cancelar,
// R.string.confirmar_limpar_todos, R.string.mensagem_confirmar_limpar_todos, R.string.sim, R.string.nao,
// R.string.jogo_excluido, R.string.todos_jogos_excluidos, R.string.detalhes_do_jogo,
// R.string.numeros_jogados, R.string.data_geracao, R.string.caracteristicas_jogo,
// R.string.pares_impares, R.string.soma_total, R.string.numeros_primos, R.string.numeros_fibonacci,
// R.string.miolo_moldura, R.string.multiplos_tres, R.string.resultado_conferencia, R.string.fechar,
// R.string.desmarcar_como_favorito, R.string.marcar_como_favorito, R.string.jogo_data_criacao,
// R.string.jogo_caracteristicas_compacto, R.string.jogo_acertos

@Preview(showBackground = true)
@Composable
fun DefaultPreviewGerenciamentoJogosScreen() {
    // GerenciamentoJogosScreen() // Comentado pois requer ViewModels
    CebolaoLotofacilGeneratorTheme {
        Text("Preview desabilitada: GerenciamentoJogosScreen requer ViewModels")
    }
} 