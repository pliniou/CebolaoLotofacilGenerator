package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow // Necessário para converter LiveData para Flow
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import com.example.cebolaolotofacilgenerator.util.PreferenciasManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FiltrosViewModel(
    application: Application,
    private val resultadoRepository: ResultadoRepository // Injetar ResultadoRepository
) : AndroidViewModel(application) {

    private val preferenciasManager = PreferenciasManager(application)

    private val _configuracaoFiltros = MutableLiveData<ConfiguracaoFiltros>()
    val configuracaoFiltros: LiveData<ConfiguracaoFiltros> = _configuracaoFiltros

    // Para compatibilidade com o fragmento que espera filtrosAplicados
    val filtrosAplicados: LiveData<ConfiguracaoFiltros>
        get() = _configuracaoFiltros

    // Para mensagens de feedback
    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    // StateFlow para o último resultado salvo
    val ultimoResultado: StateFlow<Resultado?> =
        resultadoRepository
            .ultimoResultado // Supõe que isso retorna LiveData<Resultado?>
            .asFlow() // Converte LiveData para Flow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    // StateFlow para indicar se existe um último resultado salvo
    val temUltimoResultadoSalvo: StateFlow<Boolean> =
        ultimoResultado
            .map { it != null && it.numeros.isNotEmpty() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )


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

    fun carregarDezenasDoUltimoResultadoSalvo() {
        viewModelScope.launch {
            val ultimo = ultimoResultado.value
            if (ultimo != null && ultimo.numeros.isNotEmpty()) {
                val currentConfig = _configuracaoFiltros.value ?: ConfiguracaoFiltros()
                _configuracaoFiltros.value = currentConfig.copy(dezenasConcursoAnterior = ultimo.numeros.sorted())
                _mensagem.value = getApplication<Application>().getString(R.string.dezenas_ultimo_resultado_carregadas)
            } else {
                _mensagem.value = getApplication<Application>().getString(R.string.nenhum_resultado_salvo_para_carregar_dezenas)
            }
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
                // Salvar também as dezenas do concurso anterior se necessário
                // preferenciasManager.salvarDezenasConcursoAnterior(it.dezenasConcursoAnterior)
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
    // TODO: Revisar a necessidade desses métodos específicos se a UI manipula o objeto completo.
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
        _mensagem.value = getApplication<Application>().getString(R.string.filtros_aplicados_com_sucesso) // Adicionar esta string
    }

    fun limparMensagem() {
        _mensagem.value = null
    }
}
