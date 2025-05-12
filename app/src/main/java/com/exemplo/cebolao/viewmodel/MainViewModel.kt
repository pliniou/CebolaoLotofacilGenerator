package com.exemplo.cebolao.viewmodel

import androidx.lifecycle.ViewModel
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.repository.JogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val repository: JogoRepository
) : ViewModel() {

    private val _jogosGerados = MutableStateFlow<List<Jogo>>(emptyList())
    val jogosGerados: StateFlow<List<Jogo>> = _jogosGerados.asStateFlow()

    private val _numerosSelecionados = MutableStateFlow<Set<Int>>(emptySet())

    fun updateJogo(jogo: Jogo) {
        // Implementation remains unchanged
    }

    fun <T> transpose(list: List<List<T>>): List<List<T>> {
        val rowCount = list.size
        if (rowCount == 0) return emptyList()
        val colCount = list[0].size
        if (colCount == 0) return List(rowCount) { emptyList() }

        return List(colCount) { j ->
            List(rowCount) { i ->
                list[i][j]
            }
        }
    }
}