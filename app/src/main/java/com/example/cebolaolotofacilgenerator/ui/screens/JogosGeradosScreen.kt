package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.Screen
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.ui.components.ConferenciaResultados
import com.example.cebolaolotofacilgenerator.ui.components.SeletorResultado
import com.example.cebolaolotofacilgenerator.ui.components.SnackbarManager
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.JogosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.ResultadoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Tela para exibir os jogos que foram gerados pelo [GeradorViewModel].
 * Permite ao usuário visualizar os jogos, salvá-los, gerar mais jogos ou limpar a lista atual.
 *
 * @param navController O [NavHostController] para navegação.
 * @param mainViewModel O [MainViewModel] para acesso a ViewModels compartilhados e ações globais.
 * @param geradorViewModel O [GeradorViewModel] que contém os jogos gerados e a lógica de geração/salvamento.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JogosGeradosScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    geradorViewModel: GeradorViewModel
) {
    val jogosGerados by geradorViewModel.jogosGerados.observeAsState(emptyList())
    val operacaoStatus by geradorViewModel.operacaoStatus.observeAsState(OperacaoStatus.OCIOSO)
    val resultadoViewModel = mainViewModel.resultadoViewModel
    val jogosViewModel = mainViewModel.jogosViewModel
    
    // Estados para a conferência
    val resultados by resultadoViewModel.todosResultados.observeAsState(emptyList())
    val resultadoSelecionado by jogosViewModel.resultadoSelecionado.collectAsState()
    val conferenciaEmAndamento by jogosViewModel.conferenciaEmAndamento.collectAsState()
    var mostrarDialogLimparJogos by remember { mutableStateOf(false) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text(stringResource(R.string.jogos_gerados_titulo)) },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                        navigationIconContentColor =
                                                MaterialTheme.colorScheme.onPrimary
                                ),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.voltar),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        actions = {
                            if (jogosGerados.isNotEmpty()) {
                                IconButton(onClick = { mostrarDialogLimparJogos = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = stringResource(R.string.limpar_jogos_gerados_descricao),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                )
            }
    ) { paddingValues ->
        when (operacaoStatus) {
            OperacaoStatus.CARREGANDO -> {
                Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            else -> {
                if (jogosGerados.isEmpty()) {
                    Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                    ) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                    text = stringResource(R.string.nenhum_jogo_gerado_ainda),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = {
                                    geradorViewModel.salvarJogosGerados()
                                    mainViewModel.showSnackbar(navController.context.getString(R.string.jogos_salvos_com_sucesso))
                                    // Opcional: limpar jogos gerados da lista local ou navegar para outro lugar
                                }) {
                                    Text(stringResource(R.string.salvar_todos_jogos))
                                }
                                OutlinedButton(onClick = { navController.navigate(Screen.Gerador.route) }) {
                                    Text(stringResource(R.string.gerar_mais_jogos))
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentPadding = PaddingValues(16.dp)
                    ) {
                        // Componente de seleção de resultado
                        item {
                            SeletorResultado(
                                resultados = resultados,
                                resultadoSelecionado = resultadoSelecionado,
                                onResultadoSelecionado = { resultado ->
                                    jogosViewModel.selecionarResultadoParaConferencia(resultado)
                                }
                            )
                            
                            // Componente de conferência
                            ConferenciaResultados(
                                jogos = jogosGerados,
                                resultado = resultadoSelecionado,
                                isLoading = conferenciaEmAndamento,
                                onConferirClick = {
                                    jogosViewModel.conferirJogos(jogosGerados)
                                },
                                onConferenciaCompleta = { _ ->
                                    // Atualizar a lista após a conferência é feito diretamente no ViewModel
                                }
                            )
                        }
                        
                        item {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            Text(
                                text = stringResource(R.string.seus_jogos_contagem, jogosGerados.size),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(jogosGerados, key = { it.id }) { jogo ->
                            JogoGeradoItemCard(jogo = jogo, mainViewModel = mainViewModel)
                        }
                    }
                }
            }
        }
        
        if (mostrarDialogLimparJogos) {
            AlertDialog(
                onDismissRequest = { mostrarDialogLimparJogos = false },
                title = { Text(stringResource(R.string.limpar_jogos_gerados_titulo_dialog)) },
                text = { Text(stringResource(R.string.limpar_jogos_gerados_mensagem_dialog)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            geradorViewModel.limparJogosGerados()
                            mostrarDialogLimparJogos = false
                        }
                    ) {
                        Text(stringResource(R.string.confirmar))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogLimparJogos = false }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }
    }
}

/**
 * Composable para exibir um único jogo gerado em um Card, com opções de favoritar.
 *
 * @param jogo O objeto [Jogo] a ser exibido.
 * @param mainViewModel O [MainViewModel] para ações como marcar o jogo como favorito.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JogoGeradoItemCard(
    jogo: Jogo,
    mainViewModel: MainViewModel
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.jogo_id_label, jogo.id),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { mainViewModel.marcarComoFavorito(jogo, !jogo.favorito) }) {
                    Icon(
                        imageVector = if (jogo.favorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(if (jogo.favorito) R.string.desmarcar_como_favorito else R.string.marcar_como_favorito),
                        tint = if (jogo.favorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.data_criacao_label, dateFormatter.format(jogo.dataCriacao)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                jogo.numeros.sorted().forEach { numero ->
                    Text(
                        text = numero.toString().padStart(2, '0'),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
