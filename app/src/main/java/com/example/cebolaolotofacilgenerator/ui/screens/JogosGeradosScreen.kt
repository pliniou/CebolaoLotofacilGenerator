package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
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
import java.util.Date
import java.util.Locale
import java.util.UUID

/** Tela que exibe os jogos gerados pelo app */
@OptIn(ExperimentalMaterial3Api::class)
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
                        title = { Text("Jogos Gerados") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                        navigationIconContentColor =
                                                MaterialTheme.colorScheme.onPrimary
                                ),
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
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
                                        contentDescription = "Limpar jogos",
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
                            Button(
                                    onClick = {
                                        navController.navigate(
                                                Screen.Gerador.route,
                                                navOptions { popUpTo(Screen.Home.route) }
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth(0.7f)
                            ) { Text(text = stringResource(R.string.gerar_mais_jogos)) }
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
                                text = "Seus jogos (${jogosGerados.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(jogosGerados, key = { it.id }) { jogo ->
                            var isFavoritoState by remember { mutableStateOf(jogo.favorito) }
                            JogoItem(
                                jogo = jogo,
                                isFavorito = isFavoritoState,
                                onFavoritoClick = {
                                    val novoEstadoFavorito = !isFavoritoState
                                    mainViewModel.marcarComoFavorito(jogo, novoEstadoFavorito)
                                    isFavoritoState = novoEstadoFavorito
                                    val msgResId = if (novoEstadoFavorito) R.string.jogo_adicionado_favoritos
                                              else R.string.jogo_removido_favoritos
                                    mainViewModel.showSnackbar(mainViewModel.getApplication<Application>().getString(msgResId))
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
        
        if (mostrarDialogLimparJogos) {
            AlertDialog(
                onDismissRequest = { mostrarDialogLimparJogos = false },
                title = { Text("Limpar jogos") },
                text = { Text("Tem certeza que deseja excluir todos os jogos gerados?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            geradorViewModel.resetarConfiguracoes()
                            SnackbarManager.mostrarMensagem("Todos os jogos foram excluídos")
                            mostrarDialogLimparJogos = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarDialogLimparJogos = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun JogoItem(jogo: Jogo, isFavorito: Boolean, onFavoritoClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jogo #" + (jogo.id.toString()),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Exibir acertos, se conferido
                    if (jogo.acertos != null) {
                        val corAcertos = when (jogo.acertos) {
                            15 -> Color(0xFF43A047)
                            14 -> Color(0xFF7CB342)
                            13 -> Color(0xFF9CCC65)
                            12 -> Color(0xFFFFEB3B)
                            11 -> Color(0xFFFFC107)
                            else -> Color.Gray
                        }
                        
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(
                                    color = corAcertos,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${jogo.acertos} pts",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (jogo.acertos!! >= 14) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Text(
                    text = jogo.numeros.joinToString(" - "),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Data de criação
                Text(
                    text = "Criado em: ${formatarData(jogo.dataCriacao)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            IconButton(onClick = onFavoritoClick) {
                Icon(
                    imageVector = if (isFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorito) "Desmarcar como favorito" else "Marcar como favorito",
                    tint = if (isFavorito) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatarData(data: Date): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return formato.format(data)
}
