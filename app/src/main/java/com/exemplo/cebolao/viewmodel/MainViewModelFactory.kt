// Arquivo legado, esvaziado para evitar conflitos

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exemplo.cebolao.data.AppDataStore
//import com.exemplo.cebolao.repository.JogoRepository

class MainViewModelFactory(
    //private val application: Application,
    //private val repository: JogoRepository,
    //private val dataStore: AppDataStore
) {
    //override fun <T : ViewModel> create(modelClass: Class<T>): T {
    //    return MainViewModel(repository, dataStore) as T
    //}
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
 return MainViewModel(repository, dataStore) as T
    }
}