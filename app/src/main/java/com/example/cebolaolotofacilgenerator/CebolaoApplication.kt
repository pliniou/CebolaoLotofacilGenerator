package com.example.cebolaolotofacilgenerator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Classe de aplicação para inicialização de componentes essenciais. */
@HiltAndroidApp
class CebolaoApplication : Application() {
    // A injeção de dependências é gerenciada pelo Hilt
    // O banco de dados e os repositórios são fornecidos através do DatabaseModule
}
