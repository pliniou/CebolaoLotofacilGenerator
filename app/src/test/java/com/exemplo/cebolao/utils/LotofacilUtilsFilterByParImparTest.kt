package com.exemplo.cebolao.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class LotofacilUtilsFilterByParImparTest {

    @Test
    fun `filterByParImpar with even numbers should return list with even numbers`() {
        val jogos = listOf(
            listOf(1, 2, 3, 4, 5),
            listOf(2, 4, 6, 8, 10),
            listOf(11, 12, 13, 14, 15)
        )
        val expected = listOf(
            listOf(2, 4)
        )

        val result = LotofacilUtils.filterByParImpar(jogos, 2)

        assertEquals(expected.size, result.size)
    }

    @Test
    fun `filterByParImpar with odd numbers should return list with odd numbers`() {
        val jogos = listOf(
            listOf(1, 2, 3, 4, 5),
            listOf(1, 3, 5, 7, 9),
            listOf(11, 12, 13, 14, 15)
        )
        val expected = listOf(
            listOf(1, 3, 5, 7, 9)
        )

        val result = LotofacilUtils.filterByParImpar(jogos, 1)

        assertEquals(expected.size, result.size)
    }

    @Test
    fun `filterByParImpar with even numbers in all games`() {
        val jogos = listOf(
            listOf(2, 4, 6, 8, 10),
            listOf(12, 14, 16, 18, 20),
            listOf(22, 24, 26, 28, 30)
        )
        val expected = jogos

        val result = LotofacilUtils.filterByParImpar(jogos, 5)

        assertEquals(expected.size, result.size)
    }

    @Test
    fun `filterByParImpar with odd numbers in all games`() {
        val jogos = listOf(
            listOf(1, 3, 5, 7, 9),
            listOf(11, 13, 15, 17, 19),
            listOf(21, 23, 25, 27, 29)
        )
        val expected = jogos

        val result = LotofacilUtils.filterByParImpar(jogos, 5)

        assertEquals(expected.size, result.size)
    }

    @Test
    fun `filterByParImpar with no matching games should return empty list`() {
        val jogos = listOf(
            listOf(1, 3, 5, 7, 9),
            listOf(11, 13, 15, 17, 19),
            listOf(21, 23, 25, 27, 29)
        )
        val expected = emptyList<List<Int>>()

        val result = LotofacilUtils.filterByParImpar(jogos, 2)

        assertEquals(expected, result)
    }

    @Test
    fun `filterByParImpar with empty list should return empty list`() {
        val jogos = emptyList<List<Int>>()
        val expected = emptyList<List<Int>>()

        val result = LotofacilUtils.filterByParImpar(jogos, 2)

        assertEquals(expected, result)
    }

    @Test
    fun `filterByParImpar with only one game`() {
        val jogos = listOf(listOf(1,2,3,4,5))
        val expected = listOf(listOf(2,4))

        val result = LotofacilUtils.filterByParImpar(jogos, 2)

        assertEquals(expected, result)
    }
}