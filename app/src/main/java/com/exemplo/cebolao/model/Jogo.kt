package com.exemplo.cebolao.model

data class Jogo(
    val id: Int = 0, // Added default value for Room
    val numbers: List<Int>,
    val date: Long,
    val favorito: Boolean
)