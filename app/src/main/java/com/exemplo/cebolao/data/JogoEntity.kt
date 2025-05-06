package com.exemplo.cebolao.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.exemplo.cebolao.model.Jogo // Ensure this import is present if Jogo model is used elsewhere

@Entity(tableName = "jogo_table")
data class JogoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numbers: String,
    val date: Long,
    @ColumnInfo(name = "favorito")
    val favorito: Boolean = false
)