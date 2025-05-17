package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.util.PreferenciasManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel para gerenciar as preferências do usuário. */
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

        // StateFlow para observar os números fixos e excluídos
        val numerosFixosState: StateFlow<List<Int>> =
                preferenciasManager.numerosFixos.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = emptyList()
                )

        val numerosExcluidosState: StateFlow<List<Int>> =
                preferenciasManager.numerosExcluidos.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = emptyList()
                )

        val salvarFiltrosAutomaticamente: StateFlow<Boolean> =
                preferenciasManager.salvarFiltrosAutomaticamente.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = false // Valor padrão inicial
                )

        /** Salva a quantidade de números por jogo. */
        fun salvarQuantidadeNumeros(quantidade: Int) =
                viewModelScope.launch { preferenciasManager.salvarQuantidadeNumeros(quantidade) }

        /** Salva a quantidade de jogos a gerar. */
        fun salvarQuantidadeJogos(quantidade: Int) =
                viewModelScope.launch { preferenciasManager.salvarQuantidadeJogos(quantidade) }

        /** Salva a configuração de tema escuro. */
        fun salvarTemaEscuro(ativo: Boolean) =
                viewModelScope.launch { preferenciasManager.salvarTemaEscuro(ativo) }

        /** Salva as configurações de filtro de números pares. */
        fun salvarFiltrosPares(minimo: Int?, maximo: Int?) =
                viewModelScope.launch { preferenciasManager.salvarFiltrosPares(minimo, maximo) }

        /** Salva as configurações de filtro de números primos. */
        fun salvarFiltrosPrimos(minimo: Int?, maximo: Int?) =
                viewModelScope.launch { preferenciasManager.salvarFiltrosPrimos(minimo, maximo) }

        /** Salva as configurações de filtro de números de Fibonacci. */
        fun salvarFiltrosFibonacci(minimo: Int?, maximo: Int?) =
                viewModelScope.launch { preferenciasManager.salvarFiltrosFibonacci(minimo, maximo) }

        /** Salva as configurações de filtro de números do miolo. */
        fun salvarFiltrosMiolo(minimo: Int?, maximo: Int?) =
                viewModelScope.launch { preferenciasManager.salvarFiltrosMiolo(minimo, maximo) }

        /** Salva as configurações de filtro de múltiplos de 3. */
        fun salvarFiltrosMultiplosTres(minimo: Int?, maximo: Int?) =
                viewModelScope.launch {
                        preferenciasManager.salvarFiltrosMultiplosTres(minimo, maximo)
                }

        /** Salva as configurações de filtro de soma total. */
        fun salvarFiltrosSoma(minimo: Int?, maximo: Int?) =
                viewModelScope.launch { preferenciasManager.salvarFiltrosSoma(minimo, maximo) }

        /** Salva a lista de números fixos. */
        fun salvarNumerosFixos(numeros: List<Int>) =
                viewModelScope.launch { preferenciasManager.salvarNumerosFixos(numeros) }

        /** Salva a lista de números excluídos. */
        fun salvarNumerosExcluidos(numeros: List<Int>) =
                viewModelScope.launch { preferenciasManager.salvarNumerosExcluidos(numeros) }

        /** Reseta todas as configurações para os valores padrão. */
        fun resetarConfiguracoes() =
                viewModelScope.launch { preferenciasManager.resetarConfiguracoes() }

        /** Define se os filtros devem ser salvos automaticamente. */
        fun setSalvarFiltrosAutomaticamente(salvar: Boolean) = viewModelScope.launch {
            preferenciasManager.setSalvarFiltrosAutomaticamente(salvar)
        }

        /** Salva uma configuração de filtros completa. */
        fun salvarConfiguracaoFiltros(config: ConfiguracaoFiltros) = viewModelScope.launch {
            preferenciasManager.salvarConfiguracaoFiltros(config)
        }

        /** Carrega a configuração de filtros salva. */
        fun carregarConfiguracaoFiltros(): StateFlow<ConfiguracaoFiltros?> = // Alterado para retornar StateFlow
            preferenciasManager.configuracaoFiltros.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
}