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
import androidx.room.Room
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

val appDataStore: AppDataStore by lazy { AppDataStore(App.instance) }

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var jogoRepository: JogoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "jogo-database"
        ).build()
        jogoRepository = JogoRepository(appDatabase.jogoDao())

        setContent {
            val navController = rememberNavController()

            // Collect theme preference as State
            val selectedTheme by appDataStore.getThemePreference().collectAsState(initial = "system")

            val isDarkTheme = when (selectedTheme) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            CebolaoLotofacilGeneratorTheme(darkTheme = isDarkTheme) {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(jogoRepository))

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

class App : android.app.Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}


@Composable
fun navigation(
    navController: NavHostController,
    appDataStore: AppDataStore,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("menu") {
            MenuScreen(navController = navController, mainViewModel = viewModel)
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
        composable("settings") { // appDataStore is not used in SettingsScreen
            SettingsScreen(navController = navController)
        }
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {

}

