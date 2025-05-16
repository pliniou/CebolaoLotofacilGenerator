package com.example.cebolaolotofacilgenerator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

// Define o DataStore
private val Application.dataStore by preferencesDataStore(name = "filtros_prefs")

class FiltrosViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = getApplication<Application>().dataStore

    // Chaves para o DataStore
    private object PreferencesKeys {
        val NUMEROS_FIXOS = stringSetPreferencesKey("numeros_fixos")
        val NUMEROS_EXCLUIDOS = stringSetPreferencesKey("numeros_excluidos")
        val QTD_PARES = intPreferencesKey("qtd_pares")
        // Adicionar chaves para outros filtros aqui
    }

    // StateFlows para os filtros
    private val _numerosFixos = MutableStateFlow<Set<Int>>(emptySet())
    val numerosFixos: StateFlow<Set<Int>> = _numerosFixos.asStateFlow()

    private val _numerosExcluidos = MutableStateFlow<Set<Int>>(emptySet())
    val numerosExcluidos: StateFlow<Set<Int>> = _numerosExcluidos.asStateFlow()

    private val _qtdPares = MutableStateFlow<Int?>(null)
    val qtdPares: StateFlow<Int?> = _qtdPares.asStateFlow()

    init {
        carregarFiltros()
    }

    private fun carregarFiltros() {
        viewModelScope.launch {
            val preferences = dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.first() // Pega a primeira emissão (snapshot atual)

            _numerosFixos.value = preferences[PreferencesKeys.NUMEROS_FIXOS]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
            _numerosExcluidos.value = preferences[PreferencesKeys.NUMEROS_EXCLUIDOS]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
            _qtdPares.value = preferences[PreferencesKeys.QTD_PARES]
        }
    }

    fun salvarNumerosFixos(numeros: Set<Int>) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.NUMEROS_FIXOS] = numeros.map { it.toString() }.toSet()
            }
            _numerosFixos.value = numeros
        }
    }

    fun salvarNumerosExcluidos(numeros: Set<Int>) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.NUMEROS_EXCLUIDOS] = numeros.map { it.toString() }.toSet()
            }
            _numerosExcluidos.value = numeros
        }
    }

    fun salvarQtdPares(qtd: Int?) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                if (qtd != null) {
                    preferences[PreferencesKeys.QTD_PARES] = qtd
                } else {
                    preferences.remove(PreferencesKeys.QTD_PARES)
                }
            }
            _qtdPares.value = qtd
        }
    }

    fun resetarFiltros() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences.clear() // Limpa todas as preferências de filtros
            }
            // Reseta os StateFlows para os valores padrão
            _numerosFixos.value = emptySet()
            _numerosExcluidos.value = emptySet()
            _qtdPares.value = null
            // Resetar outros StateFlows aqui
        }
    }
} 