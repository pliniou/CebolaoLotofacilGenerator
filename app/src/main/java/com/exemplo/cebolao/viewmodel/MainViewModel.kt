package com.exemplo.cebolao.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.repository.JogoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.asStateFlow
import com.exemplo.cebolao.utils.LotofacilUtils
import com.exemplo.cebolao.data.*
import com.exemplo.cebolao.utils.LotofacilUtils.calculateSum
import kotlinx.coroutines.flow.update
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Your implementation

    private val _games = MutableStateFlow<List<Jogo>>(emptyList())
    private val _selectedFilters = MutableStateFlow<List<String>>(emptyList())


    init {
        loadFavoritos()
    } 
    fun insertJogo(jogo: Jogo) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)
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
            val jogoEntity = JogoEntity(jogo.id, jogo.numbers.joinToString(","), jogo.date, jogo.favorito)

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
    }


    companion object {
        fun mainViewModelFactory(appDatabaseInstance: AppDatabaseInstance):
                ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = JogoRepository(appDatabaseInstance.jogoDao())
                    return MainViewModel(repository) as T
                }
            }
        }
    }
}