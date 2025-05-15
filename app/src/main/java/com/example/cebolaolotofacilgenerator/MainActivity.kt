package com.example.cebolaolotofacilgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import com.example.cebolaolotofacilgenerator.ui.components.BottomNavigationBar
import com.example.cebolaolotofacilgenerator.ui.components.ObservarMensagensSnackbar
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var appDataStore: AppDataStore
    private lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDataStore = AppDataStore(applicationContext)

        val database = AppDatabase.getDatabase(applicationContext)
        val jogoRepository = JogoRepository(database.jogoDao())
        val resultadoRepository = ResultadoRepository(database.resultadoDao())
        val viewModelFactory =
                MainViewModelFactory(application, jogoRepository, resultadoRepository, appDataStore)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            val temaAplicativo by mainViewModel.temaAplicativo.collectAsState()

            CebolaoLotofacilGeneratorTheme(tema = temaAplicativo) {
                val navController = rememberNavController()
                var showBottomBar by remember { mutableStateOf(true) }
                val startDestination = Screen.Home.route

                val snackbarHostState = remember { SnackbarHostState() }
                
                // Observar mensagens do SnackbarManager
                ObservarMensagensSnackbar(snackbarHostState)

                // Observar também mensagens do MainViewModel para compatibilidade
                LaunchedEffect(key1 = mainViewModel) {
                    mainViewModel.snackbarMessage.collect { message ->
                        if (message.isNotEmpty()) {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }

                Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                ) { innerPadding ->
                    AppNavigation(
                            navController = navController,
                            viewModel = mainViewModel,
                            modifier = Modifier.padding(innerPadding),
                            startDestination = startDestination
                    )
                }
            }
        }
    }
}
