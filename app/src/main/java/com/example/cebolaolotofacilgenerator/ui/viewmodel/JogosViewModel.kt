package com.example.cebolaolotofacilgenerator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.db.AppDatabase
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.util.GeradorJogos
import kotlinx.coroutines.launch

// Enum para o status da geração de jogos
enum class StatusGeracao {
    OCIOSO,
    GERANDO,
    CONCLUIDO,
    ERRO
}

class JogosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JogoRepository

    // LiveData para todos os jogos, jogos favoritos e jogos conferidos
    val todosJogos: LiveData<List<Jogo>>
    val jogosFavoritos: LiveData<List<Jogo>>
    private val _jogosConferidos = MutableLiveData<List<Jogo>>()
    val jogosConferidos: LiveData<List<Jogo>>
        get() = _jogosConferidos

    // LiveData para jogos gerados na tela principal
    private val _jogosGerados = MutableLiveData<List<Jogo>>()
    val jogosGerados: LiveData<List<Jogo>>
        get() = _jogosGerados

    // LiveData para o status da geração de jogos
    private val _statusGeracao = MutableLiveData<StatusGeracao>(StatusGeracao.OCIOSO)
    val statusGeracao: LiveData<StatusGeracao>
        get() = _statusGeracao

    // LiveData para mensagens de erro ou informativas
    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?>
        get() = _mensagem

    init {
        val jogoDao = AppDatabase.getDatabase(application).jogoDao()
        repository = JogoRepository(jogoDao)
        todosJogos = repository.todosJogos
        jogosFavoritos = repository.jogosFavoritos
        // Carregar jogos conferidos inicialmente se necessário (ex: último concurso)
        // carregarJogosConferidos(idDoUltimoConcurso)
    }

    fun gerarJogos(
            quantidadeJogos: Int,
            quantidadeNumerosPorJogo: Int = 15,
            filtros: ConfiguracaoFiltros? = null
    ) {
        _statusGeracao.value = StatusGeracao.GERANDO
        viewModelScope.launch {
            try {
                val jogos =
                        GeradorJogos.gerarJogos(
                                quantidadeJogos = quantidadeJogos,
                                quantidadeNumeros = quantidadeNumerosPorJogo,
                                numerosFixos = filtros?.numerosFixos ?: emptyList(),
                                numerosExcluidos = filtros?.numerosExcluidos ?: emptyList(),
                                minPares =
                                        if (filtros?.filtroParesImpares == true)
                                                filtros.atualMinPares
                                        else null,
                                maxPares =
                                        if (filtros?.filtroParesImpares == true)
                                                filtros.atualMaxPares
                                        else null,
                                minPrimos =
                                        if (filtros?.filtroPrimos == true) filtros.atualMinPrimos
                                        else null,
                                maxPrimos =
                                        if (filtros?.filtroPrimos == true) filtros.atualMaxPrimos
                                        else null,
                                minFibonacci =
                                        if (filtros?.filtroFibonacci == true)
                                                filtros.atualMinFibonacci
                                        else null,
                                maxFibonacci =
                                        if (filtros?.filtroFibonacci == true)
                                                filtros.atualMaxFibonacci
                                        else null,
                                minMiolo =
                                        if (filtros?.filtroMioloMoldura == true)
                                                filtros.atualMinMiolo
                                        else null, // Assumindo que Miolo/Moldura usa os mesmos
                                // campos
                                maxMiolo =
                                        if (filtros?.filtroMioloMoldura == true)
                                                filtros.atualMaxMiolo
                                        else null,
                                minMultiplosTres =
                                        if (filtros?.filtroMultiplosDeTres == true)
                                                filtros.atualMinMultiplos
                                        else null,
                                maxMultiplosTres =
                                        if (filtros?.filtroMultiplosDeTres == true)
                                                filtros.atualMaxMultiplos
                                        else null,
                                minSoma =
                                        if (filtros?.filtroSomaTotal == true) filtros.atualMinSoma
                                        else null,
                                maxSoma =
                                        if (filtros?.filtroSomaTotal == true) filtros.atualMaxSoma
                                        else null
                        )
                _jogosGerados.postValue(jogos)
                _statusGeracao.postValue(StatusGeracao.CONCLUIDO)
            } catch (e: IllegalArgumentException) {
                _jogosGerados.postValue(emptyList())
                _mensagem.postValue(e.message ?: "Erro ao gerar jogos: Parâmetros inválidos")
                _statusGeracao.postValue(StatusGeracao.ERRO)
            } catch (e: Exception) {
                _jogosGerados.postValue(emptyList())
                _mensagem.postValue("Erro desconhecido ao gerar jogos: ${e.localizedMessage}")
                _statusGeracao.postValue(StatusGeracao.ERRO)
            }
        }
    }

    fun salvarJogos(jogos: List<Jogo>) {
        viewModelScope.launch {
            try {
                repository.inserirJogos(jogos)
                _mensagem.postValue("Jogos salvos com sucesso!")
                // Limpar jogos gerados da tela após salvar, ou dar opção ao usuário
                _jogosGerados.postValue(emptyList())
            } catch (e: Exception) {
                _mensagem.postValue("Erro ao salvar jogos: ${e.localizedMessage}")
            }
        }
    }

    fun marcarComoFavorito(jogo: Jogo, favorito: Boolean) {
        viewModelScope.launch {
            try {
                val jogoAtualizado = jogo.copy(favorito = favorito)
                repository.atualizarJogo(jogoAtualizado)
                val msg =
                        if (favorito) "Jogo marcado como favorito."
                        else "Jogo desmarcado como favorito."
                _mensagem.postValue(msg)
            } catch (e: Exception) {
                _mensagem.postValue("Erro ao atualizar favorito: ${e.localizedMessage}")
            }
        }
    }

    fun deletarJogo(jogo: Jogo) {
        viewModelScope.launch {
            try {
                repository.deletarJogo(jogo)
                _mensagem.postValue("Jogo excluído com sucesso.")
            } catch (e: Exception) {
                _mensagem.postValue("Erro ao excluir jogo: ${e.localizedMessage}")
            }
        }
    }

    fun limparTodosJogos() {
        viewModelScope.launch {
            _mensagem.postValue(
                    "Funcionalidade de limpar todos os jogos precisa ser implementada no JogoDao/JogoRepository."
            )
            // Exemplo: val dao = AppDatabase.getDatabase(getApplication()).jogoDao()
            // dao.limparTodos()
            // E então atualizar os LiveData observados, se necessário, ou confiar na observação
            // direta do repositório.
        }
    }

    fun carregarJogosConferidos(concursoId: Long) {
        viewModelScope.launch {
            try {
                _jogosConferidos.postValue(repository.buscarJogosConferidos(concursoId))
            } catch (e: Exception) {
                _mensagem.postValue("Erro ao carregar jogos conferidos: ${e.localizedMessage}")
            }
        }
    }

    fun limparMensagem() {
        _mensagem.value = null
    }
}
