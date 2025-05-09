package com.exemplo.cebolao.repository

import android.util.Log
import com.exemplo.cebolao.data.JogoEntity
import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.mapper.mapJogoToJogoEntity

class JogoRepository(private val jogoDao: JogoDao) {
    suspend fun insert(jogo: Jogo) {
 val jogoEntity = mapJogoToJogoEntity(jogo)
        jogoDao.insertJogo(jogoEntity)
    }
    fun getAllJogos() = jogoDao.getAllJogos()
}