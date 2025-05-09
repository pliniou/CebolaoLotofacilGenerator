package com.exemplo.cebolao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exemplo.cebolao.data.AppDataStore
import com.exemplo.cebolao.repository.JogoRepository
import java.lang.IllegalArgumentException

class MainViewModelFactory(
    private val repository: JogoRepository,
    private val dataStore: AppDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
 return MainViewModel(repository, dataStore) as T
    }
}