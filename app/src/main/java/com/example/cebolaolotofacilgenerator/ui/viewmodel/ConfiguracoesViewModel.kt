package com.example.cebolaolotofacilgenerator.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de Configurações. TODO: Implementar a lógica de negócios e os
 * LiveData/StateFlow necessários.
 */
class ConfiguracoesViewModel(private val appDataStore: AppDataStore) : ViewModel() {

    enum class TemaAplicativo {
        CLARO,
        ESCURO,
        SISTEMA
    }

    private val _temaAtual = MutableLiveData<TemaAplicativo>()
    val temaAtual: LiveData<TemaAplicativo> = _temaAtual

    init {
        viewModelScope.launch {
            // Carrega o tema salvo. Usa first() para obter o valor inicial.
            // Para atualizações contínuas, você usaria .collect {}
            val temaOrdinal = appDataStore.temaAplicativo.first()
            _temaAtual.value =
                    TemaAplicativo.values().getOrElse(temaOrdinal) { TemaAplicativo.SISTEMA }
        }
    }

    fun alterarTema(novoTema: TemaAplicativo) {
        viewModelScope.launch {
            appDataStore.salvarTemaAplicativo(novoTema.ordinal)
            _temaAtual.value = novoTema // Atualiza o LiveData imediatamente
        }
    }

    // Exemplo de LiveData/StateFlow que você pode precisar:
    // private val _notificationsEnabled = MutableStateFlow(false)
    // val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    // fun setNotificationsEnabled(enabled: Boolean) {
    // _notificationsEnabled.value = enabled
    // TODO: Salvar esta preferência (DataStore, SharedPreferences, etc.)
    // }

    // init {
    // TODO: Carregar as configurações salvas ao iniciar o ViewModel
    // viewModelScope.launch {
    // _notificationsEnabled.value = settingsRepository.getNotificationsEnabled()
    // }
    // }
}
