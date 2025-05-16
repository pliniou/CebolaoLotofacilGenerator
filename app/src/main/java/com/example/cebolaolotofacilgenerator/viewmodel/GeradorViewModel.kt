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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asFlow

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
     * Bloco de inicialização.
     */
    init {
        // Lógica de inicialização, se necessária, como carregar dezenas fixas/excluídas persistidas
        // já é tratada em inicializarComNumerosFixos ou via preferenciasViewModel
    }

    // As variáveis de min/max locais (minPares, maxPares, etc.) são removidas.
    // Os valores virão de filtrosViewModel.configuracaoFiltros.value

    // Os StateFlows para ativação de filtros (_filtroParesImparesAtivado, etc.) e
    // para inputs de min/max (_minParesInput, etc.) são REMOVIDOS.
    // A UI observará diretamente filtrosViewModel.configuracaoFiltros.

    private var mainViewModelRef: MainViewModel? = null

    /**
     * Inicializa a lista de números fixos com um conjunto de dezenas. Se dezenas forem fornecidas,
     * elas são usadas e persistidas. Se não, as dezenas persistidas (já carregadas no init) são
     * mantidas.
     */
    fun inicializarComNumerosFixos(dezenas: List<Int>?) {
        if (dezenas != null) { // Apenas processa se dezenas não for null
            numerosFixosInterno.clear() // Limpa a lista atual de números fixos
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
            _numerosFixosState.value = numerosFixosInterno.toList() // Atualiza o StateFlow
            // Persiste as novas dezenas fixas
            preferenciasViewModel.salvarNumerosFixos(numerosFixosInterno.toList())
        }
        // Se dezenas for null, significa que não vieram argumentos de navegação,
        // então as dezenas carregadas do DataStore no init são mantidas.
    }

    /** Reseta todas as configurações para os valores padrão. */
    fun resetarConfiguracoes() {
        viewModelScope.launch {
            // Reseta a configuração de filtros no FiltrosViewModel para os valores padrão.
            filtrosViewModel.atualizarFiltro(ConfiguracaoFiltros()) // Isso já define um valor não nulo no LiveData
        }

        // Reseta os estados locais do GeradorViewModel
        numerosFixosInterno.clear()
        _numerosFixosState.value = emptyList()
        preferenciasViewModel.salvarNumerosFixos(emptyList()) // Também reseta a persistência

        numerosExcluidosInterno.clear()
        _numerosExcluidosState.value = emptyList()
        // Não há persistência para números excluídos no PreferenciasViewModel atualmente.

        _jogosGerados.value = emptyList()
        _operacaoStatus.value = OperacaoStatus.OCIOSO
        _mensagem.value = null

        // Não é mais necessário resetar StateFlows de filtros locais,
        // pois eles foram removidos. O reset de filtrosViewModel.atualizarFiltro() acima
        // já cuida disso para os filtros estatísticos.
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
            if (numerosFixosInterno.size < (filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()).quantidadeNumerosPorJogo - 1) { // Limita o número de fixos
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
            if (25 - numerosExcluidosInterno.size > (filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()).quantidadeNumerosPorJogo
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
        val currentConfig = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        if (currentConfig.quantidadeJogos <= 0)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_jogos_invalida)
                )
        if (currentConfig.quantidadeNumerosPorJogo !in 15..20)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_numeros_invalida)
                )
        if (numerosFixosInterno.size >= currentConfig.quantidadeNumerosPorJogo)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_numeros_fixos_invalida)
                )
        if (25 - numerosExcluidosInterno.size < currentConfig.quantidadeNumerosPorJogo)
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
     * Gera jogos com base na configuração atual de filtros e dezenas.
     * Atualiza [_jogosGerados] e [_operacaoStatus].
     */
    fun gerarJogosComConfiguracaoAtual() {
        _operacaoStatus.value = OperacaoStatus.CARREGANDO
        viewModelScope.launch {
            val currentConfigFiltros = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
            // Validar inputs antes de prosseguir
            if (!validarEParsearInputs(currentConfigFiltros)) { // Passa a configuração atual
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
                // A mensagem de erro já foi setada por validarEParsearInputs
                return@launch
            }

            try {
                // val configFiltros = filtrosViewModel.configuracaoFiltros.value // Já obtido como currentConfigFiltros
                val ultimoResultadoDezenas = mainViewModelRef?.ultimoResultado?.value?.numeros
                val dezenasFixasAtuais = numerosFixosState.value
                val dezenasExcluidasAtuais = numerosExcluidosState.value

                // Mapear ConfiguracaoFiltros para ConfiguracaoGeracao
                val configuracaoGerador = ConfiguracaoGeracao(
                    quantidadeJogos = currentConfigFiltros.quantidadeJogos,
                    quantidadeNumerosPorJogo = currentConfigFiltros.quantidadeNumerosPorJogo,
                    numerosFixos = dezenasFixasAtuais,
                    numerosExcluidos = dezenasExcluidasAtuais,
                    filtroParesImpares = if (currentConfigFiltros.filtroParesImpares) FiltroRange(currentConfigFiltros.minImpares, currentConfigFiltros.maxImpares) else null,
                    filtroSomaTotal = if (currentConfigFiltros.filtroSomaTotal) FiltroRange(currentConfigFiltros.minSoma, currentConfigFiltros.maxSoma) else null,
                    filtroPrimos = if (currentConfigFiltros.filtroPrimos) FiltroRange(currentConfigFiltros.minPrimos, currentConfigFiltros.maxPrimos) else null,
                    filtroFibonacci = if (currentConfigFiltros.filtroFibonacci) FiltroRange(currentConfigFiltros.minFibonacci, currentConfigFiltros.maxFibonacci) else null,
                    filtroMiolo = if (currentConfigFiltros.filtroMioloMoldura) FiltroRange(currentConfigFiltros.minMiolo, currentConfigFiltros.maxMiolo) else null,
                    filtroMultiplosDeTres = if (currentConfigFiltros.filtroMultiplosDeTres) FiltroRange(currentConfigFiltros.minMultiplos, currentConfigFiltros.maxMultiplos) else null,
                    filtroRepeticaoAnterior = if (currentConfigFiltros.filtroRepeticaoConcursoAnterior && ultimoResultadoDezenas != null && ultimoResultadoDezenas.isNotEmpty()) { // Verificar se ultimoResultadoDezenas não é nulo nem vazio
                        FiltroRepeticao(
                            dezenasAnteriores = ultimoResultadoDezenas,
                            range = FiltroRange(currentConfigFiltros.minRepeticaoConcursoAnterior, currentConfigFiltros.maxRepeticaoConcursoAnterior)
                        )
                    } else null,
                    ultimoResultadoConcursoAnterior = ultimoResultadoDezenas
                )

                val jogosGeradosListCompletos = withContext(Dispatchers.Default) {
                    // Corrigir nome do método e passar o objeto de configuração correto
                    val listasDeNumerosGeradas = GeradorJogos.gerarJogos(configuracaoGerador)
                    // O método gerarJogos já retorna List<Jogo>, não List<List<Int>>
                    listasDeNumerosGeradas // Já é List<Jogo>
                }

                if (jogosGeradosListCompletos.isNotEmpty()) {
                    _jogosGerados.postValue(jogosGeradosListCompletos)
                    _operacaoStatus.postValue(OperacaoStatus.SUCESSO)
                } else {
                    _jogosGerados.postValue(emptyList())
                    _operacaoStatus.postValue(OperacaoStatus.SUCESSO) // Sucesso, mas nenhum jogo gerado
                    _mensagem.postValue(getApplication<Application>().getString(R.string.info_nenhum_jogo_gerado_filtros))
                }

            } catch (e: Exception) {
                _jogosGerados.postValue(emptyList())
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
                _mensagem.postValue("Erro ao gerar jogos: ${e.message}")
                // Log.e("GeradorViewModel", "Erro ao gerar jogos", e)
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
    fun atualizarFiltroParesImpares(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroParesImpares = ativado,
                minImpares = min ?: configAtual.minImpares, // Mantém se não fornecido
                maxImpares = max ?: configAtual.maxImpares  // Mantém se não fornecido
            )
        )
    }

    fun atualizarFiltroSomaTotal(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroSomaTotal = ativado,
                minSoma = min ?: configAtual.minSoma,
                maxSoma = max ?: configAtual.maxSoma
            )
        )
    }

    fun atualizarFiltroPrimos(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroPrimos = ativado,
                minPrimos = min ?: configAtual.minPrimos,
                maxPrimos = max ?: configAtual.maxPrimos
            )
        )
    }

    fun atualizarFiltroFibonacci(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroFibonacci = ativado,
                minFibonacci = min ?: configAtual.minFibonacci,
                maxFibonacci = max ?: configAtual.maxFibonacci
            )
        )
    }

    fun atualizarFiltroMioloMoldura(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroMioloMoldura = ativado,
                minMiolo = min ?: configAtual.minMiolo,
                maxMiolo = max ?: configAtual.maxMiolo
            )
        )
    }

    fun atualizarFiltroMultiplosDeTres(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroMultiplosDeTres = ativado,
                minMultiplos = min ?: configAtual.minMultiplos,
                maxMultiplos = max ?: configAtual.maxMultiplos
            )
        )
    }

    fun atualizarFiltroRepeticaoDezenas(ativado: Boolean, min: Int? = null, max: Int? = null) {
        val configAtual = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros()
        filtrosViewModel.atualizarFiltro(
            configAtual.copy(
                filtroRepeticaoConcursoAnterior = ativado,
                minRepeticaoConcursoAnterior = min ?: configAtual.minRepeticaoConcursoAnterior,
                maxRepeticaoConcursoAnterior = max ?: configAtual.maxRepeticaoConcursoAnterior
            )
        )
        // Se o filtro de repetição for ativado, verificar se o último resultado está disponível
        if (ativado && (mainViewModelRef?.ultimoResultado?.value?.numeros.isNullOrEmpty())) {
            _mensagem.value = getApplication<Application>().getString(R.string.erro_ultimo_resultado_nao_salvo_para_filtro_repeticao)
            // Considerar se a ativação deve ser revertida ou apenas mostrar a mensagem.
            // Por ora, apenas mostra a mensagem; a validação final ocorre em validarEParsearInputs.
        }
    }

    fun setMainViewModelRef(mainViewModel: MainViewModel) {
        this.mainViewModelRef = mainViewModel
    }

    private fun validarEParsearInputs(configFiltros: ConfiguracaoFiltros): Boolean { // Recebe ConfiguracaoFiltros
        // Validação da quantidade de jogos e números por jogo (já parte de configFiltros)
        if (configFiltros.quantidadeJogos <= 0) {
            _mensagem.value = getApplication<Application>().getString(R.string.erro_quantidade_jogos_invalida)
            return false
        }
        if (configFiltros.quantidadeNumerosPorJogo !in 15..20) {
            _mensagem.value = getApplication<Application>().getString(R.string.erro_quantidade_numeros_invalida)
            return false
        }

        // Validações de dezenas fixas/excluídas (usam estado local do GeradorViewModel)
        if (numerosFixosState.value.size >= configFiltros.quantidadeNumerosPorJogo) {
             _mensagem.value = getApplication<Application>().getString(R.string.erro_quantidade_numeros_fixos_invalida)
             return false
        }
        if (25 - numerosExcluidosState.value.size < configFiltros.quantidadeNumerosPorJogo) {
             _mensagem.value = getApplication<Application>().getString(R.string.erro_quantidade_numeros_excluidos_invalida)
             return false
        }
        val sobreposicao = numerosFixosState.value.filter { it in numerosExcluidosState.value }
        if (sobreposicao.isNotEmpty()) {
             _mensagem.value = getApplication<Application>().getString(R.string.erro_numeros_sobreposicao, sobreposicao.sorted().joinToString(", "))
             return false
        }


        // Filtro Pares/Ímpares (usa valores de configFiltros)
        if (configFiltros.filtroParesImpares) {
            val minImpares = configFiltros.minImpares
            val maxImpares = configFiltros.maxImpares
            if (minImpares < 0 || maxImpares > 15 || minImpares > maxImpares) { // Max de 15 ímpares em um jogo de 15
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_pares_impares_invalido)
                return false
            }
        }

        // Filtro Soma Total
        if (configFiltros.filtroSomaTotal) {
            val minSoma = configFiltros.minSoma
            val maxSoma = configFiltros.maxSoma
            // TODO: Os valores minSomaPossivel e maxSomaPossivel devem ser ajustados se a quantidade de números por jogo não for 15.
            // Por ora, assumindo 15 dezenas.
            val minSomaPossivel = (1..15).sumOf { it } // Soma dos 15 menores números (1 a 15) -> 120
            val maxSomaPossivel = (11..25).sumOf { it } // Soma dos 15 maiores números (11 a 25) -> 270
            if (minSoma < minSomaPossivel || maxSoma > maxSomaPossivel || minSoma > maxSoma) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_soma_total_invalido)
                return false
            }
        }

        // Filtro Números Primos (2, 3, 5, 7, 11, 13, 17, 19, 23 - são 9 primos até 25)
        if (configFiltros.filtroPrimos) {
            val minPrimos = configFiltros.minPrimos
            val maxPrimos = configFiltros.maxPrimos
            if (minPrimos < 0 || maxPrimos > 9 || minPrimos > maxPrimos) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_primos_invalido)
                return false
            }
        }

        // Filtro Números de Fibonacci (1, 2, 3, 5, 8, 13, 21 - são 7 de Fibonacci até 25)
        if (configFiltros.filtroFibonacci) {
            val minFibonacci = configFiltros.minFibonacci
            val maxFibonacci = configFiltros.maxFibonacci
            if (minFibonacci < 0 || maxFibonacci > 7 || minFibonacci > maxFibonacci) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_fibonacci_invalido)
                return false
            }
        }

        // Filtro Miolo (7, 8, 9, 12, 13, 14, 17, 18, 19 - são 9 dezenas no miolo)
        if (configFiltros.filtroMioloMoldura) {
            val minMiolo = configFiltros.minMiolo
            val maxMiolo = configFiltros.maxMiolo
            if (minMiolo < 0 || maxMiolo > 9 || minMiolo > maxMiolo) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_miolo_invalido)
                return false
            }
        }

        // Filtro Múltiplos de 3 (3, 6, 9, 12, 15, 18, 21, 24 - são 8 múltiplos de 3 até 25)
        if (configFiltros.filtroMultiplosDeTres) {
            val minMultiplos = configFiltros.minMultiplos
            val maxMultiplos = configFiltros.maxMultiplos
            if (minMultiplos < 0 || maxMultiplos > 8 || minMultiplos > maxMultiplos) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_multiplos_tres_invalido)
                return false
            }
        }

        // Filtro Repetição de Dezenas
        if (configFiltros.filtroRepeticaoConcursoAnterior) {
            val minRepeticao = configFiltros.minRepeticaoConcursoAnterior
            val maxRepeticao = configFiltros.maxRepeticaoConcursoAnterior
            if (minRepeticao < 0 || maxRepeticao > 15 || minRepeticao > maxRepeticao) { // Max de 15 dezenas podem se repetir
                _mensagem.value = getApplication<Application>().getString(R.string.erro_filtro_repeticao_invalido)
                return false
            }
            // Verifica se o último resultado está disponível
            val ultimoResultadoDezenas = mainViewModelRef?.ultimoResultado?.value?.numeros
            if (ultimoResultadoDezenas.isNullOrEmpty() && configFiltros.dezenasConcursoAnterior.isEmpty()) {
                 _mensagem.value = getApplication<Application>().getString(R.string.erro_ultimo_resultado_nao_salvo_para_filtro_repeticao)
                 return false
            }
        }
        _mensagem.value = null // Limpa mensagens de erro anteriores se tudo for válido
        return true
    }

    /** Salva a lista de jogos atualmente em [jogosGerados] no repositório. */
    fun salvarJogosGerados() {
        viewModelScope.launch {
            val jogosParaSalvar = _jogosGerados.value // Pode ser nulo
            val repo = jogoRepository
            // val configFiltros = filtrosViewModel.configuracaoFiltros.value ?: ConfiguracaoFiltros() // Já é obtido e validado em gerarJogosComConfiguracaoAtual e usado na UI

            if (repo == null) {
                _mensagem.value = getApplication<Application>().getString(R.string.erro_repositorio_indisponivel) // String corrigida
                return@launch
            }
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
}