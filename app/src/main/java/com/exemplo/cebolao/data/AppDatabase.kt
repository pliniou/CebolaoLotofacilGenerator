package com.exemplo.cebolao.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.exemplo.cebolao.model.Jogo

@Database(entities = [Jogo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jogoDao(): JogoDao
}