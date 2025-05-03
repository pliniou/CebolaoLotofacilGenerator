package com.exemplo.cebolao.viewmodel

import androidx.lifecycle.ViewModel
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.repository.JogoRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.exemplo.cebolao.data.*
import com.exemplo.cebolao.utils.Utils
import com.exemplo.cebolao.utils.LotofacilUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(private val repository: JogoRepository) : ViewModel() {

    
    private val _games = MutableStateFlow<List<Jogo>>(emptyList())
    val games: StateFlow<List<Jogo>> = _games

    private val _selectedFilters = MutableStateFlow<List<String>>(emptyList())
    val selectedFilters: StateFlow<List<String>> = _selectedFilters

    private val _filtersStatus = MutableStateFlow<Boolean>(false)
    val filtersStatus: StateFlow<Boolean> = _filtersStatus.asStateFlow()


    fun insertJogo(jogo: Jogo) {
        viewModelScope.launch {
            val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)
            repository.insertJogo(jogoEntity)
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

    fun loadGames() {
        
        viewModelScope.launch {
            repository.getAllJogos().collect{ jogosEntities -> 
                _games.emit(jogosEntities.map { jogoEntity ->
                        mapJogoEntityToJogo(jogoEntity)
                    })
                }
        }
    }

    fun loadFavoritos() {
        viewModelScope.launch {
           repository.getFavoritos().collect{ jogosEntities -> 
               
               _games.emit(jogosEntities.map { jogoEntity ->
                
                    mapJogoEntityToJogo(jogoEntity)
                }
            }
        }
    }

    fun generateGames(numberOfGames: Int, filters: List<String>) {
        val newGames = mutableListOf<Jogo>()
        viewModelScope.launch {
            for (i in 0 until numberOfGames) {
                var game: List<Int>
                var jogoEntity: JogoEntity
                do {
                    game = LotofacilUtils.generateGame()
                    val checkFilters = checkFilters(game, filters)
                    if(checkFilters){
                        jogoEntity = JogoEntity(0, Utils.numbersToString(game), System.currentTimeMillis(), false)
                        newGames.add(Jogo(jogoEntity.id, game, jogoEntity.date, false))
                        repository.insertJogo(jogoEntity)
                    }
                } while (!checkFilters(game, filters))

                }
        } 
    }

    fun updateJogo(jogo: Jogo){
        viewModelScope.launch {
            val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)
            
            
            val currentGames = _games.value.toMutableList()
            val index = currentGames.indexOfFirst { it.id == jogo.id }
            if (index != -1) {
                currentGames[index] = jogo
                _games.emit(currentGames)
            }
            
            repository.updateJogo(jogoEntity)
            
            
        }
    }
    
    private fun checkFilters(game: List<Int>, filters: List<String>): Boolean {
        for (filter in filters) {
            when (filter) {
                "Primos" -> if (LotofacilUtils.countPrimes(game) !in 3..6) return false
                "Pares" -> if (LotofacilUtils.countPairs(game) !in 7..8) return false
                "Cruz" -> if (LotofacilUtils.countCross(game) !in 4..6) return false
                "Moldura" -> if (LotofacilUtils.countFrame(game) !in 9..11) return false
                "Metade Superior" -> if (LotofacilUtils.countUpperHalf(game) !in 7..8) return false
                "Progressão Aritmética" -> if (LotofacilUtils.countArithmeticProgression(game) !in 5..6) return false
                "Sequência" -> if (LotofacilUtils.countSequence(game) !in 3..5) return false
                "Linha" -> if (LotofacilUtils.countLine(game) !in 2..4) return false
                "Coluna" -> if (LotofacilUtils.countColumn(game) !in 2..4) return false
                "Soma" -> if (LotofacilUtils.calculateSum(game) !in 170..225) return false
            }
        }
        return true
    }

    fun setFilters(filters: List<String>){
        _selectedFilters.value = filters
        _filtersStatus.value = filters.isNotEmpty()
    }

}