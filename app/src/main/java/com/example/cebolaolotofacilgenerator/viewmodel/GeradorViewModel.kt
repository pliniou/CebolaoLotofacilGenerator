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

/** ViewModel para o gerador de jogos da Lotofácil com filtros estatísticos. */
class GeradorViewModel(application: Application) : AndroidViewModel(application) {

    // Estado da operação de geração
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>()
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    // Jogos gerados
    private val _jogosGerados = MutableLiveData<List<Jogo>>()
    val jogosGerados: LiveData<List<Jogo>> = _jogosGerados

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
            numerosFixos.add(numero)
            true
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
            numerosExcluidos.add(numero)
            true
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
        // Verificar quantidade de jogos
        if (quantidadeJogos <= 0) {
            return Pair(false, appContext.getString(R.string.erro_quantidade_jogos_invalida))
        }

        // Verificar quantidade de números por jogo
        if (quantidadeNumeros !in 15..20) {
            return Pair(false, appContext.getString(R.string.erro_quantidade_numeros_invalida))
        }

        // Verificar se há números fixos demais
        if (numerosFixos.size > quantidadeNumeros) {
            return Pair(
                    false,
                    appContext.getString(R.string.erro_quantidade_numeros_fixos_invalida)
            )
        }

        // Verificar se há números excluídos demais
        if (25 - numerosExcluidos.size < quantidadeNumeros) {
            return Pair(
                    false,
                    appContext.getString(R.string.erro_quantidade_numeros_excluidos_invalida)
            )
        }

        // Verificar sobreposição entre fixos e excluídos
        val sobreposicao = verificarSobreposicao()
        if (sobreposicao.isNotEmpty()) {
            return Pair(
                    false,
                    appContext.getString(
                            R.string.erro_numeros_sobreposicao,
                            sobreposicao.sorted().joinToString(", ")
                    )
            )
        }

        return Pair(true, null)
    }

    /** Gera jogos com base nas configurações atuais. */
    fun gerarJogos() =
            viewModelScope.launch {
                try {
                    // Valida as configurações
                    val (valido, _) =
                            validarConfiguracoes() // Usando _ para ignorar o erro não utilizado
                    if (!valido) {
                        _operacaoStatus.value = OperacaoStatus.ERRO
                        return@launch
                    }

                    _operacaoStatus.value = OperacaoStatus.CARREGANDO

                    // Executa a geração em um thread de fundo
                    val jogos =
                            withContext(Dispatchers.Default) {
                                GeradorJogos.gerarJogos(
                                        quantidadeJogos = quantidadeJogos,
                                        quantidadeNumeros = quantidadeNumeros,
                                        numerosFixos = numerosFixos,
                                        numerosExcluidos = numerosExcluidos,
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

                    _jogosGerados.value = jogos
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /** Limpa a lista de jogos gerados. */
    fun limparJogosGerados() {
        _jogosGerados.value = emptyList()
    }

    /** Reseta o status da operação. */
    fun resetarStatus() {
        _operacaoStatus.value = OperacaoStatus.INATIVO
    }
}
