package com.exemplo.cebolao.utils

import com.exemplo.cebolao.model.Jogo
import java.util.Random

class LotofacilUtils {
    companion object {

        fun generateGame(numbersToGenerate: Int, filterBy: MutableList<Int> = mutableListOf(), notIn: MutableList<Int> = mutableListOf()): MutableList<Int> {
            val random = Random()
            val selectedNumbers = mutableListOf<Int>()

            if(filterBy.isNotEmpty()){
                selectedNumbers.addAll(filterBy)
            }

            while (selectedNumbers.size < numbersToGenerate) {
                val randomNumber = random.nextInt(25) + 1 // Números de 1 a 25
                if (!selectedNumbers.contains(randomNumber) && !notIn.contains(randomNumber)) {
                    selectedNumbers.add(randomNumber)
                }
            }
            return selectedNumbers.sorted().toMutableList()
        }

        fun filterByParImpar(game: List<Int>, even: Int, odd: Int): Boolean {
            var countEven = 0
            var countOdd = 0
            game.forEach { number ->
                if (number % 2 == 0) {
                    countEven++
                } else {
                    countOdd++
                }
            }
            return countEven == even && countOdd == odd
        }

        fun filterByMultiplo(game: List<Int>, multiplo3: Int, multiplo5: Int, multiplo7: Int): Boolean {
            var countMultiplo3 = 0
            var countMultiplo5 = 0
            var countMultiplo7 = 0
            game.forEach { number ->
                if (number % 3 == 0) {
                    countMultiplo3++
                }
                if (number % 5 == 0) {
                    countMultiplo5++
                }
                if (number % 7 == 0) {
                    countMultiplo7++
                }
            }
            return countMultiplo3 == multiplo3 && countMultiplo5 == multiplo5 && countMultiplo7 == multiplo7
        }

        fun filterBySoma(game: List<Int>, somaMin: Int, somaMax: Int): Boolean {
            val soma = game.sum()
            return soma in somaMin..somaMax
        }

        fun numbersToString(numbers: List<Int>): String {
            return numbers.joinToString(", ", transform = { "%02d".format(it) })
        }
    }
}













































