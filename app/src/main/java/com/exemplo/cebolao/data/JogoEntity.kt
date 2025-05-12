package com.exemplo.cebolao.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "jogo_table")
data class JogoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numbers: List<Int>,
    val date: Long,
    @ColumnInfo(name = "favorito")
    val favorito: Boolean = false,
    val dataGeracao: String
)