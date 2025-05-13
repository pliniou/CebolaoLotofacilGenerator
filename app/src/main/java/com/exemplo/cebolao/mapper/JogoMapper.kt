package com.exemplo.cebolao.mapper

import com.exemplo.cebolao.data.JogoEntity
import com.exemplo.cebolao.model.Jogo

fun mapJogoToJogoEntity(jogo: Jogo): JogoEntity =
        JogoEntity(
                id = jogo.id,
                numeros = jogo.numeros,
                dataCriacao = jogo.dataCriacao.time,
                favorito = jogo.favorito,
                acertos = jogo.acertos,
                concursoConferido = jogo.concursoConferido,
                quantidadePares = jogo.quantidadePares,
                quantidadeImpares = jogo.quantidadeImpares,
                quantidadePrimos = jogo.quantidadePrimos,
                quantidadeFibonacci = jogo.quantidadeFibonacci,
                quantidadeMiolo = jogo.quantidadeMiolo,
                quantidadeMoldura = jogo.quantidadeMoldura,
                quantidadeMultiplosDeTres = jogo.quantidadeMultiplosDeTres,
                soma = jogo.soma
        )

fun mapJogoEntityToJogo(jogoEntity: JogoEntity): Jogo =
        Jogo(
                id = jogoEntity.id,
                numeros = jogoEntity.numeros,
                dataCriacao = Date(jogoEntity.dataCriacao),
                favorito = jogoEntity.favorito,
                acertos = jogoEntity.acertos,
                concursoConferido = jogoEntity.concursoConferido,
                quantidadePares = jogoEntity.quantidadePares,
                quantidadeImpares = jogoEntity.quantidadeImpares,
                quantidadePrimos = jogoEntity.quantidadePrimos,
                quantidadeFibonacci = jogoEntity.quantidadeFibonacci,
                quantidadeMiolo = jogoEntity.quantidadeMiolo,
                quantidadeMoldura = jogoEntity.quantidadeMoldura,
                quantidadeMultiplosDeTres = jogoEntity.quantidadeMultiplosDeTres,
                soma = jogoEntity.soma
        )
