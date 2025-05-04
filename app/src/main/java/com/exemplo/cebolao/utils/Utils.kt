package com.exemplo.cebolao.utils




fun stringToNumbers(numbersString: String): List<Int> {
    return try {
        numbersString.split(",").map { it.trim().toInt() }
    } catch (e: NumberFormatException) {
        emptyList()
    }
}
