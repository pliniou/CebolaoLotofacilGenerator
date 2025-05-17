package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase // Para a Factory
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository // Para a Factory
import com.example.cebolaolotofacilgenerator.viewmodel.FavoritosViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.FavoritosViewModelFactory
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    navController: NavController,
    mainViewModel: MainViewModel // Para Snackbar
) {
    val context = LocalContext.current
    // É importante instanciar o JogoRepository aqui ou garantir que ele seja injetado corretamente.
    // Para simplificar, estou criando a factory aqui. Em um app maior, usaria injeção de dependência (Hilt).
    val jogoDao = AppDatabase.getDatabase(context).jogoDao()
    val jogoRepository = JogoRepository(jogoDao)
    val favoritosViewModel: FavoritosViewModel = viewModel(
        factory = FavoritosViewModelFactory(jogoRepository)
    )

    val jogosFavoritos by favoritosViewModel.jogosFavoritos.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tela_favoritos_titulo)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.voltar))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (jogosFavoritos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.nenhum_jogo_favorito))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(jogosFavoritos, key = { it.id }) { jogo ->
                        JogoFavoritoItem(
                            jogo = jogo,
                            onFavoritoClick = {
                                favoritosViewModel.marcarComoFavorito(jogo, !jogo.favorito)
                                val mensagem = if (!jogo.favorito) {
                                     "" 
                                } else {
                                    context.getString(R.string.jogo_removido_favoritos)
                                }
                                if (mensagem.isNotEmpty()) mainViewModel.showSnackbar(mensagem)
                            },
                            onExcluirClick = {
                                // Adicionar diálogo de confirmação antes de excluir
                                favoritosViewModel.excluirJogo(jogo)
                                mainViewModel.showSnackbar(context.getString(R.string.jogo_excluido))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JogoFavoritoItem(
    jogo: Jogo,
    onFavoritoClick: () -> Unit,
    onExcluirClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.jogo_id_label, jogo.id),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = jogo.numeros.joinToString(" - "),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Data de criação: ${dateFormat.format(jogo.dataCriacao)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    IconButton(onClick = onFavoritoClick) {
                        Icon(
                            imageVector = if (jogo.favorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = stringResource(if (jogo.favorito) R.string.desmarcar_como_favorito else R.string.marcar_como_favorito),
                            tint = if (jogo.favorito) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
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
            }
        }
    }
}
