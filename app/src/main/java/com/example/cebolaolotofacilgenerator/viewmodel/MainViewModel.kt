package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        application: Application,
        val jogoRepository: JogoRepository,
        private val appDataStore: AppDataStore
) : AndroidViewModel(application) {

    init {
        // Log para ver se o Hilt consegue instanciar isso
        android.util.Log.d("MainViewModelHilt", "MainViewModel com JogoRepository e AppDataStore")
    }

    // TODO: O restante do conteúdo original ainda está comentado.
    // Vamos restaurá-lo gradualmente.

    // ViewModels secundários para compartilhar entre as telas
    val jogosViewModel = JogosViewModel(jogoRepository) 
    val filtrosViewModel = FiltrosViewModel(application) 
    val geradorViewModel = GeradorViewModel(application, filtrosViewModel, jogoRepository)

    enum class TemaAplicativo(val key: String) {
        CLARO("light"),
        ESCURO("dark"),
        SISTEMA("system"),
        AZUL("blue"),
        VERDE("green"),
        LARANJA("orange"),
        CIANO("cyan");
        val displayName: String
            get() = when (this) {
                SISTEMA -> "Padrão do Sistema"
                CLARO -> "Claro"
                ESCURO -> "Escuro"
                AZUL -> "Azul"
                VERDE -> "Verde"
                LARANJA -> "Laranja"
                CIANO -> "Ciano"
            }
    }

    val temaAplicativo: StateFlow<TemaAplicativo> =
            appDataStore 
                    .temaAplicativo 
                    .map { ordinal ->
                        TemaAplicativo.values().getOrElse(ordinal) {
                            TemaAplicativo.SISTEMA
                        } 
                    }
                    .stateIn(
                            scope = viewModelScope, 
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = TemaAplicativo.SISTEMA 
                    )

    fun salvarTemaAplicativo(tema: TemaAplicativo) {
        viewModelScope.launch { appDataStore.salvarTemaAplicativo(tema.ordinal) } 
    }

    val preferenciasViewModel: PreferenciasViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(PreferenciasViewModel::class.java)

    // val todosJogos: LiveData<List<Jogo>> = jogoRepository.todosJogos

    // val jogosFavoritos: LiveData<List<Jogo>> = jogoRepository.jogosFavoritos

    // fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
    //     viewModelScope.launch { 
    //         val jogoAtualizado = jogo.copy(favorito = favorito)
    //         jogoRepository.atualizarJogo(jogoAtualizado) 
    //     }
    // }

    // fun completeFirstRun() {
    //     viewModelScope.launch { appDataStore.setFirstRunCompleted() } 
    // }

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun showSnackbar(message: String) {
        viewModelScope.launch { 
            _snackbarMessage.emit(message)
        }
    }
}

/* Removida MainViewModelFactory duplicada מכאן
class MainViewModelFactory(
        private val application: Application,
        private val jogoRepository: JogoRepository,
        private val appDataStore: AppDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, jogoRepository, appDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/
