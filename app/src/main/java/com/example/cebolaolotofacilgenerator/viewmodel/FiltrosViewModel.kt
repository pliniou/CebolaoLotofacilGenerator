package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
// import androidx.lifecycle.asFlow // Não será mais usado para ResultadoRepository
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
// import com.example.cebolaolotofacilgenerator.data.model.Resultado // Não mais necessário
// import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository // REMOVER
import com.example.cebolaolotofacilgenerator.util.PreferenciasManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FiltrosViewModel(
    application: Application
    // private val resultadoRepository: ResultadoRepository // REMOVER
) : AndroidViewModel(application) {

    private val preferenciasManager = PreferenciasManager(application)

    private val _configuracaoFiltros = MutableLiveData<ConfiguracaoFiltros>()
    val configuracaoFiltros: LiveData<ConfiguracaoFiltros> = _configuracaoFiltros
    
    // Expor dezenas do concurso anterior para a UI
    val dezenasConcursoAnterior: LiveData<List<Int>> = MutableLiveData()

    // Para mensagens de feedback
    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    init {
        carregarConfiguracoesIniciais()
    }

    private fun carregarConfiguracoesIniciais() {
        viewModelScope.launch {
            val configSalva = preferenciasManager.configuracaoFiltros.firstOrNull()
            _configuracaoFiltros.value = configSalva ?: ConfiguracaoFiltros()
            (dezenasConcursoAnterior as MutableLiveData).value = _configuracaoFiltros.value?.dezenasConcursoAnterior ?: emptyList()
        }
    }

    // Não precisamos mais de carregarDezenasDoUltimoResultadoSalvo, pois as dezenas já virão com ConfiguracaoFiltros

    fun atualizarFiltro(novoFiltro: ConfiguracaoFiltros) {
        _configuracaoFiltros.value = novoFiltro
        // Atualizar o LiveData separado também, se necessário para observadores específicos
         (dezenasConcursoAnterior as MutableLiveData).value = novoFiltro.dezenasConcursoAnterior
        // Considerar salvar automaticamente se a preferência estiver ativa
        // viewModelScope.launch {
        //     if (preferenciasManager.salvarFiltrosAutomaticamente.firstOrNull() == true) {
        //         salvarConfiguracaoFiltrosCompleta()
        //     }
        // }
    }
    
    fun atualizarDezenasConcursoAnterior(novasDezenas: List<Int>) {
        val currentConfig = _configuracaoFiltros.value ?: ConfiguracaoFiltros()
        _configuracaoFiltros.value = currentConfig.copy(dezenasConcursoAnterior = novasDezenas.sorted())
        (dezenasConcursoAnterior as MutableLiveData).value = novasDezenas.sorted()
        // Salvar automaticamente ou via botão "Aplicar/Salvar Filtros"
        // Se for salvar aqui, chamar salvarConfiguracaoFiltrosCompleta()
    }

    private fun salvarConfiguracaoFiltrosCompleta() { // Tornar privado se chamado apenas internamente
        viewModelScope.launch {
            _configuracaoFiltros.value?.let {
                preferenciasManager.salvarConfiguracaoFiltros(it)
                // _mensagem.value = getApplication<Application>().getString(R.string.filtros_salvos_com_sucesso) // Exemplo
            }
        }
    }

    fun salvarConfiguracaoFiltros() { // Renomeado de aplicarFiltros para consistência com a UI
        salvarConfiguracaoFiltrosCompleta()
        _mensagem.value = "Configurações de filtro salvas com sucesso." // Placeholder para R.string.filtros_aplicados_com_sucesso ou R.string.filtros_salvos_com_sucesso
    }
    
    fun resetarConfiguracaoFiltros() { // Renomeado de resetarFiltros
        val defaultConfig = ConfiguracaoFiltros()
        _configuracaoFiltros.value = defaultConfig
        (dezenasConcursoAnterior as MutableLiveData).value = defaultConfig.dezenasConcursoAnterior
        salvarConfiguracaoFiltrosCompleta() // Salva a configuração resetada
    }

    // Métodos como setQuantidadePares, setQuantidadeImpares, setFiltrarPrimos, setFiltrarFibonacci
    // podem ser mantidos se a UI os chama individualmente, mas eles devem operar sobre
    // _configuracaoFiltros.value e então, opcionalmente, acionar um salvamento.
    // Idealmente, a UI construiria o objeto ConfiguracaoFiltros e chamaria atualizarFiltro().
    // Por ora, vou mantê-los e garantir que atualizem _configuracaoFiltros.value.

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
    // Adicionar outros setters se existirem e forem usados (ex: Soma, Miolo, Múltiplos de 3, etc.)
    // seguindo o mesmo padrão.

    fun limparMensagem() {
        _mensagem.value = null
    }
}
