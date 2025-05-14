package com.example.cebolaolotofacilgenerator.util

import com.example.cebolaolotofacilgenerator.data.model.Jogo
import kotlin.random.Random

/** Classe utilitária para geração de jogos da Lotofácil com filtros estatísticos. */
class GeradorJogos {

    companion object {
        // Constantes
        private const val TOTAL_NUMEROS = 25 // Lotofácil usa números de 1 a 25
        private const val MIN_NUMEROS = 15
        private const val MAX_NUMEROS = 20

        // Valores de Fibonacci em Lotofácil (1-25)
        private val FIBONACCI = setOf(1, 2, 3, 5, 8, 13, 21)

        // Números primos em Lotofácil (1-25)
        private val PRIMOS = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23)

        // Números no miolo do volante (centro)
        private val MIOLO = setOf(7, 8, 9, 12, 13, 14, 17, 18, 19)

        // Números na moldura do volante (bordas)
        private val MOLDURA = setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25)

        /**
         * Gera jogos da Lotofácil com base nos filtros especificados.
         *
         * @param quantidadeJogos Quantidade de jogos a serem gerados
         * @param quantidadeNumeros Quantidade de números por jogo (15 a 20)
         * @param numerosFixos Lista de números que devem estar presentes em todos os jogos
         * @param numerosExcluidos Lista de números que não devem estar presentes em nenhum jogo
         * @param minPares Mínimo de números pares (null para ignorar)
         * @param maxPares Máximo de números pares (null para ignorar)
         * @param minPrimos Mínimo de números primos (null para ignorar)
         * @param maxPrimos Máximo de números primos (null para ignorar)
         * @param minFibonacci Mínimo de números de Fibonacci (null para ignorar)
         * @param maxFibonacci Máximo de números de Fibonacci (null para ignorar)
         * @param minMiolo Mínimo de números do miolo (null para ignorar)
         * @param maxMiolo Máximo de números do miolo (null para ignorar)
         * @param minMultiplosTres Mínimo de múltiplos de 3 (null para ignorar)
         * @param maxMultiplosTres Máximo de múltiplos de 3 (null para ignorar)
         * @param minSoma Valor mínimo para a soma dos números (null para ignorar)
         * @param maxSoma Valor máximo para a soma dos números (null para ignorar)
         * @return Lista de jogos gerados que atendem aos critérios
         */
        fun gerarJogos(
                quantidadeJogos: Int,
                quantidadeNumeros: Int,
                numerosFixos: List<Int> = emptyList(),
                numerosExcluidos: List<Int> = emptyList(),
                minPares: Int? = null,
                maxPares: Int? = null,
                minPrimos: Int? = null,
                maxPrimos: Int? = null,
                minFibonacci: Int? = null,
                maxFibonacci: Int? = null,
                minMiolo: Int? = null,
                maxMiolo: Int? = null,
                minMultiplosTres: Int? = null,
                maxMultiplosTres: Int? = null,
                minSoma: Int? = null,
                maxSoma: Int? = null
        ): List<Jogo> {
            // Validações iniciais
            if (quantidadeNumeros !in MIN_NUMEROS..MAX_NUMEROS) {
                throw IllegalArgumentException(
                        "Quantidade de números deve estar entre $MIN_NUMEROS e $MAX_NUMEROS"
                )
            }

            if (quantidadeJogos <= 0) {
                throw IllegalArgumentException("Quantidade de jogos deve ser maior que zero")
            }

            if (numerosFixos.size > quantidadeNumeros) {
                throw IllegalArgumentException(
                        "Quantidade de números fixos não pode ser maior que a quantidade de números por jogo"
                )
            }

            if (TOTAL_NUMEROS - numerosExcluidos.size < quantidadeNumeros) {
                throw IllegalArgumentException(
                        "Muitos números excluídos, impossível gerar jogos com a quantidade solicitada"
                )
            }

            // Validar que os números fixos e excluídos não se sobrepõem
            val sobreposicao = numerosFixos.intersect(numerosExcluidos.toSet())
            if (sobreposicao.isNotEmpty()) {
                throw IllegalArgumentException(
                        "Há números que estão tanto na lista de fixos quanto na lista de excluídos: $sobreposicao"
                )
            }

            val jogosGerados = mutableListOf<Jogo>()
            val tentativasMaximas =
                    quantidadeJogos * 100 // Limite de tentativas para evitar loop infinito
            var tentativas = 0

            while (jogosGerados.size < quantidadeJogos && tentativas < tentativasMaximas) {
                tentativas++

                try {
                    // Inicia com os números fixos
                    val numerosSorteados = numerosFixos.toMutableList()

                    // Determina quantos números ainda precisam ser sorteados
                    val numerosRestantes = quantidadeNumeros - numerosSorteados.size

                    // Lista de números disponíveis para sorteio (excluindo fixos e excluídos)
                    val numerosDisponiveis =
                            (1..TOTAL_NUMEROS)
                                    .filter { it !in numerosFixos && it !in numerosExcluidos }
                                    .shuffled(Random(System.nanoTime()))
                                    .toMutableList()

                    // Adiciona números aleatórios até completar a quantidade desejada
                    for (i in 0 until numerosRestantes) {
                        if (numerosDisponiveis.isEmpty()) break

                        numerosSorteados.add(numerosDisponiveis.removeAt(0))
                    }

                    // Se não conseguiu completar a quantidade de números, pula para a próxima
                    // tentativa
                    if (numerosSorteados.size < quantidadeNumeros) continue

                    // Verifica se o jogo atende aos critérios estatísticos
                    if (!verificarCriteriosEstatisticos(
                                    numerosSorteados,
                                    minPares,
                                    maxPares,
                                    minPrimos,
                                    maxPrimos,
                                    minFibonacci,
                                    maxFibonacci,
                                    minMiolo,
                                    maxMiolo,
                                    minMultiplosTres,
                                    maxMultiplosTres,
                                    minSoma,
                                    maxSoma
                            )
                    ) {
                        continue
                    }

                    // Verifica se o jogo já foi gerado anteriormente
                    val jogoOrdenado = numerosSorteados.sorted()

                    if (jogosGerados.any { it.numeros == jogoOrdenado }) {
                        continue
                    }

                    // Adiciona o jogo à lista de jogos gerados
                    jogosGerados.add(Jogo.fromList(jogoOrdenado))
                } catch (e: Exception) {
                    // Ignora exceções e continua tentando
                    continue
                }
            }

            return jogosGerados
        }

        /** Verifica se um jogo atende aos critérios estatísticos configurados. */
        private fun verificarCriteriosEstatisticos(
                numeros: List<Int>,
                minPares: Int?,
                maxPares: Int?,
                minPrimos: Int?,
                maxPrimos: Int?,
                minFibonacci: Int?,
                maxFibonacci: Int?,
                minMiolo: Int?,
                maxMiolo: Int?,
                minMultiplosTres: Int?,
                maxMultiplosTres: Int?,
                minSoma: Int?,
                maxSoma: Int?
        ): Boolean {
            // Contagem de números pares
            val pares = numeros.count { it % 2 == 0 }
            if (minPares != null && pares < minPares) return false
            if (maxPares != null && pares > maxPares) return false

            // Contagem de números primos
            val primos = numeros.count { it in PRIMOS }
            if (minPrimos != null && primos < minPrimos) return false
            if (maxPrimos != null && primos > maxPrimos) return false

            // Contagem de números de Fibonacci
            val fibonacci = numeros.count { it in FIBONACCI }
            if (minFibonacci != null && fibonacci < minFibonacci) return false
            if (maxFibonacci != null && fibonacci > maxFibonacci) return false

            // Contagem de números no miolo
            val miolo = numeros.count { it in MIOLO }
            if (minMiolo != null && miolo < minMiolo) return false
            if (maxMiolo != null && miolo > maxMiolo) return false

            // Contagem de múltiplos de 3
            val multiplosTres = numeros.count { it % 3 == 0 }
            if (minMultiplosTres != null && multiplosTres < minMultiplosTres) return false
            if (maxMultiplosTres != null && multiplosTres > maxMultiplosTres) return false

            // Soma dos números
            val soma = numeros.sum()
            if (minSoma != null && soma < minSoma) return false
            if (maxSoma != null && soma > maxSoma) return false

            return true
        }
    }
}
