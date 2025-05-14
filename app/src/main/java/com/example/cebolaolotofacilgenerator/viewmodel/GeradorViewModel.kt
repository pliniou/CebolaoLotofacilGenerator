package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.util.GeradorJogos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Definição do Enum OperacaoStatus
enum class OperacaoStatus {
    CARREGANDO,
    SUCESSO,
    ERRO,
    OCIOSO
}

/** ViewModel para o gerador de jogos da Lotofácil com filtros estatísticos. */
class GeradorViewModel(application: Application) : AndroidViewModel(application) {

    // Estado da operação de geração
    private val _operacaoStatus =
            MutableLiveData<OperacaoStatus>(OperacaoStatus.OCIOSO) // Inicializado
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    // Jogos gerados
    private val _jogosGerados = MutableLiveData<List<Jogo>>()
    val jogosGerados: LiveData<List<Jogo>> = _jogosGerados

    // Mensagem para feedback (erros de validação ou sucesso/erro da geração)
    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    // Configurações de geração
    var quantidadeJogos = 10
    var quantidadeNumeros = 15
    var numerosFixos = mutableListOf<Int>()
    var numerosExcluidos = mutableListOf<Int>()

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

    /** Reseta todas as configurações para os valores padrão. */
    fun resetarConfiguracoes() {
        quantidadeJogos = 10
        quantidadeNumeros = 15
        numerosFixos.clear()
        numerosExcluidos.clear()
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
    }

    /**
     * Adiciona ou remove um número da lista de números fixos.
     * @param numero O número a ser adicionado ou removido.
     * @return Verdadeiro se o número foi adicionado, falso se foi removido.
     */
    fun toggleNumeroFixo(numero: Int): Boolean {
        return if (numero in numerosFixos) {
            numerosFixos.remove(numero)
            false
        } else {
            if (numerosFixos.size < quantidadeNumeros - 1) { // Limita o número de fixos
                if (numero !in numerosExcluidos) { // Não pode ser fixo se já estiver excluído
                    numerosFixos.add(numero)
                    true
                } else {
                    _mensagem.value =
                            getApplication<Application>()
                                    .getString(R.string.erro_numero_excluido_nao_pode_ser_fixo)
                    false
                }
            } else {
                _mensagem.value =
                        getApplication<Application>()
                                .getString(R.string.erro_maximo_numeros_fixos_atingido)
                false
            }
        }
    }

    /**
     * Adiciona ou remove um número da lista de números excluídos.
     * @param numero O número a ser adicionado ou removido.
     * @return Verdadeiro se o número foi adicionado, falso se foi removido.
     */
    fun toggleNumeroExcluido(numero: Int): Boolean {
        return if (numero in numerosExcluidos) {
            numerosExcluidos.remove(numero)
            false
        } else {
            if (25 - numerosExcluidos.size > quantidadeNumeros) { // Limita o número de excluídos
                if (numero !in numerosFixos) { // Não pode ser excluído se já for fixo
                    numerosExcluidos.add(numero)
                    true
                } else {
                    _mensagem.value =
                            getApplication<Application>()
                                    .getString(R.string.erro_numero_fixo_nao_pode_ser_excluido)
                    false
                }
            } else {
                _mensagem.value =
                        getApplication<Application>()
                                .getString(R.string.erro_maximo_numeros_excluidos_atingido)
                false
            }
        }
    }

    /**
     * Verifica se há sobreposição entre números fixos e excluídos.
     * @return Lista de números que estão tanto na lista de fixos quanto na de excluídos.
     */
    fun verificarSobreposicao(): List<Int> {
        return numerosFixos.filter { it in numerosExcluidos }
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
        if (numerosFixos.size >= quantidadeNumeros)
                return Pair(
                        false,
                        getApplication<Application>()
                                .getString(R.string.erro_quantidade_numeros_fixos_invalida)
                )
        if (25 - numerosExcluidos.size < quantidadeNumeros)
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
                                            numerosFixos.toList(), // Passa cópia para segurança
                                    numerosExcluidos = numerosExcluidos.toList(), // Passa cópia
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
}
