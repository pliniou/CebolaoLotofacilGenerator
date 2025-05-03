package com.exemplo.cebolao.repository

import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.data.JogoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JogoRepository(private val jogoDao: JogoDao) {

    suspend fun insertJogo(jogoEntity: JogoEntity)  {
        try {
            withContext(Dispatchers.IO) {
                jogoDao.insertJogo(jogoEntity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

     suspend fun getAllJogos(): List<JogoEntity> {
        try {
            return withContext(Dispatchers.IO) {
                jogoDao.getAllJogos()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun clearJogos() {
        jogoDao.clearJogos()
    }

    suspend fun getFavoritos(): List<JogoEntity> {
        try {
            return withContext(Dispatchers.IO) {
                jogoDao.getFavoritos()
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