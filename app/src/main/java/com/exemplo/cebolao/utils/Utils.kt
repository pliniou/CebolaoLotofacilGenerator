package com.exemplo.cebolao.utils

fun numbersToString(numbers: List<Int>): String {
    return numbers.joinToString(",")
}

fun filterNumbers(numbers: List<Int>, min: Int, max: Int): List<Int> {
    return numbers.filter { it in min..max }
}

fun stringToNumbers(numbersString: String): List<Int> {
    return try {
        numbersString.split(",").map { it.trim().toInt() }
    } catch (e: NumberFormatException) {
        emptyList()
    }
}
