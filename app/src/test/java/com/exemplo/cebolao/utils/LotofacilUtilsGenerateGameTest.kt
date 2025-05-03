package com.exemplo.cebolao.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class LotofacilUtilsGenerateGameTest {

    @Test
    fun `generateGame should return a list of 15 unique numbers between 1 and 25`() {
        val game = LotofacilUtils.generateGame()
        assertEquals(15, game.size)
        assertEquals(game.distinct().size, game.size)
        game.forEach { number ->
            assert(number in 1..25)
        }
    }

    @Test
    fun `generateGame should return different games on subsequent calls`() {
        val game1 = LotofacilUtils.generateGame()
        val game2 = LotofacilUtils.generateGame()
        assert(game1 != game2)
    }
    @Test
    fun `generateGame should return a sorted list`() {
        val game = LotofacilUtils.generateGame()
        val sortedGame = game.sorted()
        assertEquals(sortedGame, game)
    }
}