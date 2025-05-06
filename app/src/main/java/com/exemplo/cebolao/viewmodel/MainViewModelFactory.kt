package com.exemplo.cebolao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application
import com.exemplo.cebolao.repository.JogoRepository

class MainViewModelFactory(private val application: Application, private val repository: JogoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, repository) as T
    }
}