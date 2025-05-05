package com.exemplo.cebolao.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [JogoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase()