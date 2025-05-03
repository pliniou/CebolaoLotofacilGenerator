package com.exemplo.cebolao.repository

import android.util.Log
import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.data.JogoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class JogoRepository(private val jogoDao: JogoDao) {

    suspend fun insertJogo(jogoEntity: JogoEntity)  {
        try {
            withContext(Dispatchers.IO) {
                jogoDao.insertJogo(jogoEntity)
            }
        } catch (e: Exception) {
            Log.e("JogoRepository", "Erro ao inserir jogo: ${e.message}", e)
            throw e
        }
    }

     suspend fun getAllJogos(): List<JogoEntity> {
        try {
            return withContext(Dispatchers.IO) {
                jogoDao.getAllJogos()
            }
        } catch (e: Exception) {
            Log.e("JogoRepository", "Erro ao recuperar jogos: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun clearJogos() {
       try {
           withContext(Dispatchers.IO) {
               jogoDao.clearJogos()
           }
       }catch (e: Exception) {
           Log.e("JogoRepository", "Erro ao limpar jogos: ${e.message}", e)
       }
    }

    suspend fun getFavoritos(): List<JogoEntity> {
        try {
            return withContext(Dispatchers.IO) {
                jogoDao.getFavoritos()

            }

        } catch (e: Exception) {
            Log.e("JogoRepository", "Erro ao recuperar favoritos: ${e.message}", e)
            return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    suspend fun updateJogo(jogoEntity: JogoEntity){
        try {
            jogoDao.updateJogo(jogoEntity)
        }catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }
}