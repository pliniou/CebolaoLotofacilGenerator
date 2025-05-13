package com.example.cebolaolotofacilgenerator.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cebolaolotofacilgenerator.data.model.Resultado

/** DAO (Data Access Object) para operações relacionadas aos resultados oficiais da Lotofácil. */
@Dao
interface ResultadoDao {
    /**
     * Insere um novo resultado no banco de dados.
     * @param resultado O resultado a ser inserido.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun inserir(resultado: Resultado)

    /**
     * Insere vários resultados de uma vez no banco de dados.
     * @param resultados A lista de resultados a serem inseridos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(resultados: List<Resultado>)

    /**
     * Atualiza um resultado existente no banco de dados.
     * @param resultado O resultado com as informações atualizadas.
     */
    @Update suspend fun atualizar(resultado: Resultado)

    /**
     * Busca um resultado pelo número do concurso.
     * @param concurso O número do concurso.
     * @return O resultado correspondente ao concurso, ou null se não existir.
     */
    @Query("SELECT * FROM resultados WHERE concurso = :concurso")
    suspend fun buscarPorConcurso(concurso: Long): Resultado?

    /**
     * Busca todos os resultados no banco de dados, ordenados por concurso (decrescente).
     * @return Uma lista com todos os resultados.
     */
    @Query("SELECT * FROM resultados ORDER BY concurso DESC")
    suspend fun buscarTodos(): List<Resultado>

    /**
     * Busca todos os resultados no banco de dados e observa mudanças (LiveData).
     * @return Um LiveData com a lista de todos os resultados.
     */
    @Query("SELECT * FROM resultados ORDER BY concurso DESC")
    fun observarTodos(): LiveData<List<Resultado>>

    /**
     * Obtém o último resultado cadastrado (concurso mais recente).
     * @return O resultado mais recente, ou null se não houver nenhum.
     */
    @Query("SELECT * FROM resultados ORDER BY concurso DESC LIMIT 1")
    suspend fun buscarUltimoResultado(): Resultado?

    /**
     * Obtém o último resultado cadastrado (concurso mais recente) e observa mudanças (LiveData).
     * @return Um LiveData com o resultado mais recente.
     */
    @Query("SELECT * FROM resultados ORDER BY concurso DESC LIMIT 1")
    fun observarUltimoResultado(): LiveData<Resultado>

    /**
     * Verifica se um determinado concurso já existe no banco de dados.
     * @param concurso O número do concurso.
     * @return Verdadeiro se o concurso existir, falso caso contrário.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM resultados WHERE concurso = :concurso)")
    suspend fun concursoExiste(concurso: Long): Boolean

    /**
     * Conta quantos resultados estão armazenados no banco de dados.
     * @return O número total de resultados.
     */
    @Query("SELECT COUNT(*) FROM resultados") suspend fun contarResultados(): Int
}
