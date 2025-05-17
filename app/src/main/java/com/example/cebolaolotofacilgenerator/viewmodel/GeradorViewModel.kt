package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.util.GeradorJogos
import com.example.cebolaolotofacilgenerator.util.GeradorJogos.ConfiguracaoGeracao
import com.example.cebolaolotofacilgenerator.util.GeradorJogos.FiltroRange
import com.example.cebolaolotofacilgenerator.util.GeradorJogos.FiltroRepeticao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para a tela de geração de jogos da Lotofácil.
 *
 * Responsável por:
 * - Gerenciar as configurações de geração (quantidade de jogos, dezenas fixas/excluídas).
 * - Aplicar filtros estatísticos selecionados pelo usuário.
 * - Coordenar o processo de geração de jogos através do [GeradorJogos].
 * - Expor o estado da operação de geração ([operacaoStatus]) e os jogos gerados ([jogosGerados]).
 * - Lidar com a persistência de algumas preferências de geração (ex: dezenas fixas) via [PreferenciasViewModel].
 */
class GeradorViewModelFactory(
    private val application: Application,
    private val filtrosViewModel: FiltrosViewModel,
    private val jogoRepository: JogoRepository?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeradorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeradorViewModel(application, filtrosViewModel, jogoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class GeradorViewModel(
    application: Application,
    private val filtrosViewModel: FiltrosViewModel,
    private val jogoRepository: JogoRepository? = null
) : AndroidViewModel(application) {

    private val preferenciasViewModel: PreferenciasViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    .create(PreferenciasViewModel::class.java)

    // Estado da operação de geração
    private val _operacaoStatus =
            MutableLiveData<OperacaoStatus>(OperacaoStatus.OCIOSO) // Inicializado
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    // Jogos gerados
    private val _jogosGerados = MutableLiveData<List<Jogo>>(emptyList())
    val jogosGerados: LiveData<List<Jogo>> = _jogosGerados

    // Mensagem para feedback (erros de validação ou sucesso/erro da geração)
    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    // Configurações de geração
    // quantidadeJogosInput agora virá diretamente de filtrosViewModel.configuracaoFiltros na UI
    // val quantidadeJogosInput: StateFlow<String> = filtrosViewModel.configuracaoFiltros.asFlow()
    //    .map { config -> config.quantidadeJogos.toString() }
    //    .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, "10")

    // Dezenas Fixas e Excluídas com StateFlow para observação na UI
    private val _numerosFixosState = MutableStateFlow<List<Int>>(emptyList())
    val numerosFixosState: StateFlow<List<Int>> = _numerosFixosState.asStateFlow()
    private var numerosFixosInterno = mutableListOf<Int>() // Lista interna para manipulação

    private val _numerosExcluidosState = MutableStateFlow<List<Int>>(emptyList())
    val numerosExcluidosState: StateFlow<List<Int>> = _numerosExcluidosState.asStateFlow()
    private var numerosExcluidosInterno = mutableListOf<Int>() // Lista interna para manipulação

    /**
     * Referência para o MainViewModel, para mostrar snackbars globais.
     * Esta abordagem pode ser melhorada com um serviço de mensagens injetado.
     */
    private var mainViewModelRef: MainViewModel? = null
    fun setMainViewModelRef(mainVm: MainViewModel) {
        mainViewModelRef = mainVm
    }

    /**
     * Bloco de inicialização.
     */
    init {
        // Carregar números fixos/excluídos persistidos, se necessário
        viewModelScope.launch {
            numerosFixosInterno.addAll(preferenciasViewModel.numerosFixosState.value)
            _numerosFixosState.value = numerosFixosInterno.toList()
            // Numeros excluídos não parecem ser persistidos via preferenciasViewModel
        }
    }

    // As variáveis de min/max locais (minPares, maxPares, etc.) são removidas.
    // Os valores virão de filtrosViewModel.configuracaoFiltros.value

    // Os StateFlows para ativação de filtros (_filtroParesImparesAtivado, etc.) e
    // para inputs de min/max (_minParesInput, etc.) são REMOVIDOS.
    // A UI observará diretamente filtrosViewModel.configuracaoFiltros.

    /**
     * Inicializa a lista de números fixos com um conjunto de dezenas. Se dezenas forem fornecidas,
     * elas são usadas e persistidas. Se não, as dezenas persistidas (já carregadas no init) são
     * mantidas.
     */
    fun inicializarComNumerosFixos(dezenas: List<Int>?) {
        if (dezenas != null) { 
            numerosFixosInterno.clear() 
            val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
            if (dezenas.isNotEmpty()) {
                dezenas.forEach { dezena ->
                    if (dezena in 1..25 &&
                                    numerosFixosInterno.size < (currentConfig.quantidadeNumerosPorJogo - 1) &&
                                    dezena !in numerosExcluidosInterno
                    ) {
                        if (!numerosFixosInterno.contains(dezena)) {
                            numerosFixosInterno.add(dezena)
                        }
                    }
                }
            }
            _numerosFixosState.value = numerosFixosInterno.toList()
            preferenciasViewModel.salvarNumerosFixos(numerosFixosInterno.toList())
        }
    }

    /** Reseta todas as configurações para os valores padrão. */
    fun resetarConfiguracoes() {
        viewModelScope.launch {
            filtrosViewModel.atualizarFiltro(ConfiguracaoFiltros()) 
        }
        numerosFixosInterno.clear()
        _numerosFixosState.value = emptyList()
        preferenciasViewModel.salvarNumerosFixos(emptyList()) 
        numerosExcluidosInterno.clear()
        _numerosExcluidosState.value = emptyList()
        _jogosGerados.value = emptyList()
        _operacaoStatus.value = OperacaoStatus.OCIOSO
        _mensagem.value = null
    }

    /**
     * Adiciona ou remove um número da lista de números fixos.
     * @param numero O número a ser adicionado ou removido.
     * @return Verdadeiro se o número foi adicionado, falso se foi removido.
     */
    fun toggleNumeroFixo(numero: Int): Boolean {
        val alterado: Boolean
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        if (numero in numerosFixosInterno) {
            numerosFixosInterno.remove(numero)
            alterado = false
        } else {
            if (numerosFixosInterno.size < currentConfig.quantidadeNumerosPorJogo - 1) { 
                if (numero !in numerosExcluidosInterno) { 
                    numerosFixosInterno.add(numero)
                    alterado = true
                } else {
                    _mensagem.value = getApplication<Application>().getString(R.string.erro_numero_excluido_nao_pode_ser_fixo)
                    alterado = false
                }
            } else {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_maximo_numeros_fixos_atingido)
                alterado = false
            }
        }
        _numerosFixosState.value = numerosFixosInterno.toList()
        preferenciasViewModel.salvarNumerosFixos(numerosFixosInterno.toList())
        return alterado
    }

    /**
     * Adiciona ou remove um número da lista de números excluídos.
     * @param numero O número a ser adicionado ou removido.
     * @return Verdadeiro se o número foi adicionado, falso se foi removido.
     */
    fun toggleNumeroExcluido(numero: Int): Boolean {
        val alterado: Boolean
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        if (numero in numerosExcluidosInterno) {
            numerosExcluidosInterno.remove(numero)
            alterado = false
        } else {
            if (25 - numerosExcluidosInterno.size > currentConfig.quantidadeNumerosPorJogo) {
                if (numero !in numerosFixosInterno) { 
                    numerosExcluidosInterno.add(numero)
                    alterado = true
                } else {
                    _mensagem.value = getApplication<Application>().getString(R.string.erro_numero_fixo_nao_pode_ser_excluido)
                    alterado = false
                }
            } else {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_maximo_numeros_excluidos_atingido)
                alterado = false
            }
        }
        if (alterado || !numerosExcluidosInterno.contains(numero)) { 
            _numerosExcluidosState.value = numerosExcluidosInterno.toList()
        }
        return alterado
    }

    /**
     * Verifica se há sobreposição entre números fixos e excluídos.
     * @return Lista de números que estão tanto na lista de fixos quanto na de excluídos.
     */
    fun verificarSobreposicao(): List<Int> {
        return numerosFixosInterno.filter { it in numerosExcluidosInterno }
    }

    /**
     * Verifica se as configurações atuais são válidas para geração.
     * @return Um par com o status (verdadeiro se válido) e uma mensagem de erro (se houver).
     */
    fun validarConfiguracoes(): Pair<Boolean, String?> {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: run {
            return Pair(false, getApplication<Application>().getString(R.string.erro_configuracao_filtros_nao_carregada))
        }

        if (currentConfig.quantidadeNumerosPorJogo !in 15..20) {
            return Pair(false, getApplication<Application>().getString(R.string.erro_quantidade_numeros_invalida, 15, 20))
        }
        if (currentConfig.quantidadeJogos <= 0) {
            return Pair(false, getApplication<Application>().getString(R.string.erro_quantidade_jogos_invalida))
        }
        if (numerosFixosState.value.size >= currentConfig.quantidadeNumerosPorJogo) {
            return Pair(false, getApplication<Application>().getString(R.string.erro_muitos_numeros_fixos, currentConfig.quantidadeNumerosPorJogo))
        }
        val sobreposicao = numerosFixosState.value.intersect(numerosExcluidosState.value.toSet())
        if (sobreposicao.isNotEmpty()) {
            return Pair(false, getApplication<Application>().getString(R.string.erro_sobreposicao_fixos_excluidos, sobreposicao.joinToString()))
        }
        if (25 - numerosExcluidosState.value.size < currentConfig.quantidadeNumerosPorJogo - numerosFixosState.value.size) {
            return Pair(false, getApplication<Application>().getString(R.string.erro_muitos_numeros_excluidos))
        }

        if (currentConfig.filtroParesImpares) {
            if (currentConfig.minImpares < 0 || currentConfig.maxImpares > currentConfig.quantidadeNumerosPorJogo || currentConfig.minImpares > currentConfig.maxImpares) {
                return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_impares_invalido))
            }
        }
        if (currentConfig.filtroSomaTotal) {
            if (currentConfig.minSoma <= 0 || currentConfig.maxSoma <= 0 || currentConfig.minSoma > currentConfig.maxSoma) {
                 return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_soma_total_invalido))
            }
        }
        if (currentConfig.filtroPrimos) {
            if (currentConfig.minPrimos < 0 || currentConfig.maxPrimos > GeradorJogos.PRIMOS.size || currentConfig.minPrimos > currentConfig.maxPrimos) {
                return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_primos_invalido))
            }
        }
        if (currentConfig.filtroFibonacci) {
            if (currentConfig.minFibonacci < 0 || currentConfig.maxFibonacci > GeradorJogos.FIBONACCI.size || currentConfig.minFibonacci > currentConfig.maxFibonacci) {
                return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_fibonacci_invalido))
            }
        }
        if (currentConfig.filtroMioloMoldura) {
            if (currentConfig.minMiolo < 0 || currentConfig.maxMiolo > GeradorJogos.MIOLO.size || currentConfig.minMiolo > currentConfig.maxMiolo) {
                return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_miolo_invalido))
            }
        }
        if (currentConfig.filtroMultiplosDeTres) {
            if (currentConfig.minMultiplos < 0 || currentConfig.maxMultiplos > GeradorJogos.MULTIPLOS_DE_TRES.size || currentConfig.minMultiplos > currentConfig.maxMultiplos) {
                return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_multiplos_tres_invalido))
            }
        }
        if (currentConfig.filtroRepeticaoConcursoAnterior) {
            if (currentConfig.minRepeticaoConcursoAnterior < 0 || currentConfig.maxRepeticaoConcursoAnterior > currentConfig.quantidadeNumerosPorJogo || currentConfig.minRepeticaoConcursoAnterior > currentConfig.maxRepeticaoConcursoAnterior) {
                return Pair(false, getApplication<Application>().getString(R.string.erro_filtro_repeticao_invalido))
            }
            if (currentConfig.dezenasConcursoAnterior.isEmpty()) {
                 return Pair(false, getApplication<Application>().getString(R.string.erro_ultimo_resultado_nao_salvo_para_filtro_repeticao))
            }
        }
        return Pair(true, null)
    }

    /**
     * Inicia o processo de geração de jogos com base nas configurações atuais.
     */
    fun gerarJogos() {
        viewModelScope.launch {
            _operacaoStatus.value = OperacaoStatus.CARREGANDO
            val (isValid, errorMessage) = validarConfiguracoes()
            if (!isValid) {
                _mensagem.value = errorMessage
                _operacaoStatus.value = OperacaoStatus.ERRO
                mainViewModelRef?.showSnackbar(errorMessage ?: getApplication<Application>().getString(R.string.erro_validacao_configuracao_geral))
                return@launch
            }

            val currentConfigFiltros = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros() // Should not be null due to validation

            val configGeracao = ConfiguracaoGeracao(
                quantidadeJogos = currentConfigFiltros.quantidadeJogos,
                quantidadeNumerosPorJogo = currentConfigFiltros.quantidadeNumerosPorJogo,
                numerosFixos = numerosFixosState.value.toList(),
                numerosExcluidos = numerosExcluidosState.value.toList(),
                filtroParesImpares = if (currentConfigFiltros.filtroParesImpares) FiltroRange(currentConfigFiltros.minImpares, currentConfigFiltros.maxImpares) else null,
                filtroSomaTotal = if (currentConfigFiltros.filtroSomaTotal) FiltroRange(currentConfigFiltros.minSoma, currentConfigFiltros.maxSoma) else null,
                filtroPrimos = if (currentConfigFiltros.filtroPrimos) FiltroRange(currentConfigFiltros.minPrimos, currentConfigFiltros.maxPrimos) else null,
                filtroFibonacci = if (currentConfigFiltros.filtroFibonacci) FiltroRange(currentConfigFiltros.minFibonacci, currentConfigFiltros.maxFibonacci) else null,
                filtroMiolo = if (currentConfigFiltros.filtroMioloMoldura) FiltroRange(currentConfigFiltros.minMiolo, currentConfigFiltros.maxMiolo) else null,
                filtroMultiplosDeTres = if (currentConfigFiltros.filtroMultiplosDeTres) FiltroRange(currentConfigFiltros.minMultiplos, currentConfigFiltros.maxMultiplos) else null,
                filtroRepeticaoAnterior = if (currentConfigFiltros.filtroRepeticaoConcursoAnterior && currentConfigFiltros.dezenasConcursoAnterior.isNotEmpty()) {
                    FiltroRepeticao(
                        dezenasAnteriores = currentConfigFiltros.dezenasConcursoAnterior,
                        range = FiltroRange(currentConfigFiltros.minRepeticaoConcursoAnterior, currentConfigFiltros.maxRepeticaoConcursoAnterior)
                    )
                } else null,
                ultimoResultadoConcursoAnterior = currentConfigFiltros.dezenasConcursoAnterior // For reference
            )
            
            try {
                val jogosGeradosPeloMotor: List<Jogo> = withContext(Dispatchers.Default) {
                    GeradorJogos.gerarJogos(config = configGeracao)
                }

                if (jogosGeradosPeloMotor.isNotEmpty()) {
                    _jogosGerados.value = jogosGeradosPeloMotor
                    val quantidade = jogosGeradosPeloMotor.size
                    _mensagem.value = getApplication<Application>().resources.getQuantityString(R.plurals.jogos_gerados_sucesso_contagem, quantidade, quantidade)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                    mainViewModelRef?.showSnackbar(_mensagem.value!!)
                } else {
                    _jogosGerados.value = emptyList()
                    _operacaoStatus.value = OperacaoStatus.ERRO // Ou SUCESSO com mensagem de nenhum jogo
                    _mensagem.value = getApplication<Application>().getString(R.string.nenhum_jogo_gerado_filtros_restritivos)
                    mainViewModelRef?.showSnackbar(_mensagem.value!!)
                }
            } catch (e: IllegalArgumentException) {
                _mensagem.value = e.message ?: getApplication<Application>().getString(R.string.erro_geracao_jogos_argumento_invalido)
                _operacaoStatus.value = OperacaoStatus.ERRO
                 mainViewModelRef?.showSnackbar(_mensagem.value!!)
            } catch (e: Exception) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_desconhecido_geracao_jogos) + ": ${e.localizedMessage}"
                _operacaoStatus.value = OperacaoStatus.ERRO
                mainViewModelRef?.showSnackbar(_mensagem.value!!)
            } finally {
                 if (_operacaoStatus.value == OperacaoStatus.CARREGANDO) { 
                    _operacaoStatus.value = OperacaoStatus.OCIOSO 
                }
            }
        }
    }

    /** Limpa a lista de jogos gerados. */
    fun limparJogosGerados() {
        _jogosGerados.value = emptyList()
        _operacaoStatus.value = OperacaoStatus.OCIOSO
        _mensagem.value = null
    }

    /** Reseta o status da operação. */
    fun resetarStatusOperacao() {
        _operacaoStatus.value = OperacaoStatus.OCIOSO
        _mensagem.value = null 
    }

    fun limparMensagemUnica() {
        _mensagem.value = null
    }

    // Métodos setFiltro[NomeFiltro]Ativado e set[Min/Max][NomeFiltro]Input são REMOVIDOS.
    // A UI (GeradorScreen) irá chamar diretamente os métodos do filtrosViewModel
    // ou métodos no GeradorViewModel que delegam para filtrosViewModel.atualizarFiltro().

    // Novo conjunto de métodos para atualizar ConfiguracaoFiltros no FiltrosViewModel
    fun atualizarFiltroParesImpares(ativado: Boolean, min: Int, max: Int) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(filtroParesImpares = ativado, minImpares = min, maxImpares = max))
    }

    fun atualizarFiltroSomaTotal(ativado: Boolean, min: Int, max: Int) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(filtroSomaTotal = ativado, minSoma = min, maxSoma = max))
    }

    fun atualizarFiltroPrimos(ativado: Boolean, min: Int, max: Int) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(filtroPrimos = ativado, minPrimos = min, maxPrimos = max))
    }

    fun atualizarFiltroFibonacci(ativado: Boolean, min: Int, max: Int) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(filtroFibonacci = ativado, minFibonacci = min, maxFibonacci = max))
    }

    fun atualizarFiltroMioloMoldura(ativado: Boolean, min: Int, max: Int) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(filtroMioloMoldura = ativado, minMiolo = min, maxMiolo = max))
    }

    fun atualizarFiltroMultiplosDeTres(ativado: Boolean, min: Int, max: Int) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(filtroMultiplosDeTres = ativado, minMultiplos = min, maxMultiplos = max))
    }

    fun atualizarFiltroRepeticao(ativado: Boolean, min: Int, max: Int, dezenas: List<Int>? = null) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        val dezenasParaUsar = dezenas ?: currentConfig.dezenasConcursoAnterior
        filtrosViewModel.atualizarFiltro(currentConfig.copy(
            filtroRepeticaoConcursoAnterior = ativado, 
            minRepeticaoConcursoAnterior = min, 
            maxRepeticaoConcursoAnterior = max,
            dezenasConcursoAnterior = dezenasParaUsar
            ))
    }
    
    fun atualizarDezenasConcursoAnterior(dezenas: List<Int>) {
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(currentConfig.copy(dezenasConcursoAnterior = dezenas))
    }

    /** Salva a lista de jogos atualmente em [jogosGerados] no repositório. */
    fun salvarJogosGerados() {
        viewModelScope.launch {
            val jogosParaSalvar = _jogosGerados.value
            val repo = jogoRepository 
            if (repo == null) {
                val msgErroRepo = getApplication<Application>().getString(R.string.erro_repositorio_indisponivel)
                _mensagem.value = msgErroRepo
                mainViewModelRef?.showSnackbar(msgErroRepo)
                return@launch
            }
            if (!jogosParaSalvar.isNullOrEmpty()) {
                try {
                    repo.inserirJogos(jogosParaSalvar)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO 
                    val msgSucesso = getApplication<Application>().getString(R.string.jogos_salvos_com_sucesso)
                    _mensagem.value = msgSucesso
                    mainViewModelRef?.showSnackbar(msgSucesso)
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO 
                    val msgErro = getApplication<Application>().getString(R.string.erro_salvar_jogos) + ": " + e.message
                    _mensagem.value = msgErro
                    mainViewModelRef?.showSnackbar(msgErro)
                }
            } else {
                val msgNenhumJogo = getApplication<Application>().getString(R.string.nenhum_jogo_para_salvar)
                _mensagem.value = msgNenhumJogo
                 mainViewModelRef?.showSnackbar(msgNenhumJogo)
            }
        }
    }

    /**
     * Salva a configuração de filtros atual (obtida de [FiltrosViewModel]) nas preferências do usuário.
     */
    fun salvarFiltrosConfigAtual() { 
        viewModelScope.launch {
            val configAtual = filtrosViewModel.configuracaoFiltros.value
            if (configAtual != null) {
                preferenciasViewModel.salvarConfiguracaoFiltros(configAtual)
                 mainViewModelRef?.showSnackbar(getApplication<Application>().getString(R.string.configuracao_filtros_salva_sucesso))
            } else {
                 mainViewModelRef?.showSnackbar(getApplication<Application>().getString(R.string.erro_salvar_configuracao_filtros_null))
            }
        }
    }

    /** Reseta as configurações de filtros para os valores padrão no [FiltrosViewModel]. */
    fun resetarConfiguracaoFiltrosParaPadrao() {
        viewModelScope.launch {
            // A função resetarConfiguracoesParaPadrao() deve existir em FiltrosViewModel
            // Se não existir, precisamos criá-la ou chamar filtrosViewModel.atualizarFiltro(ConfiguracaoFiltros())
            filtrosViewModel.resetarConfiguracaoFiltros() 
            mainViewModelRef?.showSnackbar(getApplication<Application>().getString(R.string.configuracao_filtros_resetada_padrao))
        }
    }
}