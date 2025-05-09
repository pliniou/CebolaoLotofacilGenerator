package com.exemplo.cebolao.repository

import com.exemplo.cebolao.data.JogoEntity
import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.mapper.mapJogoToJogoEntity
import com.exemplo.cebolao.mapper.mapJogoEntityToJogo

class JogoRepository(private val jogoDao: JogoDao) {
    suspend fun insert(jogo: Jogo) {
 val jogoEntity = mapJogoToJogoEntity(jogo)
 jogoDao.insertJogo(jogoEntity)
    }
 fun getAllJogos() = jogoDao.getAllJogos()

    fun getFavoritos(): List<Jogo> {
 return jogoDao.getFavoritos().map { mapJogoEntityToJogo(it) }
    }
    suspend fun updateJogo(jogo: Jogo) {
        val jogoEntity = mapJogoToJogoEntity(jogo)
        jogoDao.updateJogo(jogoEntity)
    }
}