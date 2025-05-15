package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import com.example.cebolaolotofacilgenerator.model.common.OperacaoStatus
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** ViewModel para gerenciar os resultados oficiais da Lotofácil. */
class ResultadoViewModel(application: Application) : AndroidViewModel(application) {

    // Repositório de resultados
    private val repository: ResultadoRepository

    // LiveData para observar todos os resultados
    val todosResultados: LiveData<List<Resultado>>

    // LiveData para observar o último resultado
    val ultimoResultado: LiveData<Resultado>

    // LiveData para o resultado selecionado atualmente
    private val _resultadoSelecionado = MutableLiveData<Resultado?>()
    val resultadoSelecionado: LiveData<Resultado?> = _resultadoSelecionado

    // Estado da operação atual
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>(OperacaoStatus.OCIOSO)
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    init {
        val resultadoDao = AppDatabase.getDatabase(application).resultadoDao()
        repository = ResultadoRepository(resultadoDao)
        todosResultados = repository.todosResultados
        ultimoResultado = repository.ultimoResultado
    }

    /**
     * Insere um novo resultado no banco de dados.
     * @param resultado O resultado a ser inserido.
     */
    fun inserirResultado(resultado: Resultado) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.inserirResultado(resultado)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Insere vários resultados de uma vez no banco de dados.
     * @param resultados A lista de resultados a serem inseridos.
     */
    fun inserirResultados(resultados: List<Resultado>) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.inserirResultados(resultados)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Atualiza um resultado existente no banco de dados.
     * @param resultado O resultado com as informações atualizadas.
     */
    fun atualizarResultado(resultado: Resultado) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.atualizarResultado(resultado)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /** Busca o último resultado cadastrado. */
    fun buscarUltimoResultado() =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    val resultado =
                            withContext(Dispatchers.IO) { repository.obterUltimoResultado() }
                    _resultadoSelecionado.value = resultado
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _resultadoSelecionado.value = null
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Cria um novo resultado com base nos parâmetros fornecidos.
     * @param dataSorteio A data do sorteio.
     * @param numerosSorteadosLista A lista de números sorteados.
     * @param premiacao15 A premiacao do resultado.
     * @param ganhadores15 O número de ganhadores do resultado.
     * @param premiacao14 A premiacao do resultado.
     * @param ganhadores14 O número de ganhadores do resultado.
     * @param premiacao13 A premiacao do resultado.
     * @param ganhadores13 O número de ganhadores do resultado.
     * @param premiacao12 A premiacao do resultado.
     * @param ganhadores12 O número de ganhadores do resultado.
     * @param premiacao11 A premiacao do resultado.
     * @param ganhadores11 O número de ganhadores do resultado.
     */
    fun criarNovoResultado(
            dataSorteio: Date,
            numerosSorteadosLista: List<Int>,
            premiacao15: Double,
            ganhadores15: Int,
            premiacao14: Double,
            ganhadores14: Int,
            premiacao13: Double,
            ganhadores13: Int,
            premiacao12: Double,
            ganhadores12: Int,
            premiacao11: Double,
            ganhadores11: Int
    ): Resultado {
        return Resultado(
                dataSorteio = dataSorteio,
                dezenas = numerosSorteadosLista.sorted(),
                premiacao15Acertos = premiacao15,
                ganhadores15 = ganhadores15,
                premiacao14Acertos = premiacao14,
                ganhadores14 = ganhadores14,
                premiacao13Acertos = premiacao13,
                ganhadores13 = ganhadores13,
                premiacao12Acertos = premiacao12,
                ganhadores12 = ganhadores12,
                premiacao11Acertos = premiacao11,
                ganhadores11 = ganhadores11
        )
    }

    /** Limpa o resultado selecionado atualmente. */
    fun limparResultadoSelecionado() {
        _resultadoSelecionado.value = null
    }

    /**
     * Define o resultado selecionado.
     * @param resultado O resultado a ser selecionado.
     */
    fun selecionarResultado(resultado: Resultado) {
        _resultadoSelecionado.value = resultado
    }

    /** Reseta o status da operação para OCIOSO. */
    fun resetarStatusOperacao() {
        _operacaoStatus.value = OperacaoStatus.OCIOSO
    }

    /** Recarrega todos os resultados do banco de dados. */
    fun carregarResultados() = viewModelScope.launch {
        try {
            _operacaoStatus.value = OperacaoStatus.CARREGANDO
            // O repository já atualiza o LiveData todosResultados automaticamente
            // quando os dados são alterados no banco de dados
            _operacaoStatus.value = OperacaoStatus.SUCESSO
        } catch (e: Exception) {
            _operacaoStatus.value = OperacaoStatus.ERRO
        }
    }
}
