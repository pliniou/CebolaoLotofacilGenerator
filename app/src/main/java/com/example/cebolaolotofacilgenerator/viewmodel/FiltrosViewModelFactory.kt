package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
// import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository // REMOVIDO

class FiltrosViewModelFactory(
    private val application: Application
    // private val resultadoRepository: ResultadoRepository // REMOVIDO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FiltrosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FiltrosViewModel(application /*, resultadoRepository */) as T // REMOVIDO resultadoRepository
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
} 