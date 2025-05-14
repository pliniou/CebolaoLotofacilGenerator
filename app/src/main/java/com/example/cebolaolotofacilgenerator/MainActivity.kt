package com.example.cebolaolotofacilgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.ui.screens.FavoritosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.HomeScreen
import com.example.cebolaolotofacilgenerator.ui.screens.OnboardingScreen
import com.example.cebolaolotofacilgenerator.ui.screens.ResultadosScreen
import com.example.cebolaolotofacilgenerator.ui.screens.SettingsScreen
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var appDataStore: AppDataStore
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o AppDataStore
        appDataStore = AppDataStore(applicationContext)

        // Inicializa o ViewModel com a Factory
        val database = AppDatabase.getDatabase(applicationContext)
        val jogoRepository = JogoRepository(database.jogoDao())
        val viewModelFactory = MainViewModelFactory(application, jogoRepository, appDataStore)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            CebolaoLotofacilGeneratorTheme {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Verifica se é a primeira execução
                    val isFirstRun = remember { mutableStateOf(true) }
                    var startDestination by remember { mutableStateOf(Routes.Home.route) }

                    // Carrega o valor de firstRunCompleted
                    LaunchedEffect(Unit) {
                        isFirstRun.value = !appDataStore.firstRunCompleted.first()
                        startDestination =
                                if (isFirstRun.value) {
                                    Routes.Onboarding.route
                                } else {
                                    Routes.Home.route
                                }
                    }

                    // Mostra o NavHost somente quando o startDestination estiver definido
                    // (após carregar dados do DataStore)
                    if (startDestination.isNotEmpty()) {
                        NavHost(
                                navController = navController,
                                startDestination = startDestination
                        ) {
                            composable(Routes.Home.route) {
                                HomeScreen(viewModel = mainViewModel, navController = navController)
                            }
                            composable(Routes.Settings.route) {
                                SettingsScreen(
                                        viewModel = mainViewModel,
                                        navController = navController
                                )
                            }
                            composable(Routes.Favoritos.route) {
                                FavoritosScreen(
                                        viewModel = mainViewModel,
                                        navController = navController
                                )
                            }
                            composable(Routes.Resultados.route) {
                                ResultadosScreen(
                                        viewModel = mainViewModel,
                                        navController = navController
                                )
                            }
                            composable(Routes.Onboarding.route) {
                                OnboardingScreen(
                                        viewModel = mainViewModel,
                                        onComplete = {
                                            lifecycleScope.launch {
                                                appDataStore.setFirstRunCompleted(true)
                                                navController.navigate(Routes.Home.route) {
                                                    popUpTo(Routes.Onboarding.route) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
