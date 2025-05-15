package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.util.GeradorJogos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** ViewModel para o gerador de jogos da Lotofácil com filtros estatísticos. */
class GeradorViewModel(
    application: Application,
    private val jogoRepository: com.example.cebolaolotofacilgenerator.data.repository.JogoRepository? = null
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
    private val _quantidadeJogosInput = MutableStateFlow("10") // Valor padrão como String
    val quantidadeJogosInput: StateFlow<String> = _quantidadeJogosInput.asStateFlow()
    var quantidadeJogos = 10 // Mantém o Int para lógica interna
        private set // Para garantir que seja alterado apenas via setQuantidadeJogosInput

    private val _quantidadeNumerosInput = MutableStateFlow("15") // Valor padrão como String
    val quantidadeNumerosInput: StateFlow<String> = _quantidadeNumerosInput.asStateFlow()
    var quantidadeNumeros = 15 // Mantém o Int para lógica interna
        private set

    // Dezenas Fixas e Excluídas com StateFlow para observação na UI
    private val _numerosFixosState = MutableStateFlow<List<Int>>(emptyList())
    val numerosFixosState: StateFlow<List<Int>> = _numerosFixosState.asStateFlow()
    private var numerosFixosInterno = mutableListOf<Int>() // Lista interna para manipulação

    private val _numerosExcluidosState = MutableStateFlow<List<Int>>(emptyList())
    val numerosExcluidosState: StateFlow<List<Int>> = _numerosExcluidosState.asStateFlow()
    private var numerosExcluidosInterno = mutableListOf<Int>() // Lista interna para manipulação

    init {
        viewModelScope.launch {
            // Carrega as dezenas fixas persistidas na inicialização
            // Usar .first() para pegar o valor inicial do StateFlow das preferências
            val dezenasFixasPersistidas = preferenciasViewModel.numerosFixosState.first()
            if (dezenasFixasPersistidas.isNotEmpty()) {
                // Não chama inicializarComNumerosFixos diretamente para evitar salvar novamente
                // a menos que seja uma ação explícita do usuário
                numerosFixosInterno.clear()
                numerosFixosInterno.addAll(dezenasFixasPersistidas)
                _numerosFixosState.value = numerosFixosInterno.toList()
            }
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

    // Filtro: Pares e Ímpares
    private val _filtroParesImparesAtivado = MutableStateFlow(false)
    val filtroParesImparesAtivado: StateFlow<Boolean> = _filtroParesImparesAtivado.asStateFlow()

    private val _minParesInput = MutableStateFlow("") // Para TextField
    val minParesInput: StateFlow<String> = _minParesInput.asStateFlow()

    private val _maxParesInput = MutableStateFlow("") // Para TextField
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
        _jogosGerados.value = emptyList() // Limpa jogos gerados ao resetar config
        _operacaoStatus.value = OperacaoStatus.OCIOSO
        _mensagem.value = null
        _filtroParesImparesAtivado.value = false // Resetar ativação do filtro
        _minParesInput.value = "" // Resetar input
        _maxParesInput.value = "" // Resetar input
        minPares = null // Assegurar que a versão Int também seja resetada
        maxPares = null // Assegurar que a versão Int também seja resetada

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

    /** Gera jogos com base nas configurações atuais. */
    fun gerarJogos() {
        viewModelScope.launch {
            val (valido, msgErroValidacao) = validarConfiguracoes()
            if (!valido) {
                _mensagem.postValue(msgErroValidacao)
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
                _jogosGerados.postValue(emptyList()) // Limpa jogos se a validação falhar
                return@launch
            }

            _operacaoStatus.postValue(OperacaoStatus.CARREGANDO)
            _mensagem.postValue(null) // Limpa mensagem anterior
            try {
                val jogos =
                        withContext(Dispatchers.Default) {
                            GeradorJogos.gerarJogos(
                                    quantidadeJogos = quantidadeJogos,
                                    quantidadeNumeros = quantidadeNumeros,
                                    numerosFixos =
                                            numerosFixosInterno
                                                    .toList(), // Passa cópia para segurança
                                    numerosExcluidos =
                                            numerosExcluidosInterno.toList(), // Passa cópia
                                    minPares = minPares,
                                    maxPares = maxPares,
                                    minPrimos = minPrimos,
                                    maxPrimos = maxPrimos,
                                    minFibonacci = minFibonacci,
                                    maxFibonacci = maxFibonacci,
                                    minMiolo = minMiolo,
                                    maxMiolo = maxMiolo,
                                    minMultiplosTres = minMultiplosTres,
                                    maxMultiplosTres = maxMultiplosTres,
                                    minSoma = minSoma,
                                    maxSoma = maxSoma
                            )
                        }
                _jogosGerados.postValue(jogos)
                if (jogos.isEmpty() && quantidadeJogos > 0) {
                    _mensagem.postValue(
                            getApplication<Application>()
                                    .getString(R.string.info_nenhum_jogo_gerado_filtros)
                    )
                    _operacaoStatus.postValue(
                            OperacaoStatus.OCIOSO
                    ) // Ou ERRO, dependendo da preferência
                } else {
                    _operacaoStatus.postValue(OperacaoStatus.SUCESSO)
                }
            } catch (e: IllegalArgumentException) {
                _jogosGerados.postValue(emptyList())
                _mensagem.postValue(
                        e.message
                                ?: getApplication<Application>()
                                        .getString(R.string.erro_gerar_jogos_parametros_invalidos)
                )
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
            } catch (e: Exception) {
                _jogosGerados.postValue(emptyList())
                _mensagem.postValue(
                        getApplication<Application>()
                                .getString(R.string.erro_desconhecido_geracao) +
                                " ${e.localizedMessage}"
                )
                _operacaoStatus.postValue(OperacaoStatus.ERRO)
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
            // Se desativar o filtro, limpar os valores de min/max pares
            _minParesInput.value = ""
            minPares = null
            _maxParesInput.value = ""
            maxPares = null
        }
    }

    fun setMinParesInput(valor: String) {
        _minParesInput.value = valor
        minPares = valor.toIntOrNull()
        // Adicionar validação se necessário, ex: garantir que não seja negativo
    }

    fun setMaxParesInput(valor: String) {
        _maxParesInput.value = valor
        maxPares = valor.toIntOrNull()
        // Adicionar validação se necessário, ex: maxPares >= minPares
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
        maxMultiplosTres = valor.toIntOrNull()
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

    // TODO: Adicionar funções para atualizar outros filtros
}
