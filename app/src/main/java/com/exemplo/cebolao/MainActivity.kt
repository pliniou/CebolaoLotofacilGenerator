package com.exemplo.cebolao

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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
import com.exemplo.cebolao.data.JogoRepository
import com.exemplo.cebolao.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch






val Context.dataStore by preferencesDataStore(name = "app_preferences")

class MainActivity : ComponentActivity() {

    private lateinit var appDatabaseInstance: AppDatabaseInstance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appDataStore = AppDataStore(this)
            val navController = rememberNavController()
            CebolaoLotofacilGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    var selectedTheme by remember { mutableStateOf("system") }
                    appDatabaseInstance = AppDatabaseInstance(applicationContext)
                    val jogoRepository = JogoRepository(appDatabaseInstance.database.jogoDao())
                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModelFactory(jogoRepository)
                    )

                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            appDataStore.getThemePreference().collect{
 selectedTheme = it ?: "system"
                            }
                        }
                    }
                    

                     val isDarkTheme = when (selectedTheme) {
                         "dark" -> true
                         "light" -> false
                         else -> isSystemInDarkTheme() }

                    CebolaoLotofacilGeneratorTheme(darkTheme = isDarkTheme) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            navigation(
                                navController = navController,
                                appDataStore = appDataStore,
                                viewModel = viewModel
                            )
                        }
                     }
                  }
                }             
        }
        }
}

    suspend fun getThemePreference(): String? {
        return AppDataStore(this).getThemePreference().collect { it }
    }


@Composable
fun navigation(
    navController: NavHostController,
    appDataStore: AppDataStore,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController, appDataStore)
        }
        composable("menu") {
            MenuScreen(navController = navController, appDataStore = appDataStore)
        }
        composable("filtros") {
            FiltrosScreen(navController = navController, viewModel = viewModel)
        }
        composable("jogosGerados") { backStackEntry ->
            JogosGeradosScreen(navController, viewModel)
        }
        composable("favoritos") {
            FavoritosScreen(navController = navController, viewModel = viewModel)
        }
        composable("settings") {
            SettingsScreen(navController = navController, appDataStore = appDataStore)
        }
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {

}

