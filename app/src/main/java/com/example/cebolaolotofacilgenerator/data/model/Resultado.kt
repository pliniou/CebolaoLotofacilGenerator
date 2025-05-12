package com.example.cebolaolotofacilgenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/** Entidade que representa um resultado oficial da Lotofácil. */
@Entity(tableName = "resultados")
data class Resultado(
        // Número do concurso (usado como chave primária)
        @PrimaryKey val numeroConcurso: Long, // Renomeado para clareza

        // Data do sorteio
        val dataRealizacao: Date, // Renomeado para clareza

        // Alterado para List<Int>
        val numeros: List<Int>,

        // Valor do prêmio principal (15 acertos) em reais
        val premiacao15Acertos: Double,

        // Quantidade de ganhadores com 15 acertos
        val ganhadores15: Int,

        // Valor do prêmio para 14 acertos em reais
        val premiacao14Acertos: Double,

        // Quantidade de ganhadores com 14 acertos
        val ganhadores14: Int,

        // Valor do prêmio para 13 acertos em reais
        val premiacao13Acertos: Double,

        // Quantidade de ganhadores com 13 acertos
        val ganhadores13: Int,

        // Valor do prêmio para 12 acertos em reais
        val premiacao12Acertos: Double,

        // Quantidade de ganhadores com 12 acertos
        val ganhadores12: Int,

        // Valor do prêmio para 11 acertos em reais
        val premiacao11Acertos: Double,

        // Quantidade de ganhadores com 11 acertos
        val ganhadores11: Int
) {
    // Adaptação: getNumerosFormatados()
    fun getNumerosFormatados(): String {
        return numeros.joinToString(" - ") // Adaptação simples
    }

    // Verifica quantos acertos um jogo teve neste resultado
    fun verificarAcertos(jogo: Jogo): Int {
        // Acessa diretamente as listas de números
        val numerosSorteados = this.numeros
        val numerosJogo = jogo.numeros

        return numerosJogo.count { it in numerosSorteados }
    }
}
