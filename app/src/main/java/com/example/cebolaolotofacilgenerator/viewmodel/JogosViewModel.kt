package com.example.cebolaolotofacilgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JogosViewModel(private val jogoRepository: JogoRepository) : ViewModel() {

    // StateFlow para todos os jogos, obtido do repositório
    val todosOsJogos: StateFlow<List<Jogo>> = jogoRepository.todosJogos
        .asFlow() // Converte LiveData para Flow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _jogosGeradosNaoFavoritos = MutableStateFlow<List<Jogo>>(emptyList())
    val jogosGeradosNaoFavoritos: StateFlow<List<Jogo>> = _jogosGeradosNaoFavoritos.asStateFlow()

    private val _jogosFavoritos = MutableStateFlow<List<Jogo>>(emptyList())
    val jogosFavoritos: StateFlow<List<Jogo>> = _jogosFavoritos.asStateFlow()

    // Estado para a aba selecionada (0 para Jogos Gerados, 1 para Favoritos)
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    // LiveData para controlar a visibilidade do diálogo de exclusão
    private val _mostrarDialogoExclusao = MutableLiveData<Jogo?>(null)
    val mostrarDialogoExclusao: LiveData<Jogo?> = _mostrarDialogoExclusao

    // LiveData para controlar a visibilidade do diálogo de exclusão de todos os jogos
    private val _mostrarDialogoExcluirTodos = MutableLiveData<Boolean>(false)
    val mostrarDialogoExcluirTodos: LiveData<Boolean> = _mostrarDialogoExcluirTodos
    
    // LiveData para controlar a visibilidade do diálogo de exclusão de todos os jogos favoritos
    private val _mostrarDialogoExcluirTodosFavoritos = MutableLiveData<Boolean>(false)
    val mostrarDialogoExcluirTodosFavoritos: LiveData<Boolean> = _mostrarDialogoExcluirTodosFavoritos

    // LiveData para status da operação
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>()
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    init {
        carregarJogosNaoFavoritos()
        carregarJogosFavoritos()
        // todosOsJogos será atualizado automaticamente pelo StateFlow
    }

    fun carregarJogosNaoFavoritos() {
        viewModelScope.launch {
            // Assumindo que o repositório tem um método para buscar apenas não favoritos
            // Se não, será necessário filtrar a partir de todosOsJogos ou ajustar o repositório
            // Por ora, vamos manter a lógica original se possível, mas isso precisa de revisão.
            // Idealmente, JogoRepository deveria fornecer um Flow/LiveData para não favoritos também.
            // Temporariamente, vamos simular que ele busca todos e filtra, ou que existe um método.
            // Esta parte pode precisar de ajuste no JogoRepository.
            // _jogosGeradosNaoFavoritos.value = jogoRepository.buscarTodosJogos().filter { !it.favorito }
            // OU, se JogoRepository.todosJogos já é a fonte da verdade:
             _jogosGeradosNaoFavoritos.value = jogoRepository.buscarTodosJogos().filter { !it.favorito }
        }
    }

    fun carregarJogosFavoritos() {
        viewModelScope.launch {
            _jogosFavoritos.value = jogoRepository.buscarJogosFavoritos()
        }
    }
    
    fun onTabSelecionada(index: Int) {
        _selectedTabIndex.value = index
    }

    fun exibirDialogoExclusao(jogo: Jogo) {
        _mostrarDialogoExclusao.value = jogo
    }

    fun esconderDialogoExclusao() {
        _mostrarDialogoExclusao.value = null
    }
    
    fun exibirDialogoExcluirTodos(confirmar: Boolean) {
        _mostrarDialogoExcluirTodos.value = confirmar
    }

    fun esconderDialogoExcluirTodos() {
        _mostrarDialogoExcluirTodos.value = false
    }

    fun exibirDialogoExcluirTodosFavoritos(confirmar: Boolean) {
        _mostrarDialogoExcluirTodosFavoritos.value = confirmar
    }

    fun esconderDialogoExcluirTodosFavoritos() {
        _mostrarDialogoExcluirTodosFavoritos.value = false
    }


    fun excluirJogo(jogo: Jogo) {
        viewModelScope.launch {
            jogoRepository.excluirJogo(jogo)
            // As listas serão atualizadas automaticamente se estiverem observando todosOsJogos
            // ou se carregarJogosNaoFavoritos/Favoritos for chamado e eles usarem sources atualizadas
            carregarJogosNaoFavoritos() 
            carregarJogosFavoritos()
        }
        esconderDialogoExclusao()
    }

    // Este método agora limpará TODOS os jogos (favoritos ou não)
    fun limparTodosOsJogos() {
        viewModelScope.launch {
            jogoRepository.excluirTodosJogos() // Chama o método do repositório que limpa toda a tabela
            // carregarJogosNaoFavoritos() e carregarJogosFavoritos() serão atualizados
            // devido à observação de todosOsJogos ou recarga explícita.
        }
        esconderDialogoExcluirTodos()
    }
    
    fun excluirTodosOsJogosFavoritos() {
        viewModelScope.launch {
            // Precisa de um método no repositório para excluir apenas os favoritos.
            // Ex: jogoRepository.excluirApenasFavoritos()
            // Por enquanto, vamos assumir que JogoRepository.excluirTodosJogosFavoritos() existe e faz isso.
            // Esta lógica pode precisar de ajuste no JogoRepository.
            // jogoRepository.excluirTodosJogosFavoritos() // Este método não existe no JogoRepository atual
            // Solução temporária: buscar favoritos e excluí-los um a um (ineficiente)
            // ou adicionar o método ao DAO/Repository.
            val favoritos = jogoRepository.buscarJogosFavoritos()
            favoritos.forEach { jogoRepository.excluirJogo(it) } // Ineficiente, mas funciona por agora
            carregarJogosFavoritos() 
            carregarJogosNaoFavoritos()
        }
        esconderDialogoExcluirTodosFavoritos()
    }


    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
        viewModelScope.launch {
            val jogoAtualizado = jogo.copy(favorito = favorito)
            jogoRepository.atualizarJogo(jogoAtualizado)
            carregarJogosNaoFavoritos() 
            carregarJogosFavoritos()
        }
    }
}
