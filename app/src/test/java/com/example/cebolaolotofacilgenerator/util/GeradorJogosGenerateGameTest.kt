package com.example.cebolaolotofacilgenerator.util // Pacote corrigido

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GeradorJogosGenerateGameTest { // Nome da classe pode ser mantido ou ajustado

    @Test
    fun `generateGame should return a list of 15 unique numbers between 1 and 25`() {
        // Acessa o primeiro jogo da lista retornada e seus números
        val game = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        assertEquals(15, game.size)
        assertEquals(game.distinct().size, game.size) // Verifica unicidade
        game.forEach { number ->
            assert(number in 1..25) { "Número $number fora do intervalo 1-25" }
        }
    }

    @Test
    fun `generateGame should return different games on subsequent calls`() {
        val game1 = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        val game2 = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        // É altamente provável que sejam diferentes, mas não garantido.
        // Um teste melhor verificaria a geração de múltiplos jogos.
        assertNotEquals(game1.toSet(), game2.toSet())
    }

    @Test
    fun `generateGame should return a sorted list`() {
        val game = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        val sortedGame = game.sorted()
        assertEquals(sortedGame, game) // Verifica se a lista já está ordenada
    }
}
