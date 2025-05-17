package com.example.cebolaolotofacilgenerator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cebolaolotofacilgenerator.data.converters.ListConverter
import com.example.cebolaolotofacilgenerator.data.dao.JogoDao
// import com.example.cebolaolotofacilgenerator.data.dao.ResultadoDao // REMOVIDO
import com.example.cebolaolotofacilgenerator.data.model.Jogo
// import com.example.cebolaolotofacilgenerator.data.model.Resultado // REMOVIDO

/** Classe principal do banco de dados Room para o aplicativo. */
@Database(entities = [Jogo::class], version = 1, exportSchema = false) // Resultado::class REMOVIDO
@TypeConverters(Converters::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    /** Obtém o DAO para operações com jogos. */
    abstract fun jogoDao(): JogoDao

    // /** Obtém o DAO para operações com resultados. */
    // abstract fun resultadoDao(): ResultadoDao // REMOVIDO

    companion object {
        // Instância singleton do banco de dados
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Obtém a instância do banco de dados, criando-a se necessário.
         *
         * @param context O contexto da aplicação.
         * @return A instância do banco de dados.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Se a instância não existe, cria-a
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "lotofacil_database"
                                        )
                                        .fallbackToDestructiveMigration() // Se houver uma migração,
                                        // recria o banco
                                        .build()

                        INSTANCE = instance
                        instance
                    }
        }
    }
}
