package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
        application: Application,
        private val jogoRepository: JogoRepository,
        private val appDataStore: AppDataStore
) : AndroidViewModel(application) {

    // LiveData para observar se o primeiro run foi completado
    // TODO: Verifique a dependência androidx.lifecycle:lifecycle-livedata-ktx para habilitar
    // asLiveData()
    val firstRunCompleted: LiveData<Boolean> // = appDataStore.firstRunCompleted.asLiveData()
        get() =
                MutableLiveData<Boolean>().apply {
                    viewModelScope.launch { appDataStore.firstRunCompleted.collect { value = it } }
                }

    // StateFlow para o estado das notificações
    val notificationsEnabled: StateFlow<Boolean> =
            appDataStore.notificationsEnabled.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = true // Valor inicial, será substituído pelo valor do DataStore
            )

    // Função para atualizar o estado das notificações
    fun setNotificationsEnabled(isEnabled: Boolean) {
        viewModelScope.launch { appDataStore.setNotificationsEnabled(isEnabled) }
    }

    // LiveData para observar todos os jogos
    val todosJogos: LiveData<List<Jogo>> = jogoRepository.todosJogos

    // LiveData para observar jogos favoritos
    val jogosFavoritos: LiveData<List<Jogo>> = jogoRepository.jogosFavoritos

    // Função para marcar um jogo como favorito
    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
        viewModelScope.launch {
            val jogoAtualizado = jogo.copy(favorito = favorito)
            jogoRepository.atualizarJogo(jogoAtualizado)
        }
    }

    // Função para indicar que o primeiro run foi completado
    fun completeFirstRun() {
        viewModelScope.launch { appDataStore.setFirstRunCompleted() }
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
