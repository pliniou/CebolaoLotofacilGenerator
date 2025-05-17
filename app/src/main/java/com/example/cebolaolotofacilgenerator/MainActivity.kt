package com.example.cebolaolotofacilgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.cebolaolotofacilgenerator.ui.components.BottomNavigationBar
import com.example.cebolaolotofacilgenerator.ui.components.ObservarMensagensSnackbar
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val temaAplicativo by mainViewModel.temaAplicativo.collectAsState()

            CebolaoLotofacilGeneratorTheme(tema = temaAplicativo) {
                val navController = rememberNavController()
                var showBottomBar by remember { mutableStateOf(true) }
                val startDestination = Screen.BoasVindas.route

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
                            mainViewModel = mainViewModel,
                            modifier = Modifier.padding(innerPadding),
                            startDestination = startDestination
                    )
                }
            }
        }
    }
}
