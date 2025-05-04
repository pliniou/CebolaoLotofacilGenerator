package com.exemplo.cebolao.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.repository.JogoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.exemplo.cebolao.data.*
import com.exemplo.cebolao.utils.LotofacilUtils
import com.exemplo.cebolao.utils.LotofacilUtils.calculateSum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import com.exemplo.cebolao.utils.LotofacilUtils.numbersToString
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Exception
import java.lang.RuntimeException

class MainViewModel(private val repository: JogoRepository) : ViewModel() {

    
    private val _games = MutableStateFlow<List<Jogo>>(emptyList())
    val games: StateFlow<List<Jogo>> = _games


    private val _selectedFilters = MutableStateFlow<List<String>>(emptyList())
    val selectedFilters: StateFlow<List<String>> = _selectedFilters.asStateFlow()

    private val _filtersStatus = MutableStateFlow(false)
    val filtersStatus: StateFlow<Boolean> = _filtersStatus.asStateFlow()

    private val _jogosFavoritos = MutableStateFlow<List<Jogo>>(emptyList())
    val jogosFavoritos: StateFlow<List<Jogo>> = _jogosFavoritos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFavoritos()
    } 
    private fun insertJogo(jogo: Jogo) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)
                repository.insertJogo(jogoEntity)                
            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao inserir jogo: ${e.message}")
                throw RuntimeException("Erro ao inserir jogo", e)
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

    fun loadGames() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
               val jogos = repository.getAllJogos()
                val jogosMapped = jogos.map { jogoEntity -> mapJogoEntityToJogo(jogoEntity) }
                _games.value = jogosMapped
           } catch (e: Exception) {
                Log.e("MainViewModel", "Erro ao carregar jogos: ${e.message}")                                                
                _games.update {
                    emptyList()
                
                        throw RuntimeException("Erro ao carregar jogos", e)
                        }
           }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao carregar jogos: ${e.message}")
               throw RuntimeException("Erro ao carregar jogos", e)
            }
        }
    }

    fun loadFavoritos() {
        viewModelScope.launch(Dispatchers.IO) {
           _isLoading.update { true }
            try{
                val favoritos = repository.getFavoritos()
                val favoritosMapped = favoritos.map { jogoEntity -> mapJogoEntityToJogo(jogoEntity) }
                _jogosFavoritos.value = favoritosMapped
               
                
            }catch (e: Exception){
                 _jogosFavoritos.update {
                     emptyList()
                        Log.e("MainViewModel", "Erro ao carregar jogos favoritos: ${e.message}")
                throw RuntimeException("Erro ao carregar favoritos", e)
                        }

            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao carregar jogos favoritos: ${e.message}")
                throw RuntimeException("Erro ao carregar favoritos", e)
            }
        }
    }

    fun generateGames(numberOfGames: Int, filters: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
             try {
                for (i in 0 until numberOfGames) {
                    var game: List<Int>
                    var jogoEntity: JogoEntity
                    do{
                        game = LotofacilUtils.generateGame()                        
                    }while (!checkFilters(game, filters))

                        jogoEntity = JogoEntity(0, numbersToString(game), System.currentTimeMillis(), false)
                        val jogo = Jogo(jogoEntity.id, game, jogoEntity.date, false)
                        try {
                            insertJogo(jogo)
                        } catch (e: Exception) {
                            Log.e("MainViewModel", "Erro ao inserir jogo: ${e.message}")
                        }
                }
            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao gerar jogos: ${e.message}")
               throw RuntimeException("Erro ao gerar jogos", e)
            } catch (e: Exception) {
                throw RuntimeException("Erro ao gerar jogos", e)
            }
        } 
    }

    fun updateJogo(jogo: Jogo) {
        viewModelScope.launch(Dispatchers.IO) {
            val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)
                        
            val currentGames = _games.value.toMutableList()
            val index = currentGames.indexOfFirst { it.id == jogo.id }
            if (index != -1) {
                currentGames[index] = jogo
                _games.value = currentGames
            }
            try{
                repository.updateJogo(jogoEntity)
                
                
            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao atualizar jogo: ${e.message}")
                //Implementar alguma tratativa se o erro acontecer. Pode ser uma mensagem pra UI, por exemplo.
            }
        }
    }

    fun deleteJogo(jogo: Jogo) {
        viewModelScope.launch(Dispatchers.IO) {
            val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)
            try{
                repository.deleteJogo(jogoEntity)
            }catch (e: Exception){
                Log.e("MainViewModel", "Erro ao deletar jogo: ${e.message}")
                //Implementar alguma tratativa se o erro acontecer. Pode ser uma mensagem pra UI, por exemplo.
            }
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
                "Coluna" -> if (LotofacilUtils.countColumn(game) !in (2..4)) return false
                "Soma" -> if (calculateSum(game) !in (170..225)) return false
            }
        }
        return true
    }

    fun setFilters(filters: List<String>){
        _selectedFilters.value = filters
        _filtersStatus.value = filters.isNotEmpty()
    }

    fun setLoad(isLoading : Boolean){
        _isLoading.value = isLoading
    }


    companion object {
        fun MainViewModelFactory(appDatabaseInstance: AppDatabaseInstance):
                ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = JogoRepository(appDatabaseInstance)
                    return MainViewModel(repository) as T
                }
            }
        }
    }
}