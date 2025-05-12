package com.example.cebolaolotofacilgenerator.util

import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.Resultado

/** Classe utilitária para verificação de resultados e conferência de jogos da Lotofácil. */
class VerificadorJogos {

    companion object {
        /**
         * Verifica quantos acertos um jogo teve em relação a um resultado.
         *
         * @param jogo O jogo a ser verificado
         * @param resultado O resultado oficial para comparação
         * @return O número de acertos (0 a 15)
         */
        fun verificarAcertos(jogo: Jogo, resultado: Resultado): Int {
            val numerosJogo = jogo.numeros
            val numerosSorteados = resultado.numeros

            return numerosJogo.count { it in numerosSorteados }
        }

        /**
         * Confere uma lista de jogos contra um resultado e retorna os jogos com seus respectivos
         * acertos.
         *
         * @param jogos Lista de jogos a serem conferidos
         * @param resultado O resultado oficial para comparação
         * @return Lista de jogos atualizada com a contagem de acertos
         */
        fun conferirJogos(jogos: List<Jogo>, resultado: Resultado): List<Jogo> {
            return jogos.map { jogo ->
                val acertos = verificarAcertos(jogo, resultado)
                jogo.copy(acertos = acertos, concursoConferido = resultado.numeroConcurso)
            }
        }

        /**
         * Classifica os prêmios com base no número de acertos.
         *
         * @param acertos Número de acertos do jogo (0 a 15)
         * @param resultado O resultado oficial com os valores dos prêmios
         * @return Valor do prêmio correspondente aos acertos, ou 0 se não houver premiação
         */
        fun calcularPremio(acertos: Int, resultado: Resultado): Double {
            return when (acertos) {
                15 -> resultado.premiacao15Acertos
                14 -> resultado.premiacao14Acertos
                13 -> resultado.premiacao13Acertos
                12 -> resultado.premiacao12Acertos
                11 -> resultado.premiacao11Acertos
                else -> 0.0
            }
        }

        /**
         * Determina se um jogo foi premiado.
         *
         * @param acertos Número de acertos do jogo
         * @return true se o jogo foi premiado (11 ou mais acertos), false caso contrário
         */
        fun foiPremiado(acertos: Int): Boolean {
            return acertos >= 11
        }

        /**
         * Gera um relatório de conferência para uma lista de jogos.
         *
         * @param jogos Lista de jogos conferidos
         * @param resultado O resultado oficial
         * @return Um mapa com o número de acertos como chave e a quantidade de jogos com esse
         * número de acertos como valor
         */
        fun gerarRelatorioConferencia(jogos: List<Jogo>, resultado: Resultado): Map<Int, Int> {
            val relatorio = mutableMapOf<Int, Int>()

            // Inicializa o relatório com zeros
            for (i in 0..15) {
                relatorio[i] = 0
            }

            // Conta os jogos por número de acertos
            jogos.forEach { jogo ->
                val acertos = jogo.acertos ?: verificarAcertos(jogo, resultado)
                relatorio[acertos] = relatorio.getOrDefault(acertos, 0) + 1
            }

            return relatorio
        }

        /**
         * Calcula o valor total dos prêmios para uma lista de jogos.
         *
         * @param jogos Lista de jogos conferidos
         * @param resultado O resultado oficial com os valores dos prêmios
         * @return O valor total dos prêmios
         */
        fun calcularValorTotalPremios(jogos: List<Jogo>, resultado: Resultado): Double {
            return jogos.sumOf { jogo ->
                val acertos = jogo.acertos ?: verificarAcertos(jogo, resultado)
                calcularPremio(acertos, resultado)
            }
        }
    }
}
