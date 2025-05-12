package com.exemplo.cebolao.data

import androidx.room.PrimaryKey

data class Jogo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dezenas: List<Int>
)