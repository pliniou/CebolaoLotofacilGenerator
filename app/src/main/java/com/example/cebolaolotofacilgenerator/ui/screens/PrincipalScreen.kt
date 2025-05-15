package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.example.cebolaolotofacilgenerator.ui.viewmodel.FiltrosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    mainViewModel: MainViewModel,
    onNavigateToFiltros: () -> Unit
) {
    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as Application

    // Obter FiltrosViewModel primeiro, pois é dependência do GeradorViewModel
    val filtrosViewModel: FiltrosViewModel = viewModel()
    // Instanciar GeradorViewModel usando a Factory
    val geradorViewModel: GeradorViewModel = viewModel(
        factory = GeradorViewModelFactory(application, filtrosViewModel, null /* TODO: Fornecer JogoRepository se GeradorViewModel precisar dele ativamente */)
    )
    val jogoViewModel: JogoViewModel = viewModel() // Pode continuar assim se não houver dependências complexas

    val jogosGerados by geradorViewModel.jogosGerados.observeAsState(emptyList())
    val operacaoStatusGerador by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
    val mensagemGerador by geradorViewModel.mensagem.observeAsState()

    val operacaoStatusJogo by jogoViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)

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
                title = { Text(stringResource(id = R.string.app_name_short)) }, // Usando um título mais curto ou o nome do app
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.gerador_de_jogos),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.instrucao_gerador_jogos),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = onNavigateToFiltros,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.configurar_filtros))
                    }
                    Button(
                        onClick = { geradorViewModel.gerarJogos() },
                        enabled = operacaoStatusGerador != OperacaoStatus.CARREGANDO,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        if (operacaoStatusGerador == OperacaoStatus.CARREGANDO) {
                            Text(stringResource(R.string.gerando_jogos))
                        } else {
                            Text(stringResource(R.string.gerar_jogos))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ocupa o espaço restante
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
                    Text(
                        text = stringResource(R.string.jogos_gerados),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (operacaoStatusGerador == OperacaoStatus.CARREGANDO) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            CircularProgressIndicator()
                        }
                    } else if (jogosGerados.isNullOrEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                           Text(stringResource(R.string.nenhum_jogo_gerado_ainda))
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(jogosGerados) { jogo ->
                                JogoItem(
                                    jogo = jogo,
                                    onFavoritoClick = { fav ->
                                        jogoViewModel.marcarComoFavorito(jogo, fav)
                                    },
                                    onJogoClick = {
                                        mainViewModel.showSnackbar(context.getString(R.string.jogo_clicado_feedback, jogo.numeros.joinToString()))
                                    }
                                )
                                Divider()
                            }
                        }
                    }
                }
            }


            Button(
                onClick = {
                    if (jogosGerados.isNullOrEmpty()) {
                        mainViewModel.showSnackbar(context.getString(R.string.nenhum_jogo_para_salvar))
                    } else {
                        jogoViewModel.inserirJogos(jogosGerados)
                        mainViewModel.showSnackbar(context.getString(R.string.jogos_salvos_com_sucesso))
                    }
                },
                enabled = !jogosGerados.isNullOrEmpty() && operacaoStatusGerador != OperacaoStatus.CARREGANDO,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(stringResource(R.string.salvar_jogos))
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

// Preview (necessário adaptar ou remover se causar problemas de build sem config completa)
@Preview(showBackground = true, name = "Tela Principal - Vazia")
@Composable
fun PreviewPrincipalScreenEmpty() {
    val mockMainViewModel = MainViewModel(Application()) // Necessita de Application context
    PrincipalScreen(mainViewModel = mockMainViewModel, onNavigateToFiltros = {})
}

@Preview(showBackground = true, name = "Tela Principal - Com Jogos")
@Composable
fun PreviewPrincipalScreenWithGames() {
    // Mock ViewModels and data for preview
    val geradorViewModel: GeradorViewModel = viewModel()
    val jogoViewModel: JogoViewModel = viewModel()
    val mainViewModel: MainViewModel = MainViewModel(Application()) // Necessita de Application context

    // Mock data
    val sampleJogos = listOf(
        Jogo(id = 1, numeros = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), dataCriacao = Date(), favorito = true),
        Jogo(id = 2, numeros = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16), dataCriacao = Date(), favorito = false)
    )
    // Populando o LiveData (isso é mais complexo para Preview, idealmente o ViewModel teria como setar dados para preview)
    // Para simplificar, vamos assumir que o ViewModel pode ser instanciado com dados iniciais ou que usamos um mock mais elaborado.
    // Por ora, o preview de jogos pode não mostrar os jogos diretamente devido à forma como LiveData é observado.

    PrincipalScreen(
        mainViewModel = mainViewModel,
        geradorViewModel = geradorViewModel, // Idealmente, um mock com jogosGerados preenchido
        jogoViewModel = jogoViewModel,
        onNavigateToFiltros = {}
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

// Adicionar string resources que estão faltando
// No strings.xml:
// <string name="app_name_short">Lotofácil Gen</string>
// <string name="gerador_de_jogos">Gerador de Jogos</string>
// <string name="instrucao_gerador_jogos">Configure os filtros e gere seus jogos da Lotofácil.</string>
// <string name="configurar_filtros">Configurar Filtros</string>
// <string name="nenhum_jogo_gerado_ainda">Nenhum jogo gerado ainda.</string>
// <string name="jogo_clicado_feedback">Jogo clicado: %s</string>
// <string name="caracteristicas_jogo_format">P: %1$d | I: %2$d | Soma: %3$d\nPrimos: %4$d | Fib: %5$d\nMiolo: %6$d | Moldura: %7$d\nMúlt. 3: %8$d</string>

// Nota: O preview do MainViewModel requer um Application context, o que pode ser complicado
// em previews do Compose. Pode ser necessário um mock mais simples ou diferente para o MainViewModel nos previews.
// O uso de `viewModel()` nos previews também pode não funcionar como esperado fora de um NavHost real.
// Considere usar instâncias mocadas diretamente para previews.
// As strings marcadas acima precisam ser adicionadas ao seu arquivo strings.xml
// Removi a referência à Application() no Preview, pois pode causar problemas.
// Para os previews funcionarem corretamente, especialmente com ViewModels,
// pode ser necessário fornecer instâncias mocadas ou usar Hilt para injeção de dependência
// e configurar previews com Hilt.
// Por simplicidade, o PreviewPrincipalScreenWithGames e PreviewPrincipalScreenEmpty podem não exibir
// o estado dos ViewModels corretamente sem mais configurações de DI para previews.

// Ajuste para Previews, removendo dependência direta de Application() para MainViewModel
// e uso de `viewModel()` que pode não funcionar bem em previews isolados.
// Para uma solução robusta de preview com ViewModels, considere usar uma factory ou Hilt.

@Preview(showBackground = true, name = "Tela Principal - Vazia (Simples)")
@Composable
fun PreviewPrincipalScreenEmptySimple() {
    MaterialTheme { // Adicionado MaterialTheme para estilos corretos
        Column { Text("Preview Conteúdo Principal Vazio") } // Placeholder
    }
}

@Preview(showBackground = true, name = "Tela Principal - Com Jogos (Simples)")
@Composable
fun PreviewPrincipalScreenWithGamesSimple() {
     MaterialTheme { // Adicionado MaterialTheme para estilos corretos
        val sampleJogos = listOf(
            Jogo(id = 1, numeros = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), dataCriacao = Date(), favorito = true),
            Jogo(id = 2, numeros = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16), dataCriacao = Date(), favorito = false)
        )
        LazyColumn {
            items(sampleJogos) { jogo ->
                JogoItem(jogo = jogo, onFavoritoClick = {}, onJogoClick = {})
            }
        }
    }
}

// É necessário criar as strings no arquivo res/values/strings.xml:
// <string name="app_name_short">Lotofácil Gen</string>
// <string name="gerador_de_jogos">Gerador de Jogos</string>
// <string name="instrucao_gerador_jogos">Configure os filtros e gere seus jogos da Lotofácil.</string>
// <string name="nenhum_jogo_gerado_ainda">Nenhum jogo gerado ainda.</string>
// <string name="jogo_clicado_feedback">Jogo clicado: %s</string>
// <string name="caracteristicas_jogo_format">P: %1$d | I: %2$d | Soma: %3$d\nPrimos: %4$d | Fib: %5$d\nMiolo: %6$d | Moldura: %7$d\nMúlt. 3: %8$d</string>
// A string "configurar_filtros" já existe, mas as outras podem precisar ser adicionadas ou verificadas.
// As strings "gerando_jogos", "gerar_jogos", "jogos_gerados", "salvar_jogos", "marcar_como_favorito"
// "nenhum_jogo_para_salvar", "jogos_salvos_com_sucesso", "erro_salvar_jogos" já devem existir.
// Verifique se todas as strings referenciadas via `stringResource(R.string.nome_string)` estão presentes.

import android.app.Application // Removido dos previews que não necessitam de ViewModel real

// Tentativa de simplificar os previews para evitar problemas de compilação/runtime
// devido a dependências de Context/Application nos ViewModels.

// Para os Previews `PreviewPrincipalScreenEmpty` e `PreviewPrincipalScreenWithGames`,
// seria melhor injetar ViewModels mocados que não dependam de `Application`
// ou usar uma biblioteca de DI como Hilt que tem suporte para previews.
// Por enquanto, os previews mais simples (`Simple`) são mais seguros.

// O `Application()` como argumento para `MainViewModel` no Preview não é ideal.
// Para previews funcionais com ViewModels, é comum criar versões mocadas
// dos ViewModels ou usar ferramentas de DI que facilitem isso.

// As dependências `GeradorViewModel = viewModel()` e `JogoViewModel = viewModel()`
// em `PrincipalScreen` são para quando a tela é usada dentro de um escopo de navegação
// onde os ViewModels são corretamente fornecidos. Para previews, isso pode não funcionar
// isoladamente sem um NavHostController ou configuração de Hilt.

// Recomenda-se testar os previews em um emulador/dispositivo para ver o comportamento real.

// Adicionando Application ao construtor do MainViewModel apenas para o preview
// Isto não é ideal para produção, mas pode fazer o preview funcionar.
// Melhor seria ter um construtor sem argumentos ou um mock.
class MockMainViewModel : MainViewModel(Application()) // Apenas para preview, se necessário

@Preview(showBackground = true, name = "Tela Principal - Vazia (Com Mock VM)")
@Composable
fun PreviewPrincipalScreenEmptyWithMockVM() {
    val mockMainViewModel = MockMainViewModel()
    PrincipalScreen(mainViewModel = mockMainViewModel, onNavigateToFiltros = {})
    // Para os outros ViewModels, precisaria de mocks similares ou usar `viewModel()`
    // esperando que o ambiente de preview os forneça (o que pode não acontecer).
} 