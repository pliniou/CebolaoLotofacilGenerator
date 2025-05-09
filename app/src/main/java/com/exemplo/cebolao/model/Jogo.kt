package com.exemplo.cebolao.model


// This data class is likely used in the domain or presentation layer
data class Jogo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Added default value for Room
    @ColumnInfo(name = "numeros")
    val numbers: List<Int>,
    @ColumnInfo(name = "data")
    val date: Long,
    val favorito: Boolean
)