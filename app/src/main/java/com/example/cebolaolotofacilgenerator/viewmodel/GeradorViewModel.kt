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
import com.example.cebolaolotofacilgenerator.ui.viewmodel.FiltrosViewModel
import com.example.cebolaolotofacilgenerator.util.GeradorJogos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.stateIn

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
    private val jogoRepository: JogoRepository? // Mantendo opcional por enquanto
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
    private val filtrosViewModel: FiltrosViewModel, // Injetar FiltrosViewModel
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
    val quantidadeJogosInput: StateFlow<String> = filtrosViewModel.configuracaoFiltros.map { it.quantidadeJogos.toString() }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, "10")

    // Dezenas Fixas e Excluídas com StateFlow para observação na UI
    private val _numerosFixosState = MutableStateFlow<List<Int>>(emptyList())
    val numerosFixosState: StateFlow<List<Int>> = _numerosFixosState.asStateFlow()
    private var numerosFixosInterno = mutableListOf<Int>() // Lista interna para manipulação

    private val _numerosExcluidosState = MutableStateFlow<List<Int>>(emptyList())
    val numerosExcluidosState: StateFlow<List<Int>> = _numerosExcluidosState.asStateFlow()
    private var numerosExcluidosInterno = mutableListOf<Int>() // Lista interna para manipulação

    /**
     * Bloco de inicialização. Carrega as dezenas fixas persistidas ao criar o ViewModel.
     */
    init {
        viewModelScope.launch {
            // Carrega as dezenas fixas persistidas na inicialização
            // Usar .first() para pegar o valor inicial do StateFlow das preferências
            val dezenasFixasPersistidas = preferenciasViewModel.numerosFixosState.first()
            // Se `filtrosViewModel` já carrega isso do DataStore, esta lógica pode ser redundante
            // ou precisar de sincronização.
            // Por ora, vamos priorizar o `filtrosViewModel.configuracaoFiltros` na geração.
        }
    }

    // Filtros estatísticos
    var minPares: Int? = null
    var maxPares: Int? = null
    var minPrimos: Int? = null
    var maxPrimos: Int? = null
    var minFibonacci: Int? = null
    var maxFibonacci: Int? = null
    var minMiolo: Int? = null
    var maxMiolo: Int? = null
    var minMultiplosTres: Int? = null
    var maxMultiplosTres: Int? = null
    var minSoma: Int? = null
    var maxSoma: Int? = null
    // Variáveis para o filtro de Repetição de Dezenas
    var minRepeticao: Int? = null
    var maxRepeticao: Int? = null

    // Filtro: Pares e Ímpares (os inputs serão para ÍMPARES)
    private val _filtroParesImparesAtivado = MutableStateFlow(false)
    val filtroParesImparesAtivado: StateFlow<Boolean> = _filtroParesImparesAtivado.asStateFlow()

    private val _minParesInput = MutableStateFlow("7") // Padrão para ÍMPARES
    val minParesInput: StateFlow<String> = _minParesInput.asStateFlow()

    private val _maxParesInput = MutableStateFlow("9") // Padrão para ÍMPARES
    val maxParesInput: StateFlow<String> = _maxParesInput.asStateFlow()

    // Filtro: Soma Total
    private val _filtroSomaTotalAtivado = MutableStateFlow(false)
    val filtroSomaTotalAtivado: StateFlow<Boolean> = _filtroSomaTotalAtivado.asStateFlow()
    private val _minSomaInput = MutableStateFlow("")
    val minSomaInput: StateFlow<String> = _minSomaInput.asStateFlow()
    private val _maxSomaInput = MutableStateFlow("")
    val maxSomaInput: StateFlow<String> = _maxSomaInput.asStateFlow()

    // Filtro: Números Primos
    private val _filtroPrimosAtivado = MutableStateFlow(false)
    val filtroPrimosAtivado: StateFlow<Boolean> = _filtroPrimosAtivado.asStateFlow()
    private val _minPrimosInput = MutableStateFlow("")
    val minPrimosInput: StateFlow<String> = _minPrimosInput.asStateFlow()
    private val _maxPrimosInput = MutableStateFlow("")
    val maxPrimosInput: StateFlow<String> = _maxPrimosInput.asStateFlow()

    // Filtro: Números de Fibonacci
    private val _filtroFibonacciAtivado = MutableStateFlow(false)
    val filtroFibonacciAtivado: StateFlow<Boolean> = _filtroFibonacciAtivado.asStateFlow()
    private val _minFibonacciInput = MutableStateFlow("")
    val minFibonacciInput: StateFlow<String> = _minFibonacciInput.asStateFlow()
    private val _maxFibonacciInput = MutableStateFlow("")
    val maxFibonacciInput: StateFlow<String> = _maxFibonacciInput.asStateFlow()

    // Filtro: Miolo e Moldura (foco na quantidade de dezenas do Miolo)
    private val _filtroMioloMolduraAtivado = MutableStateFlow(false)
    val filtroMioloMolduraAtivado: StateFlow<Boolean> = _filtroMioloMolduraAtivado.asStateFlow()
    private val _minMioloInput = MutableStateFlow("")
    val minMioloInput: StateFlow<String> = _minMioloInput.asStateFlow()
    private val _maxMioloInput = MutableStateFlow("")
    val maxMioloInput: StateFlow<String> = _maxMioloInput.asStateFlow()

    // Filtro: Múltiplos de 3
    private val _filtroMultiplosDeTresAtivado = MutableStateFlow(false)
    val filtroMultiplosDeTresAtivado: StateFlow<Boolean> =
            _filtroMultiplosDeTresAtivado.asStateFlow()
    private val _minMultiplosDeTresInput = MutableStateFlow("")
    val minMultiplosDeTresInput: StateFlow<String> = _minMultiplosDeTresInput.asStateFlow()
    private val _maxMultiplosDeTresInput = MutableStateFlow("")
    val maxMultiplosDeTresInput: StateFlow<String> = _maxMultiplosDeTresInput.asStateFlow()

    // Filtro: Repetição de Dezenas do Concurso Anterior
    private val _filtroRepeticaoDezenasAtivado = MutableStateFlow(false)
    val filtroRepeticaoDezenasAtivado: StateFlow<Boolean> = _filtroRepeticaoDezenasAtivado.asStateFlow()

    private val _minRepeticaoInput = MutableStateFlow("8") // Valor padrão
    val minRepeticaoInput: StateFlow<String> = _minRepeticaoInput.asStateFlow()

    private val _maxRepeticaoInput = MutableStateFlow("10") // Valor padrão
    val maxRepeticaoInput: StateFlow<String> = _maxRepeticaoInput.asStateFlow()

    /**
     * Inicializa a lista de números fixos com um conjunto de dezenas. Se dezenas forem fornecidas,
     * elas são usadas e persistidas. Se não, as dezenas persistidas (já carregadas no init) são
     * mantidas.
     */
    fun inicializarComNumerosFixos(dezenas: List<Int>?) {
        if (dezenas != null) { // Apenas processa se dezenas não for null
            numerosFixosInterno.clear() // Limpa a lista atual de números fixos
            if (dezenas.isNotEmpty()) {
                dezenas.forEach { dezena ->
                    if (dezena in 1..25 &&
                                    numerosFixosInterno.size < (quantidadeNumeros - 1) &&
                                    dezena !in numerosExcluidosInterno
                    ) {
                        if (!numerosFixosInterno.contains(dezena)) {
                            numerosFixosInterno.add(dezena)
                        }
                    }
                }
            }
            _numerosFixosState.value = numerosFixosInterno.toList() // Atualiza o StateFlow
            // Persiste as novas dezenas fixas
            preferenciasViewModel.salvarNumerosFixos(numerosFixosInterno.toList())
        }
        // Se dezenas for null, significa que não vieram argumentos de navegação,
        // então as dezenas carregadas do DataStore no init são mantidas.
    }

    /** Reseta todas as configurações para os valores padrão. */
    fun resetarConfiguracoes() {
        _quantidadeJogosInput.value = "10" // Resetar input
        quantidadeJogos = 10
        _quantidadeNumerosInput.value = "15" // Resetar input
        quantidadeNumeros = 15

        numerosFixosInterno.clear()
        _numerosFixosState.value = emptyList()
        preferenciasViewModel.salvarNumerosFixos(emptyList()) // Limpa persistência

        numerosExcluidosInterno.clear()
        _numerosExcluidosState.value = emptyList()
        // Não há persistência para excluídos no escopo atual, mas se houvesse, limparia aqui.

        minPares = null
        maxPares = null
        minPrimos = null
        maxPrimos = null
        minFibonacci = null
        maxFibonacci = null
        minMiolo = null
        maxMiolo = null
        minMultiplosTres = null
        maxMultiplosTres = null
        minSoma = null
        maxSoma = null
        // Reset para Repetição de Dezenas
        minRepeticao = null
        maxRepeticao = null

        _jogosGerados.value = emptyList() // Limpa jogos gerados ao resetar config
        _operacaoStatus.value = OperacaoStatus.OCIOSO
        _mensagem.value = null
        _filtroParesImparesAtivado.value = false // Resetar ativação do filtro
        _minParesInput.value = "7" // Resetar input para ÍMPARES
        _maxParesInput.value = "9" // Resetar input para ÍMPARES
        minPares = null // Assegurar que a versão Int também seja resetada (será interpretado como ímpares)
        maxPares = null // Assegurar que a versão Int também seja resetada (será interpretado como ímpares)

        _filtroSomaTotalAtivado.value = false
        _minSomaInput.value = ""
        _maxSomaInput.value = ""
        minSoma = null // Resetar Int
        maxSoma = null // Resetar Int

        _filtroPrimosAtivado.value = false
        _minPrimosInput.value = ""
        minPrimos = null
        _maxPrimosInput.value = ""
        maxPrimos = null

        _filtroFibonacciAtivado.value = false
        _minFibonacciInput.value = ""
        minFibonacci = null
        _maxFibonacciInput.value = ""
        maxFibonacci = null

        _filtroMioloMolduraAtivado.value = false
        _minMioloInput.value = ""
        minMiolo = null
        _maxMioloInput.value = ""
        maxMiolo = null

        _filtroMultiplosDeTresAtivado.value = false
        _minMultiplosDeTresInput.value = ""
        minMultiplosTres = null
        _maxMultiplosDeTresInput.value = ""
        maxMultiplosTres = null

        // Reset para Repetição de Dezenas
        _filtroRepeticaoDezenasAtivado.value = false
        _minRepeticaoInput.value = "8"
        _maxRepeticaoInput.value = "10"
        minRepeticao = null
        maxRepeticao = null
    }

    /**
     * Adiciona ou remove um número da lista de números fixos.
     * @param numero O número a ser adicionado ou removido.
     * @return Verdadeiro se o número foi adicionado, falso se foi removido.
     */
    fun toggleNumeroFixo(numero: Int): Boolean {
        val alterado: Boolean
        if (numero in numerosFixosInterno) {
            numerosFixosInterno.remove(numero)
            alterado = false
        } else {
            if (numerosFixosInterno.size < quantidadeNumeros - 1) { // Limita o número de fixos
                if (numero !in numerosExcluidosInterno
                ) { // Não pode ser fixo se já estiver excluído
                    numerosFixosInterno.add(numero)
                    alterado = true
                } else {
                    _mensagem.value =
                            getApplication<Application>()
                                    .getString(R.string.erro_numero_excluido_nao_pode_ser_fixo)
                    alterado = false
                }
            } else {
                _mensagem.value =
                        getApplication<Application>()
                                .getString(R.string.erro_maximo_numeros_fixos_atingido)
                alterado = false
            }
        }
        // Atualiza o StateFlow local e persiste a alteração
        _numerosFixosState.value = numerosFixosInterno.toList()
        preferenciasViewModel.salvarNumerosFixos(numerosFixosInterno.toList())

        return alterado // Retorna true se adicionado, false se removido ou erro
    }

    /**
     * Adiciona ou remove um número da lista de números excluídos.
     * @param numero O número a ser adicionado ou removido.
     * @return Verdadeiro se o número foi adicionado, falso se foi removido.
     */
    fun toggleNumeroExcluido(numero: Int): Boolean {
        val alterado: Boolean
        if (numero in numerosExcluidosInterno) {
            numerosExcluidosInterno.remove(numero)
            alterado = false
        } else {
            if (25 - numerosExcluidosInterno.size > quantidadeNumeros
            ) { // Limita o número de excluídos
                if (numero !in numerosFixosInterno) { // Não pode ser excluído se já for fixo
                    numerosExcluidosInterno.add(numero)
                    alterado = true
                } else {
                    _mensagem.value =
                            getApplication<Application>()
                                    .getString(R.string.erro_numero_fixo_nao_pode_ser_excluido)
                    alterado = false
                }
            } else {
                _mensagem.value =
                        getApplication<Application>()
                                .getString(R.string.erro_maximo_numeros_excluidos_atingido)
                alterado = false
            }
        }
        if (alterado || !numerosExcluidosInterno.contains(numero)
        ) { // Atualiza o StateFlow se houve adição bem-sucedida ou remoção
            _numerosExcluidosState.value = numerosExcluidosInterno.toList()
        }
        return alterado // Retorna true se adicionado, false se removido ou erro
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
        if (quantidadeJogos <= 0)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_jogos_invalida)
                )
        if (quantidadeNumeros !in 15..20)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_numeros_invalida)
                )
        if (numerosFixosInterno.size >= quantidadeNumeros)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_numeros_fixos_invalida)
                )
        if (25 - numerosExcluidosInterno.size < quantidadeNumeros)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_numeros_excluidos_invalida)
                )
        val sobreposicao = verificarSobreposicao()
        if (sobreposicao.isNotEmpty())
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(
                                        R.string.erro_numeros_sobreposicao,
                                        sobreposicao.sorted().joinToString(", ")
                                )
                )
        return Pair(true, null)
    }

    /**
     * Inicia o processo de geração de jogos.
     * Define o [operacaoStatus] como [OperacaoStatus.CARREGANDO] e, após a conclusão,
     * atualiza para [OperacaoStatus.SUCESSO] ou [OperacaoStatus.ERRO].
     * Os jogos gerados são postados em [jogosGerados].
     * Mensagens de feedback são postadas em [mensagem].
     */
    /** Gera jogos com base nas configurações atuais. */
    fun gerarJogos() {
        _operacaoStatus.value = OperacaoStatus.CARREGANDO
        viewModelScope.launch {
            try {
                val configFiltros: ConfiguracaoFiltros = filtrosViewModel.configuracaoFiltros.first() // Pega o valor atual

                // Mapear ConfiguracaoFiltros para GeradorJogos.ConfiguracaoGeracao
                val configGeracao = GeradorJogos.ConfiguracaoGeracao(
                    quantidadeJogos = configFiltros.quantidadeJogos,
                    quantidadeNumerosPorJogo = configFiltros.quantidadeNumerosPorJogo,
                    numerosFixos = configFiltros.numerosFixos,
                    numerosExcluidos = configFiltros.numerosExcluidos,
                    filtroParesImpares = if (configFiltros.filtroParesImpares) GeradorJogos.FiltroRange(configFiltros.minImpares, configFiltros.maxImpares) else null,
                    filtroSomaTotal = if (configFiltros.filtroSomaTotal) GeradorJogos.FiltroRange(configFiltros.minSoma, configFiltros.maxSoma) else null,
                    filtroPrimos = if (configFiltros.filtroPrimos) GeradorJogos.FiltroRange(configFiltros.minPrimos, configFiltros.maxPrimos) else null,
                    filtroFibonacci = if (configFiltros.filtroFibonacci) GeradorJogos.FiltroRange(configFiltros.minFibonacci, configFiltros.maxFibonacci) else null,
                    filtroMiolo = if (configFiltros.filtroMioloMoldura) GeradorJogos.FiltroRange(configFiltros.minMiolo, configFiltros.maxMiolo) else null,
                    filtroMultiplosDeTres = if (configFiltros.filtroMultiplosDeTres) GeradorJogos.FiltroRange(configFiltros.minMultiplos, configFiltros.maxMultiplos) else null,
                    filtroRepeticaoAnterior = if (configFiltros.filtroRepeticaoConcursoAnterior && configFiltros.dezenasConcursoAnterior.isNotEmpty()) {
                        GeradorJogos.FiltroRepeticao(
                            dezenasAnteriores = configFiltros.dezenasConcursoAnterior,
                            range = GeradorJogos.FiltroRange(configFiltros.minRepeticaoConcursoAnterior, configFiltros.maxRepeticaoConcursoAnterior)
                        )
                    } else null,
                    ultimoResultadoConcursoAnterior = configFiltros.dezenasConcursoAnterior // Adicionado para consistência
                )

                val jogos = withContext(Dispatchers.Default) {
                    GeradorJogos.gerarJogos(configGeracao)
                }

                if (jogos.isNotEmpty()) {
                    _jogosGerados.postValue(jogos)
                    _operacaoStatus.postValue(OperacaoStatus.SUCESSO)
                } else {
                    _jogosGerados.postValue(emptyList())
                    _operacaoStatus.postValue(OperacaoStatus.SUCESSO) // Sucesso, mas sem jogos
                    _mensagem.postValue(getApplication<Application>().getString(R.string.info_nenhum_jogo_gerado_filtros))
                }
            } catch (e: IllegalArgumentException) {
                _jogosGerados.postValue(emptyList())
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
                _mensagem.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_gerar_jogos_parametros_invalidos))
            } catch (e: Exception) {
                _jogosGerados.postValue(emptyList())
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
                _mensagem.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_desconhecido_geracao))
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

    fun setFiltroParesImparesAtivado(ativado: Boolean) {
        _filtroParesImparesAtivado.value = ativado
        if (!ativado) {
            _minParesInput.value = "7" // Padrão para ímpares
            _maxParesInput.value = "9" // Padrão para ímpares
            minPares = null
            maxPares = null
        } else {
            if (_minParesInput.value.isBlank()) _minParesInput.value = "7"
            if (_maxParesInput.value.isBlank()) _maxParesInput.value = "9"
        }
    }

    fun setMinParesInput(valor: String) {
        _minParesInput.value = valor
        // A conversão para Int e validação ocorrem em validarEParsearInputs()
        // Se o filtro for ativado e o input estiver em branco, ele será resetado para o padrão "7"
    }

    fun setMaxParesInput(valor: String) {
        _maxParesInput.value = valor
    }

    fun setFiltroSomaTotalAtivado(ativado: Boolean) {
        _filtroSomaTotalAtivado.value = ativado
        if (!ativado) {
            _minSomaInput.value = ""
            minSoma = null
            _maxSomaInput.value = ""
            maxSoma = null
        }
    }

    fun setMinSomaInput(valor: String) {
        _minSomaInput.value = valor
        minSoma = valor.toIntOrNull()
    }

    fun setMaxSomaInput(valor: String) {
        _maxSomaInput.value = valor
        maxSoma = valor.toIntOrNull()
    }

    fun setFiltroPrimosAtivado(ativado: Boolean) {
        _filtroPrimosAtivado.value = ativado
        if (!ativado) {
            _minPrimosInput.value = ""
            minPrimos = null
            _maxPrimosInput.value = ""
            maxPrimos = null
        }
    }

    fun setMinPrimosInput(valor: String) {
        _minPrimosInput.value = valor
        minPrimos = valor.toIntOrNull()
    }

    fun setMaxPrimosInput(valor: String) {
        _maxPrimosInput.value = valor
        maxPrimos = valor.toIntOrNull()
    }

    fun setFiltroFibonacciAtivado(ativado: Boolean) {
        _filtroFibonacciAtivado.value = ativado
        if (!ativado) {
            _minFibonacciInput.value = ""
            minFibonacci = null
            _maxFibonacciInput.value = ""
            maxFibonacci = null
        }
    }

    fun setMinFibonacciInput(valor: String) {
        _minFibonacciInput.value = valor
        minFibonacci = valor.toIntOrNull()
    }

    fun setMaxFibonacciInput(valor: String) {
        _maxFibonacciInput.value = valor
        maxFibonacci = valor.toIntOrNull()
    }

    fun setFiltroMioloMolduraAtivado(ativado: Boolean) {
        _filtroMioloMolduraAtivado.value = ativado
        if (!ativado) {
            _minMioloInput.value = ""
            minMiolo = null
            _maxMioloInput.value = ""
            maxMiolo = null
        }
    }

    fun setMinMioloInput(valor: String) {
        _minMioloInput.value = valor
        minMiolo = valor.toIntOrNull()
    }

    fun setMaxMioloInput(valor: String) {
        _maxMioloInput.value = valor
        maxMiolo = valor.toIntOrNull()
    }

    fun setFiltroMultiplosDeTresAtivado(ativado: Boolean) {
        _filtroMultiplosDeTresAtivado.value = ativado
        if (!ativado) {
            _minMultiplosDeTresInput.value = ""
            minMultiplosTres = null
            _maxMultiplosDeTresInput.value = ""
            maxMultiplosTres = null
        }
    }

    fun setMinMultiplosDeTresInput(valor: String) {
        _minMultiplosDeTresInput.value = valor
        minMultiplosTres = valor.toIntOrNull()
    }

    fun setMaxMultiplosDeTresInput(valor: String) {
        _maxMultiplosDeTresInput.value = valor
    }

    fun setQuantidadeJogosInput(valor: String) {
        _quantidadeJogosInput.value = valor
        valor.toIntOrNull()?.let {
            if (it > 0) { // Adicionar validação básica
                quantidadeJogos = it
            } else {
                // Poderia definir uma mensagem de erro se quisesse feedback imediato
                // ou deixar a validação principal em validarConfiguracoes() tratar
                // Por enquanto, se for inválido (ex: "0" ou texto), quantidadeJogos não muda ou usa
                // o último válido
                // Para garantir que quantidadeJogos reflita o input válido ou um padrão:
                // quantidadeJogos = valor.toIntOrNull()?.takeIf { num -> num > 0 } ?: 1 // Exemplo:
                // Padrão 1 se inválido
            }
        }
    }

    fun setQuantidadeNumerosInput(valor: String) {
        _quantidadeNumerosInput.value = valor
        valor.toIntOrNull()?.let {
            // Validar se está no intervalo permitido (ex: 15-20, conforme Lotofácil)
            if (it in 15..20) {
                quantidadeNumeros = it
            } else {
                // Tratar valor inválido - pode mostrar mensagem ou reverter para valor válido
                // Por enquanto, apenas não atualiza se fora do range aceitável
                // ou poderia forçar um padrão, ex: _quantidadeNumerosInput.value =
                // quantidadeNumeros.toString()
            }
        }
    }

    /** Define a quantidade de jogos diretamente (para uso interno ou por outros ViewModels). */
    fun setQuantidadeJogosInterno(novaQuantidade: Int) {
        if (novaQuantidade > 0) {
            quantidadeJogos = novaQuantidade
            _quantidadeJogosInput.value = novaQuantidade.toString()
        }
    }

    /**
     * Define a quantidade de números por jogo diretamente (para uso interno ou por outros
     * ViewModels).
     */
    fun setQuantidadeNumerosInterno(novaQuantidade: Int) {
        if (novaQuantidade in 15..20) {
            quantidadeNumeros = novaQuantidade
            _quantidadeNumerosInput.value = novaQuantidade.toString()
        }
    }

    /** Salva a lista de jogos atualmente em [jogosGerados] no repositório. */
    fun salvarJogosGerados() {
        viewModelScope.launch {
            val jogosParaSalvar = _jogosGerados.value // Pode ser nulo
            if (!jogosParaSalvar.isNullOrEmpty()) { // Checagem de nulo e vazio
                try {
                    // jogoRepository é nullable, precisamos tratar isso também
                    jogoRepository?.inserirJogos(jogosParaSalvar) // Usar chamada segura e garantir que jogosParaSalvar não é nulo aqui
                    _operacaoStatus.value = OperacaoStatus.SUCESSO 
                    _mensagem.value = getApplication<Application>().getString(R.string.jogos_salvos_com_sucesso)
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                    _mensagem.value = getApplication<Application>().getString(R.string.erro_salvar_jogos) + ": " + e.message
                }
            } else {
                _mensagem.value = getApplication<Application>().getString(R.string.nenhum_jogo_para_salvar)
            }
        }
    }

    // TODO: Adicionar funções para atualizar outros filtros

    private var mainViewModelRef: MainViewModel? = null
    fun setMainViewModelRef(mainViewModel: MainViewModel) {
        mainViewModelRef = mainViewModel
    }

    private fun validarEParsearInputs(): Boolean {
        // Parse e validação da quantidade de jogos
        val qtdJogos = _quantidadeJogosInput.value.toIntOrNull()
        if (qtdJogos == null || qtdJogos <= 0) {
            _mensagem.value = getApplication<Application>().getString(R.string.erro_quantidade_jogos_invalida)
            return false
        }
        quantidadeJogos = qtdJogos

        // Parse e validação da quantidade de números por jogo (fixo em 15 para Lotofácil, mas pode ser configurável no futuro)
        val qtdNumeros = _quantidadeNumerosInput.value.toIntOrNull()
        if (qtdNumeros == null || qtdNumeros != 15) { // Para Lotofácil, sempre 15
            _mensagem.value = getApplication<Application>().getString(R.string.erro_quantidade_numeros_invalida) // Reutilizar ou criar string específica
            return false
        }
        quantidadeNumeros = qtdNumeros

        // Filtro Pares/Ímpares (interpretado como ÍMPARES)
        if (_filtroParesImparesAtivado.value) {
            val minInputStr = _minParesInput.value
            val maxInputStr = _maxParesInput.value
            minPares = minInputStr.toIntOrNull() // Será minÍMPARES
            maxPares = maxInputStr.toIntOrNull() // Será maxÍMPARES

            if (minPares == null || maxPares == null || minPares!! < 0 || maxPares!! > 15 || minPares!! > maxPares!!) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_pares_impares_invalido)
                return false
            }
        } else {
            minPares = null
            maxPares = null
        }

        // Filtro Soma Total
        if (_filtroSomaTotalAtivado.value) {
            val minInputStr = _minSomaInput.value
            val maxInputStr = _maxSomaInput.value
            minSoma = minInputStr.toIntOrNull()
            maxSoma = maxInputStr.toIntOrNull()

            val minSomaPossivel = (1..15).sum() // Soma dos 15 menores números (1 a 15)
            val maxSomaPossivel = (11..25).sum() // Soma dos 15 maiores números (11 a 25)
            if (minSoma == null || maxSoma == null || minSoma!! < minSomaPossivel || maxSoma!! > maxSomaPossivel || minSoma!! > maxSoma!!) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_soma_total_invalido)
                return false
            }
        } else {
            minSoma = null
            maxSoma = null
        }

        // Filtro Números Primos (2, 3, 5, 7, 11, 13, 17, 19, 23 - são 9 primos)
        if (_filtroPrimosAtivado.value) {
            val minInputStr = _minPrimosInput.value
            val maxInputStr = _maxPrimosInput.value
            minPrimos = minInputStr.toIntOrNull()
            maxPrimos = maxInputStr.toIntOrNull()

            if (minPrimos == null || maxPrimos == null || minPrimos!! < 0 || maxPrimos!! > 9 || minPrimos!! > maxPrimos!!) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_primos_invalido)
                return false
            }
        } else {
            minPrimos = null
            maxPrimos = null
        }

        // Filtro Números de Fibonacci (1, 2, 3, 5, 8, 13, 21 - são 7 de Fibonacci até 25)
        if (_filtroFibonacciAtivado.value) {
            val minInputStr = _minFibonacciInput.value
            val maxInputStr = _maxFibonacciInput.value
            minFibonacci = minInputStr.toIntOrNull()
            maxFibonacci = maxInputStr.toIntOrNull()

            if (minFibonacci == null || maxFibonacci == null || minFibonacci!! < 0 || maxFibonacci!! > 7 || minFibonacci!! > maxFibonacci!!) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_fibonacci_invalido)
                return false
            }
        } else {
            minFibonacci = null
            maxFibonacci = null
        }

        // Filtro Miolo (7, 8, 9, 12, 13, 14, 17, 18, 19 - são 9 dezenas no miolo)
        // A UI pede Min/Max Miolo.
        if (_filtroMioloMolduraAtivado.value) {
            val minInputStr = _minMioloInput.value
            val maxInputStr = _maxMioloInput.value
            minMiolo = minInputStr.toIntOrNull()
            maxMiolo = maxInputStr.toIntOrNull()

            if (minMiolo == null || maxMiolo == null || minMiolo!! < 0 || maxMiolo!! > 9 || minMiolo!! > maxMiolo!!) { // Max de 9 dezenas no miolo
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_miolo_invalido)
                return false
            }
        } else {
            minMiolo = null
            maxMiolo = null
        }

        // Filtro Múltiplos de 3 (3, 6, 9, 12, 15, 18, 21, 24 - são 8 múltiplos de 3)
        if (_filtroMultiplosDeTresAtivado.value) {
            val minInputStr = _minMultiplosDeTresInput.value
            val maxInputStr = _maxMultiplosDeTresInput.value
            minMultiplosTres = minInputStr.toIntOrNull()
            maxMultiplosTres = maxInputStr.toIntOrNull()

            if (minMultiplosTres == null || maxMultiplosTres == null || minMultiplosTres!! < 0 || maxMultiplosTres!! > 8 || minMultiplosTres!! > maxMultiplosTres!!) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_multiplos_tres_invalido)
                return false
            }
        } else {
            minMultiplosTres = null
            maxMultiplosTres = null
        }

        // Filtro Repetição de Dezenas
        if (_filtroRepeticaoDezenasAtivado.value) {
            val minInputStr = _minRepeticaoInput.value
            val maxInputStr = _maxRepeticaoInput.value
            minRepeticao = minInputStr.toIntOrNull()
            maxRepeticao = maxInputStr.toIntOrNull()

            if (minRepeticao == null || maxRepeticao == null || minRepeticao!! < 0 || maxRepeticao!! > 15 || minRepeticao!! > maxRepeticao!!) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_repeticao_invalido)
                return false
            }
            // Verifica se o último resultado está disponível se o filtro de repetição estiver ativo
            if (mainViewModelRef?.ultimoResultado?.value?.numeros?.isEmpty() != false) {
                 _mensagem.value = getApplication<Application>().getString(R.string.erro_ultimo_resultado_nao_salvo_para_filtro_repeticao)
                 return false
            }
        } else {
            minRepeticao = null
            maxRepeticao = null
        }

        return true
    }

    fun setFiltroRepeticaoDezenasAtivado(ativado: Boolean) {
        _filtroRepeticaoDezenasAtivado.value = ativado
        if (!ativado) {
            _minRepeticaoInput.value = "8"
            _maxRepeticaoInput.value = "10"
            minRepeticao = null
            maxRepeticao = null
        } else {
            if (_minRepeticaoInput.value.isBlank()) _minRepeticaoInput.value = "8"
            if (_maxRepeticaoInput.value.isBlank()) _maxRepeticaoInput.value = "10"
        }
    }

    fun setMinRepeticaoInput(valor: String) {
        _minRepeticaoInput.value = valor
    }

    fun setMaxRepeticaoInput(valor: String) {
        _maxRepeticaoInput.value = valor
    }
}
