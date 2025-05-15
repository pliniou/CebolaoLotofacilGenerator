package com.example.cebolaolotofacilgenerator.data.model

data class ConfiguracaoFiltros(
        val quantidadeJogos: Int = 10,
        val quantidadeNumerosPorJogo: Int = 15,
        val numerosFixos: List<Int> = emptyList(),
        val numerosExcluidos: List<Int> = emptyList(),

        val filtroParesImpares: Boolean = true,
        val minImpares: Int = 6,
        val maxImpares: Int = 9,
        // Se minImpares = 6, maxImpares = 9, então minPares = 6, maxPares = 9.

        val filtroSomaTotal: Boolean = true,
        val minSoma: Int = 175, // Valores típicos para 15 dezenas
        val maxSoma: Int = 210,
        val filtroPrimos: Boolean = true,
        val minPrimos: Int = 4, // Números primos até 25: 2,3,5,7,11,13,17,19,23 (9 primos)
        val maxPrimos: Int = 7,
        val filtroFibonacci: Boolean = true,
        val minFibonacci: Int = 3, // Sequência de Fibonacci até 25: 1,2,3,5,8,13,21 (7 números)
        val maxFibonacci: Int = 6,
        val filtroMioloMoldura: Boolean = true,
        // Miolo: 7,8,9,12,13,14,17,18,19 (9 números)
        // Moldura: 1,2,3,4,5,6,10,11,15,16,20,21,22,23,24,25 (16 números)
        val minMiolo: Int = 3, // Quantidade de números do miolo no jogo
        val maxMiolo: Int = 6,
        val filtroMultiplosDeTres: Boolean = true,
        // Múltiplos de 3 até 25: 3,6,9,12,15,18,21,24 (8 números)
        val minMultiplos: Int = 3,
        val maxMultiplos: Int = 6,

        val filtroRepeticaoConcursoAnterior: Boolean = true,
        val minRepeticaoConcursoAnterior: Int = 8, // Valores típicos de repetição para Lotofácil
        val maxRepeticaoConcursoAnterior: Int = 10,
        val dezenasConcursoAnterior: List<Int> = emptyList() // Lista das dezenas do último concurso informado
)
