package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.util.PreferenciasManager
import kotlinx.coroutines.launch

class FiltrosViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenciasManager = PreferenciasManager(application)

    private val _configuracaoFiltros = MutableLiveData<ConfiguracaoFiltros>()
    val configuracaoFiltros: LiveData<ConfiguracaoFiltros> = _configuracaoFiltros

    // Para compatibilidade com o fragmento que espera filtrosAplicados
    val filtrosAplicados: LiveData<ConfiguracaoFiltros>
        get() = _configuracaoFiltros

    init {
        carregarConfiguracoesIniciais()
    }

    private fun carregarConfiguracoesIniciais() {
        viewModelScope.launch {
            // TODO: Carregar de forma mais completa do PreferenciasManager
            // Por enquanto, apenas carrega os defaults da data class
            _configuracaoFiltros.value = ConfiguracaoFiltros()
        }
    }

    fun atualizarFiltro(novoFiltro: ConfiguracaoFiltros) {
        _configuracaoFiltros.value = novoFiltro
    }

    fun salvarFiltros() {
        viewModelScope.launch {
            _configuracaoFiltros.value?.let {
                // TODO: Implementar a lógica de salvar cada campo no PreferenciasManager
                // Ex: preferenciasManager.salvarFiltrosPares(it.minPares, it.maxPares) // Adaptar
                // para impares
                // preferenciasManager.salvarFiltroBooleano(FILTRO_PARES_IMPARES_ATIVO,
                // it.filtroParesImpares)
            }
        }
    }

    fun resetarFiltros() {
        val defaultConfig = ConfiguracaoFiltros()
        _configuracaoFiltros.value = defaultConfig
        // TODO: Salvar os defaults no PreferenciasManager também
        salvarFiltros() // Chama o salvar para persistir os defaults
    }

    // Métodos chamados pelo Fragment (alguns podem ser substituídos por atualizações diretas no
    // _configuracaoFiltros.value)
    fun setQuantidadePares(min: Int, max: Int) {
        val currentConfig = _configuracaoFiltros.value ?: ConfiguracaoFiltros()
        // Esta abordagem é um pouco estranha se ConfiguracaoFiltros não tem min/maxPares diretos.
        // Assumindo que o fragmento quer atualizar minImpares/maxImpares com base nisso.
        // Melhor seria o fragmento atualizar o objeto ConfiguracaoFiltros e chamar atualizarFiltro.
    }

    fun setQuantidadeImpares(min: Int, max: Int) {
        val currentConfig = _configuracaoFiltros.value ?: ConfiguracaoFiltros()
        _configuracaoFiltros.value = currentConfig.copy(minImpares = min, maxImpares = max)
    }

    fun setFiltrarPrimos(isChecked: Boolean, min: Int? = null, max: Int? = null) {
        val currentConfig = _configuracaoFiltros.value ?: ConfiguracaoFiltros()
        _configuracaoFiltros.value =
                currentConfig.copy(
                        filtroPrimos = isChecked,
                        minPrimos = min ?: currentConfig.minPrimos,
                        maxPrimos = max ?: currentConfig.maxPrimos
                )
    }

    fun setFiltrarFibonacci(isChecked: Boolean, min: Int? = null, max: Int? = null) {
        val currentConfig = _configuracaoFiltros.value ?: ConfiguracaoFiltros()
        _configuracaoFiltros.value =
                currentConfig.copy(
                        filtroFibonacci = isChecked,
                        minFibonacci = min ?: currentConfig.minFibonacci,
                        maxFibonacci = max ?: currentConfig.maxFibonacci
                )
    }

    fun aplicarFiltros() {
        // A ação de "aplicar" geralmente significa usar os filtros para alguma operação.
        // Neste contexto, pode ser apenas salvar as configurações atuais.
        salvarFiltros()
    }
}
