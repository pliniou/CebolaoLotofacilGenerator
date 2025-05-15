package com.example.cebolaolotofacilgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.ui.components.SnackbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JogosViewModel(private val repository: JogoRepository) : ViewModel() {

    // LiveData para jogos
    private val _todosJogos = MutableLiveData<List<Jogo>>()
    val todosJogos: LiveData<List<Jogo>> = _todosJogos

    // LiveData para jogos favoritos
    private val _jogosFavoritos = MutableLiveData<List<Jogo>>()
    val jogosFavoritos: LiveData<List<Jogo>> = _jogosFavoritos

    // LiveData para jogos conferidos
    private val _jogosConferidos = MutableLiveData<List<Jogo>>()
    val jogosConferidos: LiveData<List<Jogo>> = _jogosConferidos

    // LiveData para status da operação
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>()
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    // StateFlow para controlar o estado de carregamento da conferência
    private val _conferenciaEmAndamento = MutableStateFlow(false)
    val conferenciaEmAndamento: StateFlow<Boolean> = _conferenciaEmAndamento.asStateFlow()

    // StateFlow para armazenar o resultado selecionado para conferência
    private val _resultadoSelecionado = MutableStateFlow<Resultado?>(null)
    val resultadoSelecionado: StateFlow<Resultado?> = _resultadoSelecionado.asStateFlow()

    init {
        carregarJogos()
        carregarJogosFavoritos()
        carregarJogosConferidos()
    }

    fun carregarJogos() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    _todosJogos.value = repository.buscarTodosJogos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    fun carregarJogosFavoritos() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    _jogosFavoritos.value = repository.buscarJogosFavoritos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    fun carregarJogosConferidos() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    _jogosConferidos.value = repository.buscarJogosConferidos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.atualizarJogo(jogo.copy(favorito = favorito))
                    carregarJogos()
                    carregarJogosFavoritos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    fun deletarJogo(jogo: Jogo) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.excluirJogo(jogo)
                    carregarJogos()
                    carregarJogosFavoritos()
                    carregarJogosConferidos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    fun limparTodosJogos() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.excluirTodosJogos()
                    carregarJogos()
                    carregarJogosFavoritos()
                    carregarJogosConferidos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }
            
    /**
     * Define o resultado que será usado para conferir os jogos
     * @param resultado O resultado a ser usado para conferência
     */
    fun selecionarResultadoParaConferencia(resultado: Resultado?) {
        _resultadoSelecionado.value = resultado
    }
    
    /**
     * Confere um ou mais jogos contra o resultado selecionado
     * @param jogos Lista de jogos a serem conferidos
     */
    fun conferirJogos(jogos: List<Jogo>) {
        val resultado = _resultadoSelecionado.value
        
        if (resultado == null) {
            SnackbarManager.mostrarMensagem("Selecione um resultado para conferir os jogos")
            return
        }
        
        if (jogos.isEmpty()) {
            SnackbarManager.mostrarMensagem("Nenhum jogo selecionado para conferência")
            return
        }
        
        viewModelScope.launch {
            try {
                _conferenciaEmAndamento.value = true
                _operacaoStatus.value = OperacaoStatus.CARREGANDO
                
                val jogosAtualizados = withContext(Dispatchers.Default) {
                    jogos.map { jogo ->
                        // Contagem de acertos - interseção entre números do jogo e do resultado
                        val acertos = jogo.numeros.intersect(resultado.dezenas.toSet()).size
                        
                        // Cria uma cópia do jogo com os acertos e o ID do resultado
                        jogo.copy(
                            acertos = acertos,
                            concursoConferido = resultado.concurso
                        )
                    }
                }
                
                // Atualiza os jogos no banco de dados
                for (jogo in jogosAtualizados) {
                    repository.atualizarJogo(jogo)
                }
                
                // Recarregar listas
                carregarJogos()
                carregarJogosFavoritos()
                carregarJogosConferidos()
                
                // Resultado da conferência
                val jogos11Mais = jogosAtualizados.count { it.acertos ?: 0 >= 11 }
                val temPremio = jogosAtualizados.any { it.acertos ?: 0 >= 11 }
                
                if (temPremio) {
                    SnackbarManager.mostrarMensagem(
                        "Conferência concluída! Você tem $jogos11Mais jogo(s) com 11 ou mais acertos!"
                    )
                } else {
                    SnackbarManager.mostrarMensagem("Conferência concluída. Nenhum jogo premiado.")
                }
                
                _operacaoStatus.value = OperacaoStatus.SUCESSO
            } catch (e: Exception) {
                _operacaoStatus.value = OperacaoStatus.ERRO
                SnackbarManager.mostrarMensagem("Erro ao conferir jogos: ${e.message}")
            } finally {
                _conferenciaEmAndamento.value = false
            }
        }
    }
    
    /**
     * Limpa a conferência de todos os jogos (define acertos = null, concursoConferido = null)
     */
    fun limparConferencia() {
        viewModelScope.launch {
            try {
                _operacaoStatus.value = OperacaoStatus.CARREGANDO
                
                val jogosConferidos = _jogosConferidos.value ?: emptyList()
                if (jogosConferidos.isEmpty()) {
                    SnackbarManager.mostrarMensagem("Não há jogos conferidos para limpar")
                    _operacaoStatus.value = OperacaoStatus.OCIOSO
                    return@launch
                }
                
                // Limpa a conferência de todos os jogos
                for (jogo in jogosConferidos) {
                    repository.atualizarJogo(
                        jogo.copy(acertos = null, concursoConferido = null)
                    )
                }
                
                // Recarregar listas
                carregarJogos()
                carregarJogosFavoritos()
                carregarJogosConferidos()
                
                SnackbarManager.mostrarMensagem("Conferência limpa com sucesso")
                _operacaoStatus.value = OperacaoStatus.SUCESSO
            } catch (e: Exception) {
                _operacaoStatus.value = OperacaoStatus.ERRO
                SnackbarManager.mostrarMensagem("Erro ao limpar conferência: ${e.message}")
            }
        }
    }
}
