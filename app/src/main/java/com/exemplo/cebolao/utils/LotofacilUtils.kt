// Arquivo legado, esvaziado para evitar conflitos

package com.example.cebolaolotofacilgenerator.utils

import com.example.cebolaolotofacilgenerator.data.model.Jogo
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class LotofacilUtils {

    companion object {

        private const val TOTAL_NUMBERS = 25
        private const val SELECTED_NUMBERS = 15

        fun generateGame(): Jogo {
            val allNumbers = (1..TOTAL_NUMBERS).toMutableList()
            val selectedNumbers = mutableSetOf<Int>()

            while (selectedNumbers.size < SELECTED_NUMBERS) {
                val randomIndex = Random.nextInt(allNumbers.size)
                selectedNumbers.add(allNumbers.removeAt(randomIndex))
            }

            val numerosOrdenados = selectedNumbers.toList().sorted()

            // Calcula as características estatísticas
            val pares = numerosOrdenados.count { it % 2 == 0 }
            val impares = numerosOrdenados.size - pares
            val primos = numerosOrdenados.count { isPrimo(it) }
            val fibonacci = numerosOrdenados.count { isFibonacci(it) }
            val miolo = numerosOrdenados.count { isMiolo(it) }
            val moldura = numerosOrdenados.size - miolo
            val multiplosTres = numerosOrdenados.count { it % 3 == 0 }
            val soma = numerosOrdenados.sum()

            return Jogo(
                    numeros = numerosOrdenados,
                    dataCriacao = Date(),
                    favorito = false,
                    quantidadePares = pares,
                    quantidadeImpares = impares,
                    quantidadePrimos = primos,
                    quantidadeFibonacci = fibonacci,
                    quantidadeMiolo = miolo,
                    quantidadeMoldura = moldura,
                    quantidadeMultiplosDeTres = multiplosTres,
                    soma = soma
            )
        }

        fun calculateSum(numbers: List<Int>): Int {
            return numbers.sum()
        }

        fun countPairs(numbers: List<Int>): Int {
            return numbers.count { it % 2 == 0 }
        }

        fun countCross(numbers: List<Int>): Int {
            val crossNumbers = setOf(7, 8, 9, 12, 13, 14, 17, 18, 19)
            return numbers.count { it in crossNumbers }
        }

        fun countFrame(numbers: List<Int>): Int {
            val frameNumbers = setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25)
            return numbers.count { it in frameNumbers }
        }

        fun countUpperHalf(numbers: List<Int>): Int {
            return numbers.count { it <= 13 }
        }

        fun countArithmeticProgression(numbers: List<Int>): Int {
            if (numbers.size < 3) return 0

            var count = 0
            for (i in 0 until numbers.size - 2) {
                for (j in i + 1 until numbers.size - 1) {
                    val diff = numbers[j] - numbers[i]
                    if (diff <= 0) continue
                    if (numbers.subList(j + 1, numbers.size).any { it == numbers[j] + diff }) {
                        count++
                    }
                }
            }
            return count
        }

        fun countSequence(numbers: List<Int>): Int {
            if (numbers.size < 3) return 0

            var maxSequence = 0
            var currentSequence = 1
            for (i in 1 until numbers.size) {
                if (numbers[i] == numbers[i - 1] + 1) {
                    currentSequence++
                } else {
                    maxSequence = maxOf(maxSequence, currentSequence)
                    currentSequence = 1
                }
            }
            maxSequence = maxOf(maxSequence, currentSequence)
            return maxSequence
        }

        fun countLine(numbers: List<Int>): Int {
            val lines =
                    listOf(
                            (1..5).toSet(),
                            (6..10).toSet(),
                            (11..15).toSet(),
                            (16..20).toSet(),
                            (21..25).toSet()
                    )
            return lines.count { line -> numbers.any { it in line } }
        }

        fun countColumn(numbers: List<Int>): Int {
            val columns =
                    listOf(
                            setOf(1, 6, 11, 16, 21),
                            setOf(2, 7, 12, 17, 22),
                            setOf(3, 8, 13, 18, 23),
                            setOf(4, 9, 14, 19, 24),
                            setOf(5, 10, 15, 20, 25)
                    )
            return columns.count { column -> numbers.any { it in column } }
        }

        fun filterBySoma(jogos: List<Jogo>, minSoma: Int, maxSoma: Int): List<Jogo> {
            return jogos.filter { jogo -> jogo.soma in minSoma..maxSoma }
        }

        fun filterByParImpar(jogos: List<Jogo>, minPar: Int, maxPar: Int): List<Jogo> {
            return jogos.filter { jogo -> jogo.quantidadePares in minPar..maxPar }
        }

        fun filterByMultiplo(
                jogos: List<Jogo>,
                multiplo: Int,
                minCount: Int,
                maxCount: Int
        ): List<Jogo> {
            return jogos.filter { jogo ->
                val count = jogo.numeros.count { it % multiplo == 0 }
                count in minCount..maxCount
            }
        }

        fun gerarNumerosAleatorios(quantidade: Int, numerosFixos: List<Int>): List<Int> {
            val numerosDisponiveis = (1..25).toMutableList()
            numerosDisponiveis.removeAll(numerosFixos)

            val numerosAleatorios = mutableListOf<Int>()
            numerosAleatorios.addAll(numerosFixos)

            val quantidadeFaltante = quantidade - numerosFixos.size
            if (quantidadeFaltante > 0 && numerosDisponiveis.size >= quantidadeFaltante) {
                repeat(quantidadeFaltante) {
                    val indiceAleatorio = Random.nextInt(numerosDisponiveis.size)
                    numerosAleatorios.add(numerosDisponiveis.removeAt(indiceAleatorio))
                }
            } else if (quantidadeFaltante > 0) {
                numerosAleatorios.addAll(numerosDisponiveis)
            }

            return numerosAleatorios.sorted()
        }

        fun obterDataAtual(): Triple<Int, Int, Int> {
            val calendario = Calendar.getInstance()
            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH) + 1
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            return Triple(ano, mes, dia)
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
