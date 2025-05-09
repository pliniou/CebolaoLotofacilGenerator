package com.exemplo.cebolao.repository

import android.util.Log
import com.exemplo.cebolao.data.JogoDao
import com.exemplo.cebolao.model.Jogo

class JogoRepository(private val jogoDao: JogoDao) {
    suspend fun insert(jogo: Jogo) = jogoDao.insert(jogo)
    fun getAllJogos() = jogoDao.getAllJogos()
}