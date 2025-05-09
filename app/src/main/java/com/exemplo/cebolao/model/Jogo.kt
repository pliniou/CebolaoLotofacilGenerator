package com.exemplo.cebolao.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jogo_table")
data class Jogo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Added default value for Room
    @ColumnInfo(name = "numeros")
    val numbers: List<Int>,
    @ColumnInfo(name = "data")
    val date: Long,
    val favorito: Boolean
)