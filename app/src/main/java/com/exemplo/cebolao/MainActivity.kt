package com.exemplo.cebolao

import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemplo.cebolao.data.AppDataStore
import com.exemplo.cebolao.ui.MenuScreen
import com.exemplo.cebolao.data.AppDatabaseInstance
import com.exemplo.cebolao.repository.JogoRepository
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.ui.WelcomeScreen
import com.exemplo.cebolao.ui.FiltrosScreen
import com.exemplo.cebolao.ui.JogosGeradosScreen
import com.exemplo.cebolao.ui.FavoritosScreen
import com.exemplo.cebolao.ui.SettingsScreen
import com.exemplo.cebolao.ui.theme.CebolaoLotofacilGeneratorTheme
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "app_preferences")

class MainActivity : ComponentActivity() {

    private lateinit var appDatabaseInstance: AppDatabaseInstance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CebolaoLotofacilGeneratorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val appDataStore = AppDataStore(this)
                    var selectedTheme by remember { mutableStateOf("system") }
                    appDatabaseInstance = AppDatabaseInstance(applicationContext)

                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModel.MainViewModelFactory(appDatabaseInstance)
                    )

                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            selectedTheme = appDataStore.getThemePreference()
                        }
                    }

                    val isDarkTheme = when (selectedTheme) {
                        "dark" -> true
                        "light" -> false
                        else -> isSystemInDarkTheme()
                    }

                    CebolaoLotofacilGeneratorTheme(darkTheme = isDarkTheme) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {

                            Navigation(
                                navController = navController,
                                appDataStore = appDataStore,
                                viewModel = viewModel)
                        }
                    }
                }
            }
        }



        }
    }
}

@Composable
fun Navigation(navController: NavHostController, appDataStore: AppDataStore, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController = navController)
            WelcomeScreen(navController)
        }
        composable("menu") {
            MenuScreen(navController = navController)
        }
        composable("filtros") {
            FiltrosScreen(navController = navController, viewModel = viewModel)
        }
        composable("jogos_gerados") { JogosGeradosScreen(navController = navController, viewModel = viewModel)
            JogosGeradosScreen(navController)
        }
        composable("favoritos") {
            FavoritosScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }

    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CebolaoLotofacilGeneratorTheme {
        Greeting("Android")
    }
}