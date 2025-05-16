package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository

class MainViewModelFactory(
        private val application: Application,
        private val jogoRepository: JogoRepository,
        private val resultadoRepository: ResultadoRepository,
        private val appDataStore: AppDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, jogoRepository, resultadoRepository, appDataStore) as T
        }
        if (modelClass.isAssignableFrom(FiltrosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FiltrosViewModel(application, resultadoRepository) as T
        }
        if (modelClass.isAssignableFrom(JogosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return JogosViewModel(jogoRepository) as T
        }
        if (modelClass.isAssignableFrom(ResultadoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ResultadoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
