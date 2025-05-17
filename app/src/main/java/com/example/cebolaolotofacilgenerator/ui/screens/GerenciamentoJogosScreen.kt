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
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cebolaolotofacilgenerator.Screen
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState

enum class TipoListaJogo {
    TODOS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciamentoJogosScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val jogosViewModel = mainViewModel.jogosViewModel
    val context = LocalContext.current

    val jogosParaExibir by jogosViewModel.todosOsJogos.collectAsState()

    var showDialogExcluirJogo by remember { mutableStateOf<Jogo?>(null) }
    var showDialogLimparTodos by remember { mutableStateOf(false) }
    var showDialogDetalhesJogo by remember { mutableStateOf<Jogo?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        mainViewModel.snackbarMessage.collectLatest { message ->
            if (message.isNotEmpty()) {
                snackbarHostState.showSnackbar(message = message)
            }
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
            if (jogosParaExibir.isEmpty()) {
                val mensagem = stringResource(R.string.sem_jogos_para_mostrar)
                val icone = Icons.Outlined.SearchOff
                val textoBotao = "Gerar Novos Jogos"
                val acaoBotao = { navController.navigate(Screen.Gerador.createRoute()) }

                EmptyStatePlaceholder(
                    mensagem = mensagem,
                    icone = icone,
                    textoBotao = textoBotao,
                    onAcaoBotaoClick = acaoBotao
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(jogosParaExibir, key = { jogo: Jogo -> jogo.id }) { jogo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    showDialogExcluirJogo = jogo
                                    return@rememberSwipeToDismissBoxState true
                                }
                                return@rememberSwipeToDismissBoxState false
                            },
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Filled.DeleteSweep,
                                        contentDescription = stringResource(R.string.excluir_jogo),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        ) {
                            JogoGerenciamentoItem(
                                jogo = jogo,
                                onFavoritoClick = { favorito -> jogosViewModel.marcarComoFavorito(jogo, favorito) },
                                onExcluirClick = { showDialogExcluirJogo = jogo },
                                onJogoClick = { showDialogDetalhesJogo = jogo }
                            )
                        }
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
            text = { Text(stringResource(R.string.mensagem_confirmar_exclusao_jogo, jogoParaExcluir.getNumerosFormatados())) },
            confirmButton = {
                Button(
                    onClick = {
                        jogosViewModel.excluirJogo(jogoParaExcluir)
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
                        jogosViewModel.limparTodosOsJogos()
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
            append(stringResource(R.string.numeros_jogados, jogoParaDetalhes.getNumerosFormatados()))
            append("\n\n")
            append(stringResource(R.string.jogo_data_criacao, dateFormat.format(jogoParaDetalhes.dataCriacao)))
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJogoClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = jogo.getNumerosFormatados(),
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
            val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePlaceholderPreview() {
    CebolaoLotofacilGeneratorTheme {
        EmptyStatePlaceholder(
            mensagem = "Nenhum jogo encontrado.",
            icone = Icons.Outlined.SearchOff,
            textoBotao = "Gerar Jogos",
            onAcaoBotaoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JogoGerenciamentoItemPreview() {
    val sampleJogo = Jogo.fromList(
        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
    )
    // Defina um sampleResultado aqui se necessário para o preview, ou passe null
    // Exemplo: val sampleResultado: Resultado? = null
    // Ou crie um Resultado de amostra:
    // val sampleResultado = Resultado(id = 1, concurso = 100, data = Date(), dezenas = listOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15), ...)

    CebolaoLotofacilGeneratorTheme {
        JogoGerenciamentoItem(
            jogo = sampleJogo,
            onFavoritoClick = {},
            onExcluirClick = {},
            onJogoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewGerenciamentoJogosScreen() {
    CebolaoLotofacilGeneratorTheme {
        Text("Preview desabilitada: GerenciamentoJogosScreen requer ViewModels")
    }
}

@Composable
fun EmptyStatePlaceholder(
    mensagem: String,
    icone: ImageVector,
    modifier: Modifier = Modifier,
    textoBotao: String? = null,
    onAcaoBotaoClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
                text = mensagem,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (textoBotao != null && onAcaoBotaoClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onAcaoBotaoClick) {
                    Text(textoBotao)
                }
            }
        }
    }
}