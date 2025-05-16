package com.example.cebolaolotofacilgenerator.di

import android.content.Context
import androidx.room.Room
import com.example.cebolaolotofacilgenerator.data.dao.JogoDao
import com.example.cebolaolotofacilgenerator.data.dao.ResultadoDao
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer dependências relacionadas ao banco de dados.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Fornece uma instância singleton do banco de dados AppDatabase.
     *
     * @param context O contexto da aplicação
     * @return Instância do AppDatabase
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "cebolao_database"
        ).build()
    }

    /**
     * Fornece o DAO para jogos.
     *
     * @param database A instância do banco de dados
     * @return Instância de JogoDao
     */
    @Provides
    fun provideJogoDao(database: AppDatabase): JogoDao {
        return database.jogoDao()
    }

    /**
     * Fornece o DAO para resultados.
     *
     * @param database A instância do banco de dados
     * @return Instância de ResultadoDao
     */
    @Provides
    fun provideResultadoDao(database: AppDatabase): ResultadoDao {
        return database.resultadoDao()
    }
} 