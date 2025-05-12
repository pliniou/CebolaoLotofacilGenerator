package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * ViewModel para gerenciar os resultados oficiais da Lotofácil.
 */
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
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>()
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
    fun inserirResultado(resultado: Resultado) = viewModelScope.launch {
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
    fun inserirResultados(resultados: List<Resultado>) = viewModelScope.launch {
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
    fun atualizarResultado(resultado: Resultado) = viewModelScope.launch {
        try {
            _operacaoStatus.value = OperacaoStatus.CARREGANDO
            repository.atualizarResultado(resultado)
            _operacaoStatus.value = OperacaoStatus.SUCESSO
        } catch (e: Exception) {
            _operacaoStatus.value = OperacaoStatus.ERRO
        }
    }
    
    /**
     * Busca um resultado pelo número do concurso.
     * @param numeroConcurso O número do concurso.
     */
    fun buscarResultadoPorConcurso(numeroConcurso: Long) = viewModelScope.launch {
        try {
            _operacaoStatus.value = OperacaoStatus.CARREGANDO
            val resultado = withContext(Dispatchers.IO) {
                repository.obterResultadoPorNumero(numeroConcurso)
            }
            _resultadoSelecionado.value = resultado
            _operacaoStatus.value = OperacaoStatus.SUCESSO
        } catch (e: Exception) {
            _operacaoStatus.value = OperacaoStatus.ERRO
        }
    }
    
    /**
     * Busca o último resultado cadastrado.
     */
    fun buscarUltimoResultado() = viewModelScope.launch {
        try {
            _operacaoStatus.value = OperacaoStatus.CARREGANDO
            val resultado = withContext(Dispatchers.IO) {
                repository.obterUltimoResultado()
            }
            _resultadoSelecionado.value = resultado
            _operacaoStatus.value = OperacaoStatus.SUCESSO
        } catch (e: Exception) {
            _operacaoStatus.value = OperacaoStatus.ERRO
        }
    }
    
    /**
     * Verifica se um determinado concurso já existe no banco de dados.
     * @param concurso O número do concurso.
     * @return Verdadeiro se o concurso existir, falso caso contrário.
     */
    suspend fun concursoExiste(concurso: Long): Boolean {
        return withContext(Dispatchers.IO) {
            repository.concursoExiste(concurso)
        }
    }
    
    /**
     * Cria um novo resultado com base nos parâmetros fornecidos.
     */
    fun criarNovoResultado(
        concurso: Long,
        dataSorteio: Date,
        numerosSorteados: List<Int>,
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
            concurso = concurso,
            dataSorteio = dataSorteio,
            numerosSorteados = numerosSorteados.sorted().joinToString(","),
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
    
    /**
     * Limpa o resultado selecionado atualmente.
     */
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
    
    /**
     * Reseta o status da operação.
     */
    fun resetarStatus() {
        _operacaoStatus.value = OperacaoStatus.INATIVO
    }
}
