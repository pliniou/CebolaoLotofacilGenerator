package com.example.cebolaolotofacilgenerator.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cebolaolotofacilgenerator.data.modelo.Jogo

class JogosViewModel : ViewModel() {
    // Supondo que Jogo é a entidade do seu banco de dados
    private val _todosJogos = MutableLiveData<List<Jogo>>(emptyList())
    val todosJogos: LiveData<List<Jogo>> get() = _todosJogos

    private val _jogosFavoritos = MutableLiveData<List<Jogo>>(emptyList())
    val jogosFavoritos: LiveData<List<Jogo>> get() = _jogosFavoritos

    private val _jogosConferidos = MutableLiveData<List<Jogo>>(emptyList())
    val jogosConferidos: LiveData<List<Jogo>> get() = _jogosConferidos

    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
        // Implemente a lógica de marcar como favorito
    }

    fun deletarJogo(jogo: Jogo) {
        // Implemente a lógica de deletar jogo
    }

    fun limparTodosJogos() {
        // Implemente a lógica de limpar todos os jogos
    }
}
