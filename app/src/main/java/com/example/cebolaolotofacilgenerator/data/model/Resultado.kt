package com.example.cebolaolotofacilgenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "resultados")
data class Resultado(
    @PrimaryKey val concurso: Long,
    val dataSorteio: Date,
    val numeros: List<Int>,
    val premiacao15Acertos: Double,
    val ganhadores15: Int,
    val premiacao14Acertos: Double,
    val ganhadores14: Int,
    val premiacao13Acertos: Double,
    val ganhadores13: Int,
    val premiacao12Acertos: Double,
    val ganhadores12: Int,
    val premiacao11Acertos: Double,
    val ganhadores11: Int
) {
    fun getNumerosFormatados(): String {
        return numeros.joinToString(" - ")
    }

    fun verificarAcertos(jogo: Jogo): Int {
        val numerosSorteados = this.numeros
        val numerosJogo = jogo.numeros

        return numerosJogo.count { it in numerosSorteados }
    }
}
