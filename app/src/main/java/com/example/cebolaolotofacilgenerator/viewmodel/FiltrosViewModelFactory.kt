package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository

class FiltrosViewModelFactory(
    private val application: Application,
    private val resultadoRepository: ResultadoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FiltrosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FiltrosViewModel(application, resultadoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
} 