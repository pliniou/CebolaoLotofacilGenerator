package com.exemplo.cebolao.repository

import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.model.Jogo
import com.exemplo.cebolao.mapper.mapJogoToJogoEntity
import com.exemplo.cebolao.mapper.mapJogoEntityToJogo

class JogoRepository(private val jogoDao: JogoDao) {

    // Insere um novo jogo no banco de dados.
    fun insert(jogo: Jogo) {
        val jogoEntity = mapJogoToJogoEntity(jogo)
        jogoDao.insertJogo(jogoEntity)
    }

    // Retorna os jogos favoritos do banco de dados.
    fun getFavoritos(): List<Jogo> {
        return jogoDao.getFavoritos().map { mapJogoEntityToJogo(it) }
    }

    // Atualiza um jogo existente no banco de dados.
    fun updateJogo(jogo: Jogo) {
        val jogoEntity = mapJogoToJogoEntity(jogo)
        jogoDao.updateJogo(jogoEntity)
    }
}