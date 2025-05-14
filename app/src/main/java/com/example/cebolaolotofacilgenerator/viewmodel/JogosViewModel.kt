package com.example.cebolaolotofacilgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import kotlinx.coroutines.launch

class JogosViewModel(private val repository: JogoRepository) : ViewModel() {

    // LiveData para jogos
    private val _jogos = MutableLiveData<List<Jogo>>()
    val jogos: LiveData<List<Jogo>> = _jogos

    // LiveData para jogos favoritos
    private val _jogosFavoritos = MutableLiveData<List<Jogo>>()
    val jogosFavoritos: LiveData<List<Jogo>> = _jogosFavoritos

    // LiveData para status da operação
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>()
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    init {
        carregarJogos()
        carregarJogosFavoritos()
    }

    fun carregarJogos() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    _jogos.value = repository.buscarTodosJogos()
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

    fun excluirJogo(jogo: Jogo) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.excluirJogo(jogo)
                    carregarJogos()
                    carregarJogosFavoritos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    fun excluirTodosJogos() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.excluirTodosJogos()
                    carregarJogos()
                    carregarJogosFavoritos()
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }
}
