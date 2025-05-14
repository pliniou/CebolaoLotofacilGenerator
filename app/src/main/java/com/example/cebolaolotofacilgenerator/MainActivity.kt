package com.example.cebolaolotofacilgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.ui.components.BottomNavigationBar
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private lateinit var appDataStore: AppDataStore
    private lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDataStore = AppDataStore(applicationContext)

        val database = AppDatabase.getDatabase(applicationContext)
        val jogoRepository = JogoRepository(database.jogoDao())
        val viewModelFactory = MainViewModelFactory(application, jogoRepository, appDataStore)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            CebolaoLotofacilGeneratorTheme {
                val navController = rememberNavController()
                var showBottomBar by remember { mutableStateOf(false) }
                var determinedStartDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(navController) {
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        showBottomBar = backStackEntry.destination.route != Screen.Onboarding.route
                    }
                }

                LaunchedEffect(Unit) {
                    val isFirstRun = !appDataStore.firstRunCompleted.first()
                    determinedStartDestination =
                            if (isFirstRun) Screen.Onboarding.route else Screen.Home.route
                }

                determinedStartDestination?.let { startDest ->
                    Scaffold(
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
                                startDestination = startDest
                        )
                    }
                }
                        ?: run {
                            Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                            ) {}
                        }
            }
        }
    }
}
