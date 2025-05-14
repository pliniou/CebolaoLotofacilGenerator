package com.example.cebolaolotofacilgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import kotlinx.coroutines.launch

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
}
