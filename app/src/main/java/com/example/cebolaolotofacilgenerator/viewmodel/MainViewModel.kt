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
// Novas importações para Snackbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class MainViewModel(
        application: Application,
        private val jogoRepository: JogoRepository,
        private val resultadoRepository: ResultadoRepository,
        private val appDataStore: AppDataStore
) : AndroidViewModel(application) {

    // ViewModels secundários para compartilhar entre as telas
    val jogosViewModel = JogosViewModel(jogoRepository)
    val resultadoViewModel = ResultadoViewModel(application)
    // Instanciar FiltrosViewModel primeiro, pois é dependência do GeradorViewModel
    val filtrosViewModel = FiltrosViewModel(application, resultadoRepository)
    val geradorViewModel = GeradorViewModel(application, filtrosViewModel, jogoRepository)

    // LiveData para observar se o primeiro run foi completado
    // TODO: Verifique a dependência androidx.lifecycle:lifecycle-livedata-ktx para habilitar
    // asLiveData()
    val firstRunCompleted: LiveData<Boolean> // = appDataStore.firstRunCompleted.asLiveData()
        get() =
                MutableLiveData<Boolean>().apply {
                    viewModelScope.launch { appDataStore.firstRunCompleted.collect { value = it } }
                }

    /** Enum para definir as opções de tema disponíveis no aplicativo. */
    enum class TemaAplicativo {
        /** Tema claro padrão. */
        CLARO,
        /** Tema escuro padrão. */
        ESCURO,
        /** Usa a configuração de tema do sistema operacional. */
        SISTEMA,
        /** Tema claro com azul como cor primária. */
        AZUL,
        /** Tema claro com verde como cor primária. */
        VERDE,
        /** Tema claro com laranja como cor primária. */
        LARANJA,
        /** Tema claro com ciano como cor primária. */
        CIANO
    }

    /** StateFlow que expõe o tema atual do aplicativo. */
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

    /** Salva a preferência de tema selecionada pelo usuário. */
    fun salvarTemaAplicativo(tema: TemaAplicativo) {
        viewModelScope.launch { appDataStore.salvarTemaAplicativo(tema.ordinal) }
    }

    // ViewModel para gerenciar as preferências do usuário, como filtros e configurações gerais.
    val preferenciasViewModel: PreferenciasViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(PreferenciasViewModel::class.java)

    // LiveData para observar todos os jogos
    val todosJogos: LiveData<List<Jogo>> = jogoRepository.todosJogos

    // LiveData para observar jogos favoritos
    val jogosFavoritos: LiveData<List<Jogo>> = jogoRepository.jogosFavoritos

    // Função para marcar um jogo como favorito
    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
        viewModelScope.launch {
            val jogoAtualizado = jogo.copy(favorito = favorito)
            jogoRepository.atualizarJogo(jogoAtualizado)
            // Atualizar a lista de jogos no JogosViewModel
            jogosViewModel.carregarJogos()
            jogosViewModel.carregarJogosFavoritos()
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
            val resultado = Resultado(dezenas = dezenas.sorted())
            resultadoRepository.inserirResultado(resultado)
            // Atualizar a lista de resultados no ResultadoViewModel
            resultadoViewModel.carregarResultados()
        }
    }

    // Snackbar
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
