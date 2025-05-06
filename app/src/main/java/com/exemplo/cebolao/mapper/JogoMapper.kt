package com.exemplo.cebolao.mapper

import com.exemplo.cebolao.data.JogoEntity
import com.exemplo.cebolao.model.Jogo

fun mapJogoToJogoEntity(jogo: Jogo): JogoEntity {
    return JogoEntity(
        id = jogo.id,
        numbers = jogo.numbers.joinToString(","),
        date = jogo.date,
        favorito = jogo.favorito
    )
}

fun mapJogoEntityToJogo(jogoEntity: JogoEntity): Jogo {
    return Jogo(
        id = jogoEntity.id,
        numbers = jogoEntity.numbers.split(",").map { it.trim().toInt() },
        date = jogoEntity.date,
        favorito = jogoEntity.favorito
    )
}