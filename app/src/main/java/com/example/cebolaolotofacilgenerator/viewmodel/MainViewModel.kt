package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
        application: Application,
        private val jogoRepository: JogoRepository,
        private val resultadoRepository: ResultadoRepository,
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

    // Enum para as opções de tema (pode ser movido para um arquivo/local mais adequado se
    // necessário)
    enum class TemaAplicativo {
        CLARO,
        ESCURO,
        SISTEMA
    }

    // StateFlow para o tema do aplicativo
    val temaAplicativo: StateFlow<TemaAplicativo> =
            appDataStore
                    .temaAplicativo // Flow<Int> com o ordinal
                    .map { ordinal ->
                        TemaAplicativo.values().getOrElse(ordinal) {
                            TemaAplicativo.SISTEMA
                        } // Converte ordinal para Enum
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = TemaAplicativo.SISTEMA // Valor inicial padrão
                    )

    // Função para atualizar o tema do aplicativo
    fun salvarTemaAplicativo(tema: TemaAplicativo) {
        viewModelScope.launch { appDataStore.salvarTemaAplicativo(tema.ordinal) }
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

    // LiveData/StateFlow para o último resultado
    val ultimoResultado: StateFlow<Resultado?> =
            resultadoRepository
                    .ultimoResultado // Este é LiveData<Resultado>
                    .asFlow() // Converte LiveData<Resultado> para Flow<Resultado?>
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = null
                    )

    // StateFlow para as dezenas selecionadas pelo usuário na tela de resultados
    private val _dezenasSelecionadasUltimoResultado = MutableStateFlow<Set<Int>>(emptySet())
    val dezenasSelecionadasUltimoResultado: StateFlow<Set<Int>> =
            _dezenasSelecionadasUltimoResultado

    // Função para inicializar ou atualizar as dezenas com base no último resultado
    fun carregarDezenasDoUltimoResultado(dezenas: List<Int>?) {
        _dezenasSelecionadasUltimoResultado.value = dezenas?.toSet() ?: emptySet()
    }

    // Função para o usuário modificar uma dezena selecionada
    fun toggleDezenaSelecionadaUltimoResultado(dezena: Int) {
        val atuais = _dezenasSelecionadasUltimoResultado.value.toMutableSet()
        if (atuais.contains(dezena)) {
            atuais.remove(dezena)
        } else {
            atuais.add(dezena)
        }
        _dezenasSelecionadasUltimoResultado.value = atuais
    }

    // Função para salvar o último resultado manualmente informado pelo usuário (apenas dezenas)
    fun salvarUltimoResultado(dezenas: List<Int>) {
        viewModelScope.launch {
            val resultado = Resultado(numeros = dezenas.sorted())
            resultadoRepository.inserirResultado(resultado)
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
