package com.exemplo.cebolao.repository

import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.mapper.mapJogoEntityToJogo
import com.exemplo.cebolao.mapper.mapJogoToJogoEntity
import com.exemplo.cebolao.model.Jogo

class JogoRepository(private val jogoDao: JogoDao) {

    fun insert(jogo: Jogo) {
        val jogoEntity = mapJogoToJogoEntity(jogo)
        jogoDao.insertJogo(jogoEntity)
    }

    fun getFavoritos(): List<Jogo> {
        return jogoDao.getFavoritos().map { mapJogoEntityToJogo(it) }
    }

    fun updateJogo(jogo: Jogo) {
        val jogoEntity = mapJogoToJogoEntity(jogo)
        jogoDao.updateJogo(jogoEntity)
    }
}
