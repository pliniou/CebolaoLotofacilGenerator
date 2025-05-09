package com.exemplo.cebolao.data

import androidx.room.Dao
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface JogoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJogo(jogo: JogoEntity)

    @Query("SELECT * FROM jogo_table")
    fun getAllJogos(): List<JogoEntity>

    @Query("DELETE FROM jogo_table")
    fun clearJogos()

    @Query("SELECT * FROM jogo_table WHERE favorito = 1")
    fun getFavoritos(): List<JogoEntity>

    @Update
    fun updateJogo(jogo: JogoEntity)
}