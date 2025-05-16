package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Style
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
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.JogoViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModelFactory
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.Screen
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as Application

    // 1. Criar ResultadoRepository
    val resultadoRepository = remember { ResultadoRepository(AppDatabase.getDatabase(application).resultadoDao()) }

    // 2. Criar FiltrosViewModelFactory e 3. Instanciar filtrosViewModel
    val filtrosViewModel: FiltrosViewModel = viewModel(
        factory = FiltrosViewModelFactory(application, resultadoRepository)
    )

    // 4. Criar JogoRepository (já estava lá, mas movido para clareza de ordem)
    val jogoRepository = remember { JogoRepository(AppDatabase.getDatabase(application).jogoDao()) }
    
    // 5. Criar GeradorViewModelFactory e 6. Instanciar geradorViewModel
    val geradorViewModel: GeradorViewModel = viewModel(
        factory = GeradorViewModelFactory(application, filtrosViewModel, jogoRepository)
    )
    
    val jogoViewModel: JogoViewModel = viewModel() 

    val jogosGerados by geradorViewModel.jogosGerados.observeAsState(emptyList())
    val operacaoStatusGerador by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
    val mensagemGerador by geradorViewModel.mensagem.observeAsState()

    val operacaoStatusJogo by jogoViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)

    // Coletar o valor de ultimoResultado
    val ultimoResultado by mainViewModel.ultimoResultado.collectAsState()

    LaunchedEffect(mensagemGerador) {
        mensagemGerador?.let {
            if (operacaoStatusGerador == OperacaoStatus.ERRO) {
                mainViewModel.showSnackbar(it)
                geradorViewModel.limparMensagemUnica()
            }
        }
    }

    LaunchedEffect(operacaoStatusJogo) {
        when (operacaoStatusJogo) {
            OperacaoStatus.SUCESSO -> {
                // mainViewModel.showSnackbar(context.getString(R.string.jogos_salvos_com_sucesso)) // Já é mostrado pelo Fragment/Activity
                jogoViewModel.resetarStatus()
            }
            OperacaoStatus.ERRO -> {
                mainViewModel.showSnackbar(context.getString(R.string.erro_salvar_jogos))
                jogoViewModel.resetarStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Gerador.createRoute()) }) {
                Icon(Icons.Filled.AddCircle, contentDescription = stringResource(id = R.string.gerar_jogos))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.instrucao_gerador_jogos),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = R.string.resumo_jogos_e_resultados), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Style, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp).padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = stringResource(id = R.string.jogos_salvos_contagem, jogosGerados?.size ?: 0))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp).padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ultimoResultado?.let { resultado ->
                            Text(text = stringResource(id = R.string.resultado_concurso, resultado.id))
                        } ?: Text(text = stringResource(id = R.string.nenhum_resultado_salvo))
                    }
                }
            }

            PrincipalScreenButton(
                text = stringResource(id = R.string.gerador_de_jogos),
                icon = Icons.Filled.AddCircle,
                onClick = { navController.navigate(Screen.Gerador.createRoute()) },
                isPrimary = true
            )
            PrincipalScreenButton(
                text = stringResource(id = R.string.conferir_jogos),
                icon = Icons.Filled.CheckCircle,
                onClick = { navController.navigate(Screen.Conferencia.route) },
                isPrimary = false
            )
            PrincipalScreenButton(
                text = stringResource(id = R.string.gerenciar_jogos_salvos),
                icon = Icons.Filled.List,
                onClick = { navController.navigate(Screen.JogosGerados.route) },
                isPrimary = false
            )
            PrincipalScreenButton(
                text = stringResource(id = R.string.configuracoes_app),
                icon = Icons.Filled.Settings,
                onClick = { navController.navigate(Screen.Settings.route) },
                isPrimary = false
            )
        }
    }
}

@Composable
fun PrincipalScreenButton(text: String, icon: ImageVector, onClick: () -> Unit, isPrimary: Boolean) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text)
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text)
            }
        }
    }
}

@Composable
fun JogoItem(
    jogo: Jogo,
    onFavoritoClick: (Boolean) -> Unit,
    onJogoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onJogoClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jogo.numeros.joinToString(" - "),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.caracteristicas_jogo_format,
                        jogo.quantidadePares,
                        jogo.quantidadeImpares,
                        jogo.soma,
                        jogo.quantidadePrimos,
                        jogo.quantidadeFibonacci,
                        jogo.quantidadeMiolo,
                        jogo.quantidadeMoldura,
                        jogo.quantidadeMultiplosDeTres
                    ),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            IconButton(onClick = { onFavoritoClick(!jogo.favorito) }) {
                Icon(
                    imageVector = if (jogo.favorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.marcar_como_favorito),
                    tint = if (jogo.favorito) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewPrincipalScreen() {
    CebolaoLotofacilGeneratorTheme {
        Text("Preview PrincipalScreen")
    }
}

@Preview(showBackground = true, name = "Tela Principal - Vazia")
@Composable
fun PreviewPrincipalScreenEmpty() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    // Instanciação completa para MainViewModel no Preview
    val mockMainViewModel = MainViewModel(
        application = application,
        jogoRepository = JogoRepository(AppDatabase.getDatabase(application).jogoDao()),
        resultadoRepository = ResultadoRepository(AppDatabase.getDatabase(application).resultadoDao()),
        appDataStore = AppDataStore(application)
    )
    PrincipalScreen(navController = NavController(context), mainViewModel = mockMainViewModel)
}

@Preview(showBackground = true, name = "Tela Principal - Com Jogos")
@Composable
fun PreviewPrincipalScreenWithGames() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val mockMainViewModel = MainViewModel(
        application = application,
        jogoRepository = JogoRepository(AppDatabase.getDatabase(application).jogoDao()),
        resultadoRepository = ResultadoRepository(AppDatabase.getDatabase(application).resultadoDao()),
        appDataStore = AppDataStore(application)
    )
    // GeradorViewModel e JogoViewModel são instanciados dentro de PrincipalScreen
    PrincipalScreen(
        navController = NavController(context),
        mainViewModel = mockMainViewModel
    )
}

@Preview(showBackground = true, name = "Item de Jogo")
@Composable
fun PreviewJogoItem() {
    val jogo = Jogo(
        id = 1,
        numeros = listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 24, 25, 2),
        soma = 190,
        quantidadePares = 2,
        quantidadeImpares = 13,
        quantidadePrimos = 8,
        quantidadeFibonacci = 7,
        quantidadeMiolo = 5,
        quantidadeMoldura = 10,
        quantidadeMultiplosDeTres = 5,
        favorito = true,
        dataCriacao = Date()
    )
    JogoItem(jogo = jogo, onFavoritoClick = {}, onJogoClick = {})
}

@Preview(showBackground = true, name = "Tela Principal - Vazia (Simples)")
@Composable
fun PreviewPrincipalScreenEmptySimple() {
    MaterialTheme {
        Column { Text("Preview Conteúdo Principal Vazio") }
    }
}

@Preview(showBackground = true, name = "Tela Principal - Com Jogos (Simples)")
@Composable
fun PreviewPrincipalScreenWithGamesSimple() {
    MaterialTheme {
        val sampleJogos = listOf(
            Jogo.fromList(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)),
            Jogo.fromList(listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16))
        )
        LazyColumn {
            items(sampleJogos) { jogo ->
                JogoItem(jogo = jogo, onFavoritoClick = {}, onJogoClick = {})
            }
        }
    }
}

// Corrigir MockMainViewModel para fornecer todas as dependências
class MockMainViewModel(application: Application) : MainViewModel(
    application = application,
    jogoRepository = JogoRepository(AppDatabase.getDatabase(application).jogoDao()),
    resultadoRepository = ResultadoRepository(AppDatabase.getDatabase(application).resultadoDao()),
    appDataStore = AppDataStore(application)
)

@Preview(showBackground = true, name = "Tela Principal - Vazia (Com Mock VM)")
@Composable
fun PreviewPrincipalScreenEmptyWithMockVM() {
    val application = LocalContext.current.applicationContext as Application
    val mockMainViewModel = MockMainViewModel(application)
    PrincipalScreen(navController = NavController(LocalContext.current), mainViewModel = mockMainViewModel)
} 