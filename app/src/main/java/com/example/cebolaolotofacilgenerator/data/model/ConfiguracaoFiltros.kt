package com.example.cebolaolotofacilgenerator.data.model

data class ConfiguracaoFiltros(
        val filtroParesImpares: Boolean = true,
        val minImpares: Int =
                6, // Exemplo: tipicamente Lotofácil tem 12 ou 13 ímpares de 25 números
        val maxImpares: Int = 9, // e 15 números por jogo, então ímpares + pares = 15.
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

// Estes campos parecem ser usados diretamente no FiltrosFragment para popular a UI inicial
// e podem ser redundantes se os ranges acima forem a fonte da verdade.
// No entanto, observeViewModel em FiltrosFragment usa `filtros.quantidadePares`
// Poderia ser um valor derivado ou uma preferência separada.
// Para simplificar, vamos assumir que `minImpares` e `maxImpares` são a fonte,
// e `quantidadePares` não é um campo direto de `ConfiguracaoFiltros`.
// O fragmento terá que derivar/ajustar a UI para `rangeSliderPares` e `rangeSliderImpares`.
// Se `quantidadePares` for realmente necessário aqui, precisaria de mais contexto.

// Erros em FiltrosFragment.kt (linhas 67-70) usam `filtros.quantidadePares` etc.
// mas o observeViewModel usa `value` (que deve ser um Float) e não um `ConfiguracaoFiltros` direto.
// Vou manter a classe sem `quantidadePares`, `quantidadeImpares` por enquanto
// e focar nas propriedades que o `atualizarInterface` usa.
)
