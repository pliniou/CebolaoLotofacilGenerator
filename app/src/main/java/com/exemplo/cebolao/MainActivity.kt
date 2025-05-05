package com.exemplo.cebolao


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemplo.cebolao.data.AppDataStore
import com.exemplo.cebolao.data.AppDatabaseInstance
import com.exemplo.cebolao.ui.FavoritosScreen
import com.exemplo.cebolao.ui.FiltrosScreen
import com.exemplo.cebolao.ui.JogosGeradosScreen
import com.exemplo.cebolao.ui.MenuScreen
import com.exemplo.cebolao.ui.SettingsScreen
import com.exemplo.cebolao.ui.WelcomeScreen
import com.exemplo.cebolao.ui.theme.CebolaoLotofacilGeneratorTheme
import com.exemplo.cebolao.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import android.content.Context


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
                           navigation(navController = navController,
                            appDataStore = appDataStore,
                            viewModel = viewModel)
                        }
                }
            }
        }
    }
}


@Composable
fun navigation(navController: NavHostController, appDataStore: AppDataStore, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController, appDataStore)
        }
        composable("menu") {
            MenuScreen(navController = navController, appDataStore = appDataStore)
        }
        composable("filtros") {
            FiltrosScreen(navController = navController, viewModel = viewModel, appDataStore = appDataStore)
        }
        composable("jogosGerados") {
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