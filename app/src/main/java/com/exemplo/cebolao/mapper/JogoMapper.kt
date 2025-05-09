package com.exemplo.cebolao.mapper

import com.exemplo.cebolao.data.JogoEntity
import com.exemplo.cebolao.model.Jogo

fun mapJogoToJogoEntity(jogo: Jogo): JogoEntity =
    JogoEntity(
        id = jogo.id,
        numbers = jogo.numbers,
        date = jogo.date,
        favorito = jogo.favorito
    )

fun mapJogoEntityToJogo(jogoEntity: JogoEntity): Jogo =
    Jogo(
        id = jogoEntity.id,
        numbers = jogoEntity.numbers,
        date = jogoEntity.date,
        favorito = jogoEntity.favorito
    )