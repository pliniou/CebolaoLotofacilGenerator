package com.example.cebolaolotofacilgenerator.data.repository

import androidx.lifecycle.LiveData
import com.example.cebolaolotofacilgenerator.data.dao.ResultadoDao
import com.example.cebolaolotofacilgenerator.data.model.Resultado

/**
 * Repositório para gerenciar operações relacionadas aos resultados oficiais da Lotofácil.
 * Fornece uma camada de abstração sobre o DAO.
 */
class ResultadoRepository(private val resultadoDao: ResultadoDao) {

    // Observa todos os resultados
    val todosResultados: LiveData<List<Resultado>> = resultadoDao.observarTodos()
    
    // Observa apenas o último resultado
    val ultimoResultado: LiveData<Resultado> = resultadoDao.observarUltimoResultado()

    /**
     * Insere um novo resultado no banco de dados.
     * @param resultado O resultado a ser inserido.
     */
    suspend fun inserirResultado(resultado: Resultado) {
        resultadoDao.inserir(resultado)
    }
    
    /**
     * Insere vários resultados de uma vez no banco de dados.
     * @param resultados A lista de resultados a serem inseridos.
     */
    suspend fun inserirResultados(resultados: List<Resultado>) {
        resultadoDao.inserirTodos(resultados)
    }

    /**
     * Atualiza um resultado existente no banco de dados.
     * @param resultado O resultado com as informações atualizadas.
     */
    suspend fun atualizarResultado(resultado: Resultado) {
        resultadoDao.atualizar(resultado)
    }

    /**
     * Busca um resultado pelo número do concurso.
     * @param numeroConcurso O número do concurso.
     * @return O resultado correspondente ao concurso, ou null se não existir.
     */
    suspend fun obterResultadoPorNumero(numeroConcurso: Long): Resultado? {
        return resultadoDao.buscarPorConcurso(numeroConcurso)
    }

    /**
     * Obtém o último resultado cadastrado (concurso mais recente).
     * @return O resultado mais recente, ou null se não houver nenhum.
     */
    suspend fun obterUltimoResultado(): Resultado? {
        return resultadoDao.buscarUltimoResultado()
    }
    
    /**
     * Verifica se um determinado concurso já existe no banco de dados.
     * @param concurso O número do concurso.
     * @return Verdadeiro se o concurso existir, falso caso contrário.
     */
    suspend fun concursoExiste(concurso: Long): Boolean {
        return resultadoDao.concursoExiste(concurso)
    }
    
    /**
     * Conta quantos resultados estão armazenados no banco de dados.
     * @return O número total de resultados.
     */
    suspend fun contarResultados(): Int {
        return resultadoDao.contarResultados()
    }
}
