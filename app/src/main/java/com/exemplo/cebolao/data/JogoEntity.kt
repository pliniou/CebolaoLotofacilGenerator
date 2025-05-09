package com.exemplo.cebolao.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "jogo_table")
data class JogoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numbers: String,
    val date: Long,
    @ColumnInfo(name = "favorito")
    val favorito: Boolean = false
)

class Converters {
    @TypeConverter
    fun fromString(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Int>): String {
        return Gson().toJson(list)
    }
}