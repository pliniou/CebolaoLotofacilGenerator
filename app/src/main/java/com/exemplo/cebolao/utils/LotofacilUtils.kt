package com.exemplo.cebolao.utils

import kotlin.random.Random
import kotlin.math.abs

object LotofacilUtils {

    fun generateGame(): List<Int> {
        return (1..25).shuffled().take(15).sorted()
    }

    fun isPrime(number: Int): Boolean {
        if (number <= 1) return false
        if (number <= 3) return true
        if (number % 2 == 0 || number % 3 == 0) return false
        var i = 5
        while (i * i <= number) {
            if (number % i == 0 || number % (i + 2) == 0) return false
            i += 6
        }
        return true
    }

    fun countPrimes(game: List<Int>): Int {
        return game.count { isPrime(it) }
    }

    fun countPairs(game: List<Int>): Int {
        return game.count { it % 2 == 0 }
    }

    fun countCross(game: List<Int>): Int {
        val crossNumbers = listOf(7, 9, 10, 11, 13, 17, 19)
        return game.count { crossNumbers.contains(it) }
    }

    fun countFrame(game: List<Int>): Int {
        val frameNumbers = listOf(
            1, 2, 3, 4, 5,
            6, 10,
            11, 15,
            16, 17, 18, 19, 20,
            21, 22, 23, 24, 25
        )
        return game.count { frameNumbers.contains(it) }
    }

    fun countUpperHalf(game: List<Int>): Int {
        val upperHalf = (1..13).toList()
        return game.count { upperHalf.contains(it) }
    }

    fun countArithmeticProgression(game: List<Int>): Int {
        return if (game.size < 3) 0 else {
            var count = 0
            for (i in 0 until game.size - 2) {
                val diff = game[i + 1] - game[i]
                if (diff > 0 && game[i + 2] - game[i + 1] == diff) {
                    count++
                }
            }
            count
        }
    }

    fun countSequence(game: List<Int>): Int {
        if(game.isEmpty()) return 0
        var count = 0
        for (i in 0 until game.size - 1) {
            if (game[i + 1] == game[i] + 1) {
                count++
            }
        }
        return count
    }

    fun countLine(game: List<Int>, line: Int): Int {
        val start = (line - 1) * 5 + 1
        val end = line * 5
        return game.count { it in start..end }
    }

    fun countColumn(game: List<Int>, column: Int): Int {
        return game.count { (it - column) % 5 == 0 && it > 0 }
    }

    fun calculateSum(game: List<Int>): Int {
        return game.sum()
    }

    fun filterByParImpar(game: List<Int>, paresMin: Int, paresMax: Int, imparesMin: Int, imparesMax: Int): Boolean{
        val pares = game.count { it % 2 == 0 }
        val impares = game.count { it % 2 != 0 }
        return pares in paresMin..paresMax && impares in imparesMin..imparesMax
    }

    fun filterBySoma(game: List<Int>, somaMin: Int, somaMax: Int): Boolean {
        val soma = calculateSum(game)
        return soma in somaMin..somaMax
    }

    fun filterByMultiplo(game: List<Int>, multiplo: Int, quantidadeMin: Int, quantidadeMax: Int): Boolean {
        val multiplos = game.count { it % multiplo == 0 }
        return multiplos in quantidadeMin..quantidadeMax
    }
}













































