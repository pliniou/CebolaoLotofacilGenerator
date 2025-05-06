package com.exemplo.cebolao.viewmodel

import androidx.lifecycle.ViewModel
import com.exemplo.cebolao.repository.JogoRepository
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val repository: JogoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
 return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}