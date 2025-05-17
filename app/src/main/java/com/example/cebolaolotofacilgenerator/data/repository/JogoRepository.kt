package com.example.cebolaolotofacilgenerator.data.repository

import androidx.lifecycle.LiveData
import com.example.cebolaolotofacilgenerator.data.dao.JogoDao
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório para gerenciar operações relacionadas aos jogos da Lotofácil. Fornece uma camada de
 * abstração sobre o DAO.
 */
@Singleton
class JogoRepository @Inject constructor(private val jogoDao: JogoDao) {

    // Observa todos os jogos
    val todosJogos: LiveData<List<Jogo>> = jogoDao.observarTodos()

    // Observa apenas os jogos favoritos
    val jogosFavoritos: LiveData<List<Jogo>> = jogoDao.observarFavoritos()

    /**
     * Busca todos os jogos do banco de dados.
     * @return Lista com todos os jogos.
     */
    suspend fun buscarTodosJogos(): List<Jogo> {
        return jogoDao.buscarTodos()
    }

    /**
     * Busca todos os jogos favoritos do banco de dados.
     * @return Lista com todos os jogos favoritos.
     */
    suspend fun buscarJogosFavoritos(): List<Jogo> {
        return jogoDao.buscarFavoritos()
    }

    /**
     * Insere um novo jogo no banco de dados.
     * @param jogo O jogo a ser inserido.
     * @return O ID gerado para o jogo inserido.
     */
    suspend fun inserirJogo(jogo: Jogo): Long {
        return jogoDao.inserir(jogo)
    }

    /**
     * Insere vários jogos de uma vez no banco de dados.
     * @param jogos A lista de jogos a serem inseridos.
     * @return A lista de IDs gerados para os jogos inseridos.
     */
    suspend fun inserirJogos(jogos: List<Jogo>): List<Long> {
        return jogoDao.inserirTodos(jogos)
    }

    /**
     * Atualiza um jogo existente no banco de dados.
     * @param jogo O jogo com as informações atualizadas.
     */
    suspend fun atualizarJogo(jogo: Jogo) {
        jogoDao.atualizar(jogo)
    }

    /**
     * Remove um jogo do banco de dados.
     * @param jogo O jogo a ser removido.
     */
    suspend fun excluirJogo(jogo: Jogo) {
        jogoDao.excluir(jogo)
    }

    /**
     * Busca um jogo pelo seu ID.
     * @param jogoId O ID do jogo.
     * @return O jogo correspondente ao ID, ou null se não existir.
     */
    suspend fun obterJogoPorId(jogoId: Long): Jogo? {
        return jogoDao.buscarPorId(jogoId)
    }

    /**
     * Conta quantos jogos estão armazenados no banco de dados.
     * @return O número total de jogos.
     */
    suspend fun contarJogos(): Int {
        return jogoDao.contarJogos()
    }

    /** Remove todos os jogos do banco de dados. */
    suspend fun excluirTodosJogos() {
        jogoDao.limparTodos()
    }
}
