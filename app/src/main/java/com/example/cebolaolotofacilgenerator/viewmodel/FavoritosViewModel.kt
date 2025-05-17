package com.example.cebolaolotofacilgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import kotlinx.coroutines.launch

class FavoritosViewModel(private val jogoRepository: JogoRepository) : ViewModel() {

    val jogosFavoritos: LiveData<List<Jogo>> = jogoRepository.jogosFavoritos

    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
        viewModelScope.launch {
            val jogoAtualizado = jogo.copy(favorito = favorito)
            jogoRepository.atualizarJogo(jogoAtualizado)
        }
    }

    fun excluirJogo(jogo: Jogo) {
        viewModelScope.launch {
            jogoRepository.excluirJogo(jogo)
        }
    }
}

class FavoritosViewModelFactory(
    private val jogoRepository: JogoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritosViewModel(jogoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}