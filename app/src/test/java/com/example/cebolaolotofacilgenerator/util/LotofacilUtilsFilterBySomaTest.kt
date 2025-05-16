package com.example.cebolaolotofacilgenerator.util // Pacote corrigido

import com.example.cebolaolotofacilgenerator.data.model.Jogo // Importe o modelo Jogo
import org.junit.Assert.*
import org.junit.Test

class LotofacilUtilsFilterBySomaTest { // Renomear se a classe/função testada for outra

    // Função auxiliar para criar um jogo com uma soma específica (simplificado)
    private fun criarJogoComSoma(numeros: List<Int>): Jogo {
        return Jogo.fromList(numeros) // Usa o factory method do Jogo
    }

    @Test
    fun `filterBySoma should return true when sum is within range`() {
        val jogo =
                criarJogoComSoma(
                        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 25)
                ) // Soma = 150
        // Supondo uma função que verifica se a soma está no intervalo
        val resultado = verificarSoma(jogo, 140, 160) // Exemplo de função
        assertTrue(resultado)
    }

    @Test
    fun `filterBySoma should return false when sum is below min`() {
        val jogo =
                criarJogoComSoma(
                        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ) // Soma = 120
        val resultado = verificarSoma(jogo, 140, 160) // Exemplo de função
        assertFalse(resultado)
    }

    @Test
    fun `filterBySoma should return false when sum is above max`() {
        val jogo =
                criarJogoComSoma(
                        listOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
                ) // Soma = 270
        val resultado = verificarSoma(jogo, 140, 160) // Exemplo de função
        assertFalse(resultado)
    }

    // Implementar outros casos de teste...

    // Função de exemplo para verificar a soma (adapte à sua implementação real)
    private fun verificarSoma(jogo: Jogo, minSoma: Int?, maxSoma: Int?): Boolean {
        val soma = jogo.soma
        if (minSoma != null && soma < minSoma) return false
        if (maxSoma != null && soma > maxSoma) return false
        return true
    }
}
