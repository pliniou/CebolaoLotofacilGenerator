package com.example.cebolaolotofacilgenerator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConfiguracoesViewModel

@Suppress("UNCHECKED_CAST")
class ConfiguracoesViewModelFactory(private val appDataStore: AppDataStore) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfiguracoesViewModel::class.java)) {
            return ConfiguracoesViewModel(appDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
