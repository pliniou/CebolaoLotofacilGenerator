package com.exemplo.cebolao.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LotofacilUtilsTest {

    @Test
    fun `isPrime returns true for prime numbers`() {
        assertTrue(isPrime(2))
        assertTrue(isPrime(3))
        assertTrue(isPrime(5))
        assertTrue(isPrime(7))
        assertTrue(isPrime(11))
        assertTrue(isPrime(13))
        assertTrue(isPrime(17))
        assertTrue(isPrime(19))
        assertTrue(isPrime(23))
    }

    @Test
    fun `isPrime returns false for non-prime numbers`() {
        assertFalse(isPrime(1))
        assertFalse(isPrime(4))
        assertFalse(isPrime(6))
        assertFalse(isPrime(8))
        assertFalse(isPrime(9))
        assertFalse(isPrime(10))
        assertFalse(isPrime(12))
        assertFalse(isPrime(14))
        assertFalse(isPrime(15))
        assertFalse(isPrime(16))
        assertFalse(isPrime(18))
        assertFalse(isPrime(20))
        assertFalse(isPrime(21))
        assertFalse(isPrime(22))
        assertFalse(isPrime(24))
        assertFalse(isPrime(25))
    }

    @Test
    fun `countPrimes returns correct count`() {
        val numbers1 = listOf(2, 3, 5, 7, 11)
        assertEquals(5, countPrimes(numbers1))

        val numbers2 = listOf(1, 4, 6, 8, 9)
        assertEquals(0, countPrimes(numbers2))

        val numbers3 = listOf(2, 4, 6, 8, 11)
        assertEquals(2, countPrimes(numbers3))
    }

    @Test
    fun `countPairs returns correct count`() {
        val numbers1 = listOf(2, 4, 6, 8, 10)
        assertEquals(5, countPairs(numbers1))

        val numbers2 = listOf(1, 3, 5, 7, 9)
        assertEquals(0, countPairs(numbers2))

        val numbers3 = listOf(1, 2, 3, 4, 5)
        assertEquals(2, countPairs(numbers3))
    }

    @Test
    fun `countCross returns correct count`() {
        val numbers1 = listOf(3, 8, 13, 18, 23)
        assertEquals(5, countCross(numbers1))

        val numbers2 = listOf(1, 2, 4, 5, 6)
        assertEquals(0, countCross(numbers2))

        val numbers3 = listOf(1, 3, 5, 11, 15)
        assertEquals(3, countCross(numbers3))
    }

    @Test
    fun `countFrame returns correct count`() {
        val numbers1 = listOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25)
        assertEquals(16, countFrame(numbers1))

        val numbers2 = listOf(7, 8, 9, 12, 13, 14, 17, 18, 19)
        assertEquals(0, countFrame(numbers2))

         val numbers3 = listOf(1, 2, 7, 10, 15)
        assertEquals(4, countFrame(numbers3))
    }

    @Test
    fun `countUpperHalf returns correct count`() {
        val numbers1 = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        assertEquals(12, countUpperHalf(numbers1))

        val numbers2 = listOf(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
        assertEquals(0, countUpperHalf(numbers2))

         val numbers3 = listOf(1, 2, 3, 13, 14, 15)
        assertEquals(3, countUpperHalf(numbers3))
    }

    @Test
    fun `countArithmeticProgression returns correct count`() {
        val numbers1 = listOf(1, 3, 5, 7, 9)
        assertTrue(countArithmeticProgression(numbers1))

        val numbers2 = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        assertTrue(countArithmeticProgression(numbers2))

        val numbers3 = listOf(1, 2, 4, 5, 6)
        assertFalse(countArithmeticProgression(numbers3))
    }

    @Test
    fun `countSequence returns correct count`() {
        val numbers1 = listOf(1, 2, 3)
        assertTrue(countSequence(numbers1))

        val numbers2 = listOf(1, 2, 3, 4, 5)
        assertTrue(countSequence(numbers2))
        
         val numbers3 = listOf(1, 3, 4, 5, 7, 8, 9, 10)
        assertTrue(countSequence(numbers3))

        val numbers4 = listOf(1, 3, 5, 7, 9)
        assertFalse(countSequence(numbers4))
    }
    
    @Test
    fun `countLine returns true if there are 2 to 4 numbers in the same line`() {
        assertTrue(countLine(listOf(1, 2, 3)))
        assertTrue(countLine(listOf(6, 7)))
        assertTrue(countLine(listOf(11, 12, 13, 14)))
        assertTrue(countLine(listOf(21, 22, 23)))
    }

    @Test
    fun `countLine returns false if there are less than 2 or more than 4 numbers in the same line`() {
        assertFalse(countLine(listOf(1)))
        assertFalse(countLine(listOf(1, 2, 3, 4, 5)))
        assertFalse(countLine(listOf(1, 6, 11, 16)))
        assertFalse(countLine(listOf(1, 2, 6)))
    }

    @Test
    fun `countColumn returns true if there are 2 to 4 numbers in the same column`() {
        assertTrue(countColumn(listOf(1, 6)))
        assertTrue(countColumn(listOf(2, 7, 12)))
        assertTrue(countColumn(listOf(3, 8, 13, 18)))
        assertTrue(countColumn(listOf(5, 10, 15)))
    }

    @Test
    fun `countColumn returns false if there are less than 2 or more than 4 numbers in the same column`() {
        assertFalse(countColumn(listOf(1)))
        assertFalse(countColumn(listOf(1, 6, 11, 16, 21)))
        assertFalse(countColumn(listOf(1, 2, 3)))
    }

    @Test
    fun `calculateSum returns correct sum`() {
        val numbers1 = listOf(1, 2, 3, 4, 5)
        assertEquals(15, calculateSum(numbers1))

        val numbers2 = listOf(10, 20, 30, 40, 50)
        assertEquals(150, calculateSum(numbers2))

        val numbers3 = listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25)
        assertEquals(189, calculateSum(numbers3))
    }

    @Test
    fun `generateGame returns 15 unique numbers between 1 and 25`() {
        val game = generateGame()
        assertEquals(15, game.size)
        assertTrue(game.all { it in 1..25 })
        assertEquals(15, game.distinct().size)
    }

     @Test
    fun `numbersToString should convert a list of integers to a comma-separated string`() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val expected = "1,2,3,4,5"
        assertEquals(expected, numbersToString(numbers))
    }

    @Test
    fun `stringToNumbers should convert a comma-separated string to a list of integers`() {
        val numberString = "1,2,3,4,5"
        val expected = listOf(1, 2, 3, 4, 5)
        assertEquals(expected, stringToNumbers(numberString))
    }
    @Test
    fun `checkFilters returns true when all filters are met`() {
        val numbers = listOf(2, 3, 4, 6, 8, 10, 12, 13, 14, 16, 17, 18, 20, 22, 23)
        val filters = listOf("Primos", "Pares", "Soma")

        assertTrue(checkFilters(numbers, filters))
    }

    @Test
    fun `checkFilters returns false when at least one filter is not met`() {
        val numbers = listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25)
        val filters = listOf("Primos", "Pares")

        assertFalse(checkFilters(numbers, filters))
    }
     @Test
    fun `checkFilters returns true when filters is empty`() {
        val numbers = listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25)
        val filters = emptyList<String>()

        assertTrue(checkFilters(numbers, filters))
    }

}