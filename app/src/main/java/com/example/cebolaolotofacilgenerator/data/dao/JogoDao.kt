package com.example.cebolaolotofacilgenerator.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cebolaolotofacilgenerator.data.model.Jogo

/** DAO (Data Access Object) para operações relacionadas aos jogos da Lotofácil. */
@Dao
interface JogoDao {
    /**
     * Insere um novo jogo no banco de dados.
     * @param jogo O jogo a ser inserido.
     * @return O ID gerado para o jogo inserido.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun inserir(jogo: Jogo): Long

    /**
     * Insere vários jogos de uma vez no banco de dados.
     * @param jogos A lista de jogos a serem inseridos.
     * @return A lista de IDs gerados para os jogos inseridos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(jogos: List<Jogo>): List<Long>

    /**
     * Atualiza um jogo existente no banco de dados.
     * @param jogo O jogo com as informações atualizadas.
     */
    @Update suspend fun atualizar(jogo: Jogo)

    /**
     * Remove um jogo do banco de dados.
     * @param jogo O jogo a ser removido.
     */
    @Delete suspend fun excluir(jogo: Jogo)

    /**
     * Busca um jogo pelo seu ID.
     * @param id O ID do jogo.
     * @return O jogo correspondente ao ID, ou null se não existir.
     */
    @Query("SELECT * FROM jogos WHERE id = :id") suspend fun buscarPorId(id: Long): Jogo?

    /**
     * Busca todos os jogos no banco de dados.
     * @return Uma lista com todos os jogos.
     */
    @Query("SELECT * FROM jogos ORDER BY dataCriacao DESC") suspend fun buscarTodos(): List<Jogo>

    /**
     * Busca todos os jogos no banco de dados e observa mudanças (LiveData).
     * @return Um LiveData com a lista de todos os jogos.
     */
    @Query("SELECT * FROM jogos ORDER BY dataCriacao DESC")
    fun observarTodos(): LiveData<List<Jogo>>

    /**
     * Busca todos os jogos marcados como favoritos.
     * @return Uma lista com todos os jogos favoritos.
     */
    @Query("SELECT * FROM jogos WHERE favorito = 1 ORDER BY dataCriacao DESC")
    suspend fun buscarFavoritos(): List<Jogo>

    /**
     * Busca todos os jogos marcados como favoritos e observa mudanças (LiveData).
     * @return Um LiveData com a lista de jogos favoritos.
     */
    @Query("SELECT * FROM jogos WHERE favorito = 1 ORDER BY dataCriacao DESC")
    fun observarFavoritos(): LiveData<List<Jogo>>

    /**
     * Busca jogos que foram conferidos com um determinado concurso.
     * @param concursoId O ID do concurso.
     * @return Uma lista de jogos conferidos com o concurso especificado.
     */
    @Query(
            "SELECT * FROM jogos WHERE concursoConferido = :concursoId ORDER BY acertos DESC, dataCriacao DESC"
    )
    suspend fun buscarJogosConferidos(concursoId: Long): List<Jogo>

    /**
     * Conta quantos jogos estão armazenados no banco de dados.
     * @return O número total de jogos.
     */
    @Query("SELECT COUNT(*) FROM jogos") suspend fun contarJogos(): Int

    /** Remove todos os jogos do banco de dados. */
    @Query("DELETE FROM jogos") suspend fun limparTodos()
}
