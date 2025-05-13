package com.example.cebolaolotofacilgenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/** Entidade que representa um jogo da Lotofácil. */
@Entity(tableName = "jogos")
data class Jogo(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,

        // Alterado para List<Int>
        val numeros: List<Int>,

        // Data de criação do jogo
        val dataCriacao: Date,

        // Flag para marcar o jogo como favorito
        var favorito: Boolean = false, // Use var se for modificar diretamente

        // Resultado da conferência (número de acertos) - null se não foi conferido
        val acertos: Int? = null,

        // ID do concurso com o qual o jogo foi conferido - null se não foi conferido
        val concursoConferido: Long? = null,

        // Características estatísticas do jogo
        val quantidadePares: Int, // Renomeado para consistência
        val quantidadeImpares: Int, // Renomeado para consistência
        val quantidadePrimos: Int, // Renomeado para consistência
        val quantidadeFibonacci: Int, // Renomeado para consistência
        val quantidadeMiolo: Int, // Renomeado para consistência
        val quantidadeMoldura: Int, // Renomeado para consistência (era numerosBorda)
        val quantidadeMultiplosDeTres: Int, // Renomeado para consistência
        val soma: Int // Renomeado para consistência (era somaTotal)
) {
    // Removido: getNumerosComoLista() - Acesso direto a 'numeros'
    // Removido ou adaptado: getNumerosFormatados()
    fun getNumerosFormatados(): String {
        return numeros.joinToString(" - ") // Adaptação simples
    }

    companion object {
        // Método para criar um jogo a partir de uma lista de números
        fun fromList(numerosList: List<Int>): Jogo {
            val numerosOrdenados = numerosList.sorted() // Garante a ordem

            // Calcula as características estatísticas
            val pares = numerosOrdenados.count { it % 2 == 0 }
            val impares = numerosOrdenados.size - pares
            val primos = numerosOrdenados.count { isPrimo(it) }
            val fibonacci = numerosOrdenados.count { isFibonacci(it) }
            val miolo = numerosOrdenados.count { isMiolo(it) }
            val moldura = numerosOrdenados.size - miolo // Calcula a moldura
            val multiplosTres = numerosOrdenados.count { it % 3 == 0 }
            val soma = numerosOrdenados.sum()

            return Jogo(
                    numeros = numerosOrdenados, // Usa a lista ordenada
                    dataCriacao = Date(),
                    quantidadePares = pares,
                    quantidadeImpares = impares,
                    quantidadePrimos = primos,
                    quantidadeFibonacci = fibonacci,
                    quantidadeMiolo = miolo,
                    quantidadeMoldura = moldura, // Usa a moldura calculada
                    quantidadeMultiplosDeTres = multiplosTres,
                    soma = soma // Usa o nome consistente
                    // 'favorito', 'acertos', 'concursoConferido' terão valores padrão
                    )
        }

        // Verifica se um número é primo
        private fun isPrimo(num: Int): Boolean {
            if (num <= 1) return false
            if (num <= 3) return true
            if (num % 2 == 0 || num % 3 == 0) return false

            var i = 5
            while (i * i <= num) {
                if (num % i == 0 || num % (i + 2) == 0) return false
                i += 6
            }
            return true
        }

        // Verifica se um número pertence à sequência de Fibonacci (até o 25)
        private fun isFibonacci(num: Int): Boolean {
            return num in setOf(1, 2, 3, 5, 8, 13, 21)
        }

        // Verifica se um número pertence ao "miolo"
        private fun isMiolo(num: Int): Boolean {
            return num in 7..19
        }
    }
}
