package com.example.cebolaolotofacilgenerator.util

import com.example.cebolaolotofacilgenerator.data.model.Jogo
import kotlin.random.Random

/** Classe utilitária para geração de jogos da Lotofácil com filtros estatísticos. */
class GeradorJogos {

    // Data classes para configuração de filtros
    data class FiltroRange(val min: Int?, val max: Int?)
    data class FiltroRepeticao(val dezenasAnteriores: List<Int>, val range: FiltroRange?)

    data class ConfiguracaoGeracao(
        val quantidadeJogos: Int,
        val quantidadeNumerosPorJogo: Int,
        val numerosFixos: List<Int> = emptyList(),
        val numerosExcluidos: List<Int> = emptyList(),
        val filtroParesImpares: FiltroRange? = null, // Este agora se refere a ÍMPARES
        val filtroSomaTotal: FiltroRange? = null,
        val filtroPrimos: FiltroRange? = null,
        val filtroFibonacci: FiltroRange? = null,
        val filtroMiolo: FiltroRange? = null,
        val filtroMultiplosDeTres: FiltroRange? = null,
        val filtroRepeticaoAnterior: FiltroRepeticao? = null,
        val ultimoResultadoConcursoAnterior: List<Int>? = null // Pode ser redundante se filtroRepeticaoAnterior for usado consistentemente
    )

    companion object {
        // Constantes
        private const val TOTAL_NUMEROS_LOTOFACIL = 25 // Nome mais específico
        private const val MIN_NUMEROS_POR_JOGO = 15
        private const val MAX_NUMEROS_POR_JOGO = 20 // Embora o app foque em 15, a lógica pode ser genérica

        val FIBONACCI = setOf(1, 2, 3, 5, 8, 13, 21)
        val PRIMOS = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
        val MIOLO = setOf(7, 8, 9, 12, 13, 14, 17, 18, 19)
        val MOLDURA = setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25)
        val MULTIPLOS_DE_TRES = setOf(3, 6, 9, 12, 15, 18, 21, 24)


        /**
         * Gera jogos da Lotofácil com base na configuração especificada.
         */
        fun gerarJogos(config: ConfiguracaoGeracao): List<Jogo> {
            // Validações iniciais
            if (config.quantidadeNumerosPorJogo !in MIN_NUMEROS_POR_JOGO..MAX_NUMEROS_POR_JOGO) {
                throw IllegalArgumentException(
                    "Quantidade de números por jogo deve estar entre $MIN_NUMEROS_POR_JOGO e $MAX_NUMEROS_POR_JOGO"
                )
            }

            if (config.quantidadeJogos <= 0) {
                throw IllegalArgumentException("Quantidade de jogos deve ser maior que zero")
            }

            if (config.numerosFixos.size > config.quantidadeNumerosPorJogo) {
                throw IllegalArgumentException(
                    "Quantidade de números fixos não pode ser maior que a quantidade de números por jogo"
                )
            }

            if (TOTAL_NUMEROS_LOTOFACIL - config.numerosExcluidos.size < config.quantidadeNumerosPorJogo) {
                throw IllegalArgumentException(
                    "Muitos números excluídos, impossível gerar jogos com a quantidade solicitada"
                )
            }

            val sobreposicao = config.numerosFixos.intersect(config.numerosExcluidos.toSet())
            if (sobreposicao.isNotEmpty()) {
                throw IllegalArgumentException(
                    "Há números que estão tanto na lista de fixos quanto na lista de excluídos: $sobreposicao"
                )
            }

            val jogosGerados = mutableListOf<Jogo>()
            // Aumentar um pouco o fator para dar mais margem em filtros muito restritivos
            val tentativasMaximas = config.quantidadeJogos * 200 + 1000
            var tentativas = 0

            val numerosDisponiveisBase = (1..TOTAL_NUMEROS_LOTOFACIL)
                .filter { it !in config.numerosExcluidos }
                .toList()
            
            val jogosUnicosComoSet = mutableSetOf<List<Int>>() // Para verificação rápida de duplicidade

            while (jogosGerados.size < config.quantidadeJogos && tentativas < tentativasMaximas) {
                tentativas++

                try {
                    val numerosSorteados = config.numerosFixos.toMutableSet()
                    val numerosRestantesParaSortear = config.quantidadeNumerosPorJogo - numerosSorteados.size

                    if (numerosRestantesParaSortear < 0) continue // Já tem mais fixos que o necessário

                    // Filtra os disponíveis que não estão nos fixos, para evitar duplicidade no sorteio
                    val numerosParaEscolha = numerosDisponiveisBase
                        .filter { it !in numerosSorteados }
                        .shuffled(Random(System.nanoTime() + tentativas)) // Adicionar tentativas para variar a seed

                    if (numerosParaEscolha.size < numerosRestantesParaSortear) {
                         // Não há números suficientes disponíveis para completar o jogo com os filtros de exclusão/fixos
                        continue
                    }
                    
                    for (i in 0 until numerosRestantesParaSortear) {
                        numerosSorteados.add(numerosParaEscolha[i])
                    }

                    if (numerosSorteados.size != config.quantidadeNumerosPorJogo) continue

                    val jogoComoLista = numerosSorteados.toList().sorted()

                    if (!verificarCriteriosEstatisticos(jogoComoLista, config)) {
                        continue
                    }

                    // Verifica se o jogo já foi gerado anteriormente usando o Set
                    if (!jogosUnicosComoSet.add(jogoComoLista)) {
                        // Jogo duplicado, tentar gerar um novo se possível
                        continue
                    }

                    jogosGerados.add(Jogo.fromList(jogoComoLista))
                } catch (e: Exception) {
                    // Logar a exceção pode ser útil para depuração, mas por ora continua
                    // e.printStackTrace() // Descomentar para depuração
                    continue
                }
            }
            return jogosGerados
        }

        /** Verifica se um jogo atende aos critérios estatísticos configurados. */
        private fun verificarCriteriosEstatisticos(
            numeros: List<Int>,
            config: ConfiguracaoGeracao
        ): Boolean {
            // Filtro Pares/Ímpares (agora focado em ÍMPARES)
            config.filtroParesImpares?.let { filtro ->
                val impares = numeros.count { it % 2 != 0 }
                if (filtro.min != null && impares < filtro.min) return false
                if (filtro.max != null && impares > filtro.max) return false
            }

            // Filtro Números Primos
            config.filtroPrimos?.let { filtro ->
                val primosNoJogo = numeros.count { it in PRIMOS }
                if (filtro.min != null && primosNoJogo < filtro.min) return false
                if (filtro.max != null && primosNoJogo > filtro.max) return false
            }

            // Filtro Números de Fibonacci
            config.filtroFibonacci?.let { filtro ->
                val fibonacciNoJogo = numeros.count { it in FIBONACCI }
                if (filtro.min != null && fibonacciNoJogo < filtro.min) return false
                if (filtro.max != null && fibonacciNoJogo > filtro.max) return false
            }

            // Filtro Miolo
            config.filtroMiolo?.let { filtro ->
                val mioloNoJogo = numeros.count { it in MIOLO }
                if (filtro.min != null && mioloNoJogo < filtro.min) return false
                if (filtro.max != null && mioloNoJogo > filtro.max) return false
            }

            // Filtro Múltiplos de 3
            config.filtroMultiplosDeTres?.let { filtro ->
                val multiplosTresNoJogo = numeros.count { it in MULTIPLOS_DE_TRES }
                if (filtro.min != null && multiplosTresNoJogo < filtro.min) return false
                if (filtro.max != null && multiplosTresNoJogo > filtro.max) return false
            }

            // Filtro Soma dos números
            config.filtroSomaTotal?.let { filtro ->
                val soma = numeros.sum()
                if (filtro.min != null && soma < filtro.min) return false
                if (filtro.max != null && soma > filtro.max) return false
            }
            
            // Filtro Repetição de Dezenas do Concurso Anterior
            config.filtroRepeticaoAnterior?.let { filtroRep ->
                val dezenasAnteriores = filtroRep.dezenasAnteriores
                val rangeRepeticao = filtroRep.range // Atribui a uma val para simplificar

                if (dezenasAnteriores.isNotEmpty()) { // Só aplica se houver dezenas anteriores
                    val repetidas = numeros.count { it in dezenasAnteriores }
                    // Usa a val rangeRepeticao aqui
                    if (rangeRepeticao?.min != null && repetidas < rangeRepeticao.min) return false
                    if (rangeRepeticao?.max != null && repetidas > rangeRepeticao.max) return false
                } else {
                    // dezenasAnteriores está vazia
                    // Se o filtro exige repetição (min > 0) mas não há dezenas anteriores, falha o filtro.
                    if (rangeRepeticao?.min != null && rangeRepeticao.min > 0) {
                        return false
                    }
                }
            }

            return true
        }
    }
}