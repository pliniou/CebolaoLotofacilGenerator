package com.example.cebolaolotofacilgenerator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.model.common.OperacaoStatus
import com.example.cebolaolotofacilgenerator.util.VerificadorJogos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** ViewModel para gerenciar os jogos da Lotofácil. */
class JogoViewModel(application: Application) : AndroidViewModel(application) {

    // Repositório de jogos
    private val repository: JogoRepository

    // LiveData para observar todos os jogos
    val todosJogos: LiveData<List<Jogo>>

    // LiveData para observar jogos favoritos
    val jogosFavoritos: LiveData<List<Jogo>>

    // LiveData para o jogo selecionado atualmente
    private val _jogoSelecionado = MutableLiveData<Jogo?>()
    val jogoSelecionado: LiveData<Jogo?> = _jogoSelecionado

    // LiveData para jogos conferidos (preenchido sob demanda)
    private val _jogosConferidos = MutableLiveData<List<Jogo>>()
    val jogosConferidos: LiveData<List<Jogo>> = _jogosConferidos

    // Estado da operação atual
    private val _operacaoStatus = MutableLiveData<OperacaoStatus>()
    val operacaoStatus: LiveData<OperacaoStatus> = _operacaoStatus

    init {
        val jogoDao = AppDatabase.getDatabase(application).jogoDao()
        repository = JogoRepository(jogoDao)
        todosJogos = repository.todosJogos
        jogosFavoritos = repository.jogosFavoritos
    }

    /**
     * Insere um novo jogo no banco de dados.
     * @param jogo O jogo a ser inserido.
     */
    fun inserirJogo(jogo: Jogo) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.inserirJogo(jogo)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Insere vários jogos de uma vez no banco de dados.
     * @param jogos A lista de jogos a serem inseridos.
     */
    fun inserirJogos(jogos: List<Jogo>) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.inserirJogos(jogos)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Atualiza um jogo existente no banco de dados.
     * @param jogo O jogo com as informações atualizadas.
     */
    fun atualizarJogo(jogo: Jogo) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.atualizarJogo(jogo)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Remove um jogo do banco de dados.
     * @param jogo O jogo a ser removido.
     */
    fun deletarJogo(jogo: Jogo) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.excluirJogo(jogo)
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Busca um jogo pelo seu ID.
     * @param jogoId O ID do jogo.
     */
    fun buscarJogoPorId(jogoId: Long) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    val jogo = withContext(Dispatchers.IO) { repository.obterJogoPorId(jogoId) }
                    _jogoSelecionado.value = jogo
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Marca ou desmarca um jogo como favorito.
     * @param jogo O jogo a ser atualizado.
     * @param favorito Verdadeiro para marcar como favorito, falso para desmarcar.
     */
    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    repository.atualizarJogo(jogo.copy(favorito = favorito))
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Confere uma lista de jogos contra um resultado.
     * @param jogos Lista de jogos a serem conferidos.
     * @param resultado O resultado oficial para comparação.
     */
    fun conferirJogos(jogos: List<Jogo>, resultado: Resultado) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO

                    val jogosConferidos =
                            withContext(Dispatchers.Default) {
                                VerificadorJogos.conferirJogos(jogos, resultado)
                            }

                    // Atualiza os jogos no banco de dados com os resultados da conferência
                    withContext(Dispatchers.IO) {
                        jogosConferidos.forEach { jogo -> repository.atualizarJogo(jogo) }
                    }

                    _jogosConferidos.value = jogosConferidos
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /**
     * Busca jogos que foram conferidos com um determinado concurso.
     * @param concursoId O ID do concurso.
     */
    fun buscarJogosConferidos(concursoId: Long) =
            viewModelScope.launch {
                try {
                    _operacaoStatus.value = OperacaoStatus.CARREGANDO
                    val jogos =
                            withContext(Dispatchers.IO) {
                                repository.buscarJogosConferidos(concursoId)
                            }
                    _jogosConferidos.value = jogos
                    _operacaoStatus.value = OperacaoStatus.SUCESSO
                } catch (e: Exception) {
                    _operacaoStatus.value = OperacaoStatus.ERRO
                }
            }

    /** Limpa o jogo selecionado atualmente. */
    fun limparJogoSelecionado() {
        _jogoSelecionado.value = null
    }

    /**
     * Define o jogo selecionado.
     * @param jogo O jogo a ser selecionado.
     */
    fun selecionarJogo(jogo: Jogo) {
        _jogoSelecionado.value = jogo
    }

    /** Reseta o status da operação. */
    fun resetarStatus() {
        _operacaoStatus.value = OperacaoStatus.INATIVO
    }
}
