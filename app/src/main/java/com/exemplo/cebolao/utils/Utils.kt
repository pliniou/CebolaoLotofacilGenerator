package com.exemplo.cebolao.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }

        fun formatGameNumbers(numbers: List<Int>): String {
            return numbers.joinToString(", ", transform = { "%02d".format(it) })
        }
    }
}
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
