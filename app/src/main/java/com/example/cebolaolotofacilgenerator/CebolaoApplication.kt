package com.example.cebolaolotofacilgenerator

import android.app.Application
import androidx.room.Room
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/** Classe de aplicação para inicialização de componentes essenciais. */
class CebolaoApplication : Application() {

    // Escopo de coroutine para operações que devem sobreviver a mudanças de configuração
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Instância do banco de dados
    lateinit var database: AppDatabase
        private set

    // Repositórios
    val jogoRepository by lazy { JogoRepository(database.jogoDao()) }
    val resultadoRepository by lazy { ResultadoRepository(database.resultadoDao()) }

    override fun onCreate() {
        super.onCreate()

        // Inicializa o banco de dados
        database =
                Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "cebolao_database"
                        )
                        .build()
    }
}
