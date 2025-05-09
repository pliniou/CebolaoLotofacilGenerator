package com.exemplo.cebolao.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.exemplo.cebolao.data.TypeConverter

@Database(entities = [JogoEntity::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jogoDao(): JogoDao
}