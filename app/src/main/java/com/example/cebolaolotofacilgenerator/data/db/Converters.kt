package com.example.cebolaolotofacilgenerator.data.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.cebolaolotofacilgenerator.data.converters.ListConverter
import java.util.Date

/** Classe de conversores para tipos complexos usados no banco de dados Room. */
@TypeConverters(ListConverter::class)
class Converters {
    /** Converte um timestamp (Long) para um objeto Date. */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /** Converte um objeto Date para um timestamp (Long). */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
