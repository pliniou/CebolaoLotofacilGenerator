package com.example.cebolaolotofacilgenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "resultados")
data class Resultado(
        @PrimaryKey(autoGenerate = true) val id: Long = 0L,
        val concurso: Long = 0L,
        val data: Long = System.currentTimeMillis(),
        val dataSorteio: Date? = null,
        val dezenas: List<Int>,
        val premiacao15Acertos: Double = 0.0,
        val ganhadores15: Int = 0,
        val premiacao14Acertos: Double = 0.0,
        val ganhadores14: Int = 0,
        val premiacao13Acertos: Double = 0.0,
        val ganhadores13: Int = 0,
        val premiacao12Acertos: Double = 0.0,
        val ganhadores12: Int = 0,
        val premiacao11Acertos: Double = 0.0,
        val ganhadores11: Int = 0
) {
    val numeros: List<Int>
        get() = dezenas
        
    fun getNumerosFormatados(): String {
        return dezenas.joinToString(" - ")
    }

    fun verificarAcertos(jogo: Jogo): Int {
        val numerosSorteados = this.dezenas
        val numerosJogo = jogo.numeros

        return numerosJogo.count { it in numerosSorteados }
    }
}
