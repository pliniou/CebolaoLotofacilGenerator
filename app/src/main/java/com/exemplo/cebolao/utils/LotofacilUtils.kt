package com.exemplo.cebolao.utils

import com.exemplo.cebolao.model.Jogo
import kotlin.random.Random

class LotofacilUtils {

    companion object {

        private const val TOTAL_NUMBERS = 25
        private const val SELECTED_NUMBERS = 15

        fun generateGame(): Jogo {
            val allNumbers = (1..TOTAL_NUMBERS).toMutableList()
            val selectedNumbers = mutableSetOf<Int>()

            while (selectedNumbers.size < SELECTED_NUMBERS) {
                val randomIndex = Random.nextInt(allNumbers.size)
                selectedNumbers.add(allNumbers.removeAt(randomIndex))
            }

            return Jogo(
                numeros = selectedNumbers.toList().sorted(),
                isFavorite = false,
                dataGeracao = System.currentTimeMillis()
            )
        }

        // Add other utility functions here as needed
    }
}


























