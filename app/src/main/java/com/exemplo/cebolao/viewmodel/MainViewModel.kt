package com.exemplo.cebolao.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplo.cebolao.data.AppDatabaseInstance
import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.data.JogoEntity
import com.exemplo.cebolao.repository.JogoRepository
import com.exemplo.cebolao.utils.LotofacilUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.exemplo.cebolao.utils.LotofacilUtils.calculateSum
import com.exemplo.cebolao.model.Jogo
import java.lang.Exception

class MainViewModel(
    private val repository: JogoRepository
) : ViewModel() {
    
    private val _games = MutableStateFlow<List<Jogo>>(emptyList<Jogo>())
    val games: StateFlow<List<Jogo>> = _games.asStateFlow()
    private val _selectedFilters = MutableStateFlow<List<String>>(emptyList())
    val selectedFilters: StateFlow<List<String>> = _selectedFilters.asStateFlow()
    private val _jogosFavoritos = MutableStateFlow<List<Jogo>>(emptyList())
    val jogosFavoritos: StateFlow<List<Jogo>> = _jogosFavoritos.asStateFlow()
    


    init {
        loadFavoritos()
    } 
    fun insertJogo(jogo: Jogo) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(", "), jogo.date, jogo.favorito)
                repository.insertJogo(jogoEntity)                
            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao inserir jogo: ${e.message}")

            } 
        }
    }

    private fun mapJogoEntityToJogo(jogoEntity: JogoEntity): Jogo {
        return Jogo(
            id = jogoEntity.id,
            numbers = jogoEntity.numbers.split(",").map { it.toInt() },
            date = jogoEntity.date,
            favorito = jogoEntity.favorito
        )
    }

    fun loadFavoritos() {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val favoritos = repository.getFavoritos()
                val favoritosMapped = favoritos.map { jogoEntity -> mapJogoEntityToJogo(jogoEntity) }
                _jogosFavoritos.value = favoritosMapped
               
                
            }catch (e: Exception){
                 _jogosFavoritos.update {
                    emptyList()
                        Log.e("MainViewModel", "Erro ao carregar jogos favoritos: ${e.message}")
                 }
                        }
        }
    }

    fun updateJogo(jogo: Jogo) {
        viewModelScope.launch(Dispatchers.IO) {
            val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(", "), jogo.date, jogo.favorito)

            try{
                repository.updateJogo(jogoEntity)
                
                
            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao atualizar jogo: ${e.message}")
                //Implementar alguma tratativa se o erro acontecer. Pode ser uma mensagem pra UI, por exemplo.
            }
        }
    }

    private fun checkFilters(game: List<Int>, filters: List<String>): Boolean {
        for (filter in filters) {
            when (filter) {
                "Primos" -> if (countPrimes(game) !in 3..6) return false
                "Pares" -> if (countPairs(game) !in 7..8) return false
                "Cruz" -> if (countCross(game) !in 4..6) return false
                "Moldura" -> if (countFrame(game) !in 9..11) return false
                "Metade Superior" -> if (countUpperHalf(game) !in 7..8) return false
                "Progressão Aritmética" -> if (countArithmeticProgression(game) !in 5..6) return false
                "Sequência" -> if (countSequence(game) !in 3..5) return false
                "Linha" -> if (countLine(game) !in 2..4) return false
                "Coluna" -> if (countColumn(game) !in (2..4)) return false
                "Soma" -> if (calculateSum(game) !in (170..225)) return false
            }
        }
        return true
    }
    private fun calculateSum(numbers: List<Int>): Int {
        return numbers.sum()
    }

    private fun countPrimes(numbers: List<Int>): Int {
        val primes = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
        return numbers.count { it in primes }
    }

    private fun countPairs(numbers: List<Int>): Int {
        return numbers.count { it % 2 == 0 }
    }

    private fun countCross(numbers: List<Int>): Int {
        val crossNumbers = setOf(6, 7, 8, 11, 12, 13, 16, 17, 18)
        return numbers.count { it in crossNumbers }
    }

    private fun countFrame(numbers: List<Int>): Int {
        val frameNumbers = (1..5).toSet() + (21..25).toSet() + setOf(6, 11, 16) + setOf(10, 15, 20)
        return numbers.count { it in frameNumbers }
    }

    private fun countUpperHalf(numbers: List<Int>): Int {
        val upperHalfNumbers = (1..13).toSet()
        return numbers.count { it in upperHalfNumbers }
    }

    private fun countArithmeticProgression(numbers: List<Int>): Int {
        var count = 0
        for (i in 0 until numbers.size - 2) {
            val sublist = numbers.subList(i, i + 3).sorted()
            if (sublist[1] - sublist[0] == sublist[2] - sublist[1]) {
                count++
            }
        }
        return count
    }

    private fun countSequence(numbers: List<Int>): Int {
        var count = 0
        val sortedNumbers = numbers.sorted()
        for (i in 0 until sortedNumbers.size - 1) {
            if (sortedNumbers[i + 1] == sortedNumbers[i] + 1) {
                count++
            }
        }
        return count
    }

    private fun countLine(numbers: List<Int>): Int {
        val lines = (1..5).chunked(5) + (6..10).chunked(5) + (11..15).chunked(5) + (16..20).chunked(5) + (21..25).chunked(5)
        return lines.count { line -> numbers.any { it in line } }
    }

    private fun countColumn(numbers: List<Int>): Int {
        val columns = (1..25).chunked(5).transpose()
        return columns.count { column -> numbers.any { it in column } }
    }

    private fun <T> List<List<T>>.transpose(): List<List<T>> =
        if (isEmpty()) emptyList() else (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }

    fun setFilters(filters: List<String>){
        _selectedFilters.value = filters
    }



}