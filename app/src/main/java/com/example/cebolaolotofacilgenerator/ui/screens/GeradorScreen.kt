package com.example.cebolaolotofacilgenerator.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* // Explicitamente importar ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class) // Adicionar OptIn aqui
@Composable
fun GeradorScreen(
        navController: NavController,
        dezenasFixasArg: String? // Recebe as dezenas como String da navegação
) {
    val application = LocalContext.current.applicationContext as Application
    // Nota: Idealmente, MainViewModelFactory seria provida via DI ou um LocalProvider.
    // Para este exemplo, vamos instanciá-la com o que temos, mas isso não é ideal
    // pois JogoRepository e ResultadoRepository não são diretamente necessários para
    // GeradorViewModel
    // e AppDataStore também não. GeradorViewModel só precisa de Application.
    // Seria melhor ter uma factory específica para GeradorViewModel ou ajustar
    // MainViewModelFactory.

    // Simplificando a factory para GeradorViewModel, assumindo que ele só precisa da Application
    // Se MainViewModelFactory for complexa, isso precisaria de ajuste.
    val geradorViewModel: GeradorViewModel =
            viewModel(
                    factory =
                            MainViewModelFactory(
                                    application,
                                    appDataStore = null,
                                    jogoRepository = null,
                                    resultadoRepository = null
                            ) // Ajuste improvisado
            )
    // O ideal seria: viewModel<GeradorViewModel>(factory = GeradorViewModelFactory(application))

    LaunchedEffect(dezenasFixasArg) {
        val dezenas = dezenasFixasArg?.split(",")?.mapNotNull { it.toIntOrNull() }
        geradorViewModel.inicializarComNumerosFixos(dezenas)
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Gerar Jogos") },
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
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Voltar"
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                contentAlignment = Alignment.Center
        ) {
            // Conteúdo da tela do gerador será adicionado aqui
            Column {
                Text("Tela de Geração de Jogos - Em Construção")
                Text("Dezenas fixas recebidas: ${dezenasFixasArg ?: "Nenhuma"}")
                // TODO: Adicionar UI para quantidade de jogos, números, fixos, excluídos, filtros,
                // etc.
                // TODO: Botão para chamar geradorViewModel.gerarJogos()
                // TODO: Exibir mensagens e status de operação
                // TODO: Exibir jogos gerados
            }
        }
    }
}
