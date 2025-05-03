package com.exemplo.cebolao.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AppDatabaseInstance(private val context: Context) {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Change the type of numbers from List<Int> to String
            database.execSQL("CREATE TABLE jogo_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, numeros TEXT NOT NULL, data INTEGER NOT NULL, favorito INTEGER NOT NULL DEFAULT 0)")
            database.execSQL("INSERT INTO jogo_table_new (id, numeros, data, favorito) SELECT id, '', data, favorito FROM jogo_table")
            database.execSQL("DROP TABLE jogo_table")
            database.execSQL("ALTER TABLE jogo_table_new RENAME TO jogo_table")
        }
    }

    fun getAppDatabase(): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "cebolao.db"
        ).addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}