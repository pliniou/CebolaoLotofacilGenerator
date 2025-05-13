// Arquivo legado, esvaziado para evitar conflitos

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
import com.exemplo.cebolao.data.AppDataStore
import com.exemplo.cebolao.data.AppDatabaseInstance
import com.exemplo.cebolao.repository.JogoRepository
import com.exemplo.cebolao.ui.theme.CebolaoLotofacilGeneratorTheme
import com.exemplo.cebolao.viewmodel.MainViewModel
import com.exemplo.cebolao.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var appDataStore: AppDataStore
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o AppDataStore
        appDataStore = AppDataStore(applicationContext)

        // Inicializa o ViewModel com a Factory
        val database = AppDatabaseInstance.getDatabase(applicationContext)
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
