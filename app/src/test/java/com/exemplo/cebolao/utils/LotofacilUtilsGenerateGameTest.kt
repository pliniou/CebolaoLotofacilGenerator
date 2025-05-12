package com.example.cebolaolotofacilgenerator.util.test

import com.example.cebolaolotofacilgenerator.util.GeradorJogos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GeradorJogosGenerateGameTest {

    @Test
    fun `generateGame should return a list of 15 unique numbers between 1 and 25`() {
        val game = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        assertEquals(15, game.size)
        assertEquals(game.distinct().size, game.size)
        game.forEach { number ->
            assert(number in 1..25) { "Número $number fora do intervalo 1-25" }
        }
    }

    @Test
    fun `generateGame should return different games on subsequent calls`() {
        val game1 = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        val game2 = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        assertNotEquals(game1, game2)
    }

    @Test
    fun `generateGame should return a sorted list`() {
        val game = GeradorJogos.gerarJogos(1, 15).first().getNumerosComoLista()
        val sortedGame = game.sorted()
        assertEquals(sortedGame, game)
    }
}
