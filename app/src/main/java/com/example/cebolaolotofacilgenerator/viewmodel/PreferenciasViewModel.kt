package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.util.PreferenciasManager
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar as preferências do usuário.
 */
class PreferenciasViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenciasManager = PreferenciasManager(application)
    
    // LiveData para observar as configurações
    val quantidadeNumeros = preferenciasManager.quantidadeNumeros.asLiveData()
    val quantidadeJogos = preferenciasManager.quantidadeJogos.asLiveData()
    val temaEscuro = preferenciasManager.temaEscuro.asLiveData()
    
    // LiveData para observar os filtros
    val filtrosPares = preferenciasManager.filtrosPares.asLiveData()
    val filtrosPrimos = preferenciasManager.filtrosPrimos.asLiveData()
    val filtrosFibonacci = preferenciasManager.filtrosFibonacci.asLiveData()
    val filtrosMiolo = preferenciasManager.filtrosMiolo.asLiveData()
    val filtrosMultiplosTres = preferenciasManager.filtrosMultiplosTres.asLiveData()
    val filtrosSoma = preferenciasManager.filtrosSoma.asLiveData()
    
    // LiveData para observar os números fixos e excluídos
    val numerosFixos = preferenciasManager.numerosFixos.asLiveData()
    val numerosExcluidos = preferenciasManager.numerosExcluidos.asLiveData()
    
    /**
     * Salva a quantidade de números por jogo.
     */
    fun salvarQuantidadeNumeros(quantidade: Int) = viewModelScope.launch {
        preferenciasManager.salvarQuantidadeNumeros(quantidade)
    }
    
    /**
     * Salva a quantidade de jogos a gerar.
     */
    fun salvarQuantidadeJogos(quantidade: Int) = viewModelScope.launch {
        preferenciasManager.salvarQuantidadeJogos(quantidade)
    }
    
    /**
     * Salva a configuração de tema escuro.
     */
    fun salvarTemaEscuro(ativo: Boolean) = viewModelScope.launch {
        preferenciasManager.salvarTemaEscuro(ativo)
    }
    
    /**
     * Salva as configurações de filtro de números pares.
     */
    fun salvarFiltrosPares(minimo: Int?, maximo: Int?) = viewModelScope.launch {
        preferenciasManager.salvarFiltrosPares(minimo, maximo)
    }
    
    /**
     * Salva as configurações de filtro de números primos.
     */
    fun salvarFiltrosPrimos(minimo: Int?, maximo: Int?) = viewModelScope.launch {
        preferenciasManager.salvarFiltrosPrimos(minimo, maximo)
    }
    
    /**
     * Salva as configurações de filtro de números de Fibonacci.
     */
    fun salvarFiltrosFibonacci(minimo: Int?, maximo: Int?) = viewModelScope.launch {
        preferenciasManager.salvarFiltrosFibonacci(minimo, maximo)
    }
    
    /**
     * Salva as configurações de filtro de números do miolo.
     */
    fun salvarFiltrosMiolo(minimo: Int?, maximo: Int?) = viewModelScope.launch {
        preferenciasManager.salvarFiltrosMiolo(minimo, maximo)
    }
    
    /**
     * Salva as configurações de filtro de múltiplos de 3.
     */
    fun salvarFiltrosMultiplosTres(minimo: Int?, maximo: Int?) = viewModelScope.launch {
        preferenciasManager.salvarFiltrosMultiplosTres(minimo, maximo)
    }
    
    /**
     * Salva as configurações de filtro de soma total.
     */
    fun salvarFiltrosSoma(minimo: Int?, maximo: Int?) = viewModelScope.launch {
        preferenciasManager.salvarFiltrosSoma(minimo, maximo)
    }
    
    /**
     * Salva a lista de números fixos.
     */
    fun salvarNumerosFixos(numeros: List<Int>) = viewModelScope.launch {
        preferenciasManager.salvarNumerosFixos(numeros)
    }
    
    /**
     * Salva a lista de números excluídos.
     */
    fun salvarNumerosExcluidos(numeros: List<Int>) = viewModelScope.launch {
        preferenciasManager.salvarNumerosExcluidos(numeros)
    }
    
    /**
     * Adiciona ou remove um número da lista de números fixos.
     * @return true se o número foi adicionado, false se foi removido
     */
    suspend fun toggleNumeroFixo(numero: Int): Boolean {
        return preferenciasManager.toggleNumeroFixo(numero)
    }
    
    /**
     * Adiciona ou remove um número da lista de números excluídos.
     * @return true se o número foi adicionado, false se foi removido
     */
    suspend fun toggleNumeroExcluido(numero: Int): Boolean {
        return preferenciasManager.toggleNumeroExcluido(numero)
    }
    
    /**
     * Reseta todas as configurações para os valores padrão.
     */
    fun resetarConfiguracoes() = viewModelScope.launch {
        preferenciasManager.resetarConfiguracoes()
    }
    
    /**
     * Carrega todas as configurações no GeradorViewModel.
     */
    fun carregarConfiguracoesNoGerador(geradorViewModel: GeradorViewModel) = viewModelScope.launch {
        // Carrega configurações gerais
        quantidadeNumeros.value?.let { geradorViewModel.quantidadeNumeros = it }
        quantidadeJogos.value?.let { geradorViewModel.quantidadeJogos = it }
        
        // Carrega números fixos e excluídos
        numerosFixos.value?.let { 
            geradorViewModel.numerosFixos.clear()
            geradorViewModel.numerosFixos.addAll(it)
        }
        
        numerosExcluidos.value?.let {
            geradorViewModel.numerosExcluidos.clear()
            geradorViewModel.numerosExcluidos.addAll(it)
        }
        
        // Carrega filtros estatísticos
        filtrosPares.value?.let {
            geradorViewModel.minPares = it.first
            geradorViewModel.maxPares = it.second
        }
        
        filtrosPrimos.value?.let {
            geradorViewModel.minPrimos = it.first
            geradorViewModel.maxPrimos = it.second
        }
        
        filtrosFibonacci.value?.let {
            geradorViewModel.minFibonacci = it.first
            geradorViewModel.maxFibonacci = it.second
        }
        
        filtrosMiolo.value?.let {
            geradorViewModel.minMiolo = it.first
            geradorViewModel.maxMiolo = it.second
        }
        
        filtrosMultiplosTres.value?.let {
            geradorViewModel.minMultiplosTres = it.first
            geradorViewModel.maxMultiplosTres = it.second
        }
        
        filtrosSoma.value?.let {
            geradorViewModel.minSoma = it.first
            geradorViewModel.maxSoma = it.second
        }
    }
}
