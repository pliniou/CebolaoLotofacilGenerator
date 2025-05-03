package com.exemplo.cebolao.utils

import org.junit.Assert.*
import org.junit.Test

class LotofacilUtilsFilterBySomaTest {

    @Test
    fun `filterBySoma should return true when sum is within range`() {
        val jogo = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val minSoma = 120
        val maxSoma = 120
        assertTrue(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }

    @Test
    fun `filterBySoma should return false when sum is below min`() {
        val jogo = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 1)
        val minSoma = 106
        val maxSoma = 120
        assertFalse(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }

    @Test
    fun `filterBySoma should return false when sum is above max`() {
        val jogo = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 25)
        val minSoma = 100
        val maxSoma = 110
        assertFalse(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }

    @Test
    fun `filterBySoma should return true when sum is equal to min`() {
        val jogo = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 1)
        val minSoma = 106
        val maxSoma = 120
        assertTrue(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }

    @Test
    fun `filterBySoma should return true when sum is equal to max`() {
        val jogo = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 25)
        val minSoma = 100
        val maxSoma = 110
        assertTrue(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }
    
        @Test
    fun `filterBySoma should return false when sum is equal to max but numbers are wrong`() {
        val jogo = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
        val minSoma = 15
        val maxSoma = 15
        assertTrue(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }
    
        @Test
    fun `filterBySoma should return false when sum is equal to min but numbers are wrong`() {
        val jogo = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 1)
        val minSoma = 105
        val maxSoma = 120
        assertFalse(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }

    @Test
    fun `filterBySoma should handle empty list`() {
        val jogo = emptyList<Int>()
        val minSoma = 100
        val maxSoma = 110
        assertFalse(LotofacilUtils.filterBySoma(jogo, minSoma, maxSoma))
    }
}