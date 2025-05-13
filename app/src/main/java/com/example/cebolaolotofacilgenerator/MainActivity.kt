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
import androidx.navigation.compose.rememberNavController
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.AppDatabase
import com.example.cebolaolotofacilgenerator.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModelFactory
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
        val jogoRepository = JogoRepository(database.jogoDao(), appDataStore)
        val viewModelFactory = MainViewModelFactory(application, jogoRepository, appDataStore)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        // Observa o firstRunCompleted
        lifecycleScope.launch {
            appDataStore.firstRunCompleted.collect { completed ->
                // A lógica de navegação baseada nisso deve ocorrer dentro do NavHost
                // ou ser controlada pelo estado no ViewModel.
            }
        }

        setContent {
            CebolaoLotofacilGeneratorTheme {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // Passa o MainViewModel para o Navigation Composable
                    AppNavigation(navController = navController, viewModel = mainViewModel)
                }
            }
        }
    }
}
