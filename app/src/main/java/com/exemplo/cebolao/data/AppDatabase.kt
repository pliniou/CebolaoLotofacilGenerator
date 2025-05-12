package com.exemplo.cebolao.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.exemplo.cebolao.data.converters.ListConverter
import com.exemplo.cebolao.model.Jogo

@Database(entities = [Jogo::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jogoDao(): JogoDao
}