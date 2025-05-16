package com.example.cebolaolotofacilgenerator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import java.util.Date // Importação para java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica da tela de conferência de jogos.
 *
 * Gerencia o estado do resultado selecionado para conferência, a lista de jogos conferidos,
 * o status da operação de conferência e as estatísticas de acertos.
 */
class ConferenciaViewModel(
        private val jogoRepository: JogoRepository,
        private val resultadoRepository: ResultadoRepository
) : ViewModel() {

    /** Enum que representa os possíveis estados da operação de conferência. */
    enum class StatusConferencia {
        /** Estado inicial ou após uma operação ser concluída/resetada. */
        OCIOSO,
        /** Indica que a conferência de jogos está em progresso. */
        CONFERINDO,
        /** Indica que a conferência de jogos foi concluída com sucesso. */
        CONCLUIDO,
        /** Indica que ocorreu um erro durante a conferência. */
        ERRO
    }

    // Estado para jogos conferidos
    private val _jogosConferidos = MutableStateFlow<List<JogoConferido>>(emptyList())
    /** StateFlow que emite a lista atual de jogos conferidos. */
    val jogosConferidos: StateFlow<List<JogoConferido>> = _jogosConferidos

    // Estado do resultado atual/selecionado para conferência
    private val _resultadoAtual = MutableStateFlow<Resultado?>(null)
    /** StateFlow que emite o resultado atualmente selecionado para a conferência. Nulo se nenhum selecionado. */
    val resultadoAtual: StateFlow<Resultado?> = _resultadoAtual

    // Estado para todos os resultados (para lista de seleção, por exemplo)
    private val _todosResultados = MutableStateFlow<List<Resultado>>(emptyList())
    /** StateFlow que emite a lista de todos os resultados de concursos salvos. */
    val todosResultados: StateFlow<List<Resultado>> = _todosResultados

    // Estado do status da conferência
    private val _statusConferencia = MutableStateFlow(StatusConferencia.OCIOSO)
    /** StateFlow que emite o status atual da operação de conferência. */
    val statusConferencia: StateFlow<StatusConferencia> = _statusConferencia.asStateFlow()

    // Estados para contagem de acertos
    /** StateFlow que emite a contagem de jogos com 15 acertos. */
    private val _acertos15 = MutableStateFlow(0)
    /** StateFlow público para a contagem de jogos com 15 acertos. */
    val acertos_15: StateFlow<Int> = _acertos15

    /** StateFlow que emite a contagem de jogos com 14 acertos. */
    private val _acertos14 = MutableStateFlow(0)
    /** StateFlow público para a contagem de jogos com 14 acertos. */
    val acertos_14: StateFlow<Int> = _acertos14

    /** StateFlow que emite a contagem de jogos com 13 acertos. */
    private val _acertos13 = MutableStateFlow(0)
    /** StateFlow público para a contagem de jogos com 13 acertos. */
    val acertos_13: StateFlow<Int> = _acertos13

    /** StateFlow que emite a contagem de jogos com 12 acertos. */
    private val _acertos12 = MutableStateFlow(0)
    /** StateFlow público para a contagem de jogos com 12 acertos. */
    val acertos_12: StateFlow<Int> = _acertos12

    /** StateFlow que emite a contagem de jogos com 11 acertos. */
    private val _acertos11 = MutableStateFlow(0)
    /** StateFlow público para a contagem de jogos com 11 acertos. */
    val acertos_11: StateFlow<Int> = _acertos11

    /** StateFlow que emite a contagem de jogos com menos de 11 acertos. */
    private val _acertosMenor11 = MutableStateFlow(0)
    /** StateFlow público para a contagem de jogos com menos de 11 acertos. */
    val acertos_menor_11: StateFlow<Int> = _acertosMenor11

    /** Classe de dados que representa um jogo conferido, incluindo o próprio [Jogo] e a quantidade de [acertos]. */
    data class JogoConferido(val jogo: Jogo, val acertos: Int)

    /**
     * Carrega e define um [Resultado] específico como o atual para conferência.
     * Limpa os jogos previamente conferidos e reseta as contagens de acertos.
     *
     * @param resultado O [Resultado] a ser definido como atual.
     */
    fun carregarResultado(resultado: Resultado) {
        viewModelScope.launch {
            _resultadoAtual.value = resultado
            // Limpar jogos conferidos ao carregar novo resultado para conferência
            _jogosConferidos.value = emptyList()
            resetarContadoresAcertos()
        }
    }

    /** Carrega o último resultado salvo no repositório e o define como atual para conferência. */
    fun carregarUltimoResultado() {
        viewModelScope.launch {
            _resultadoAtual.value = resultadoRepository.obterUltimoResultado()
            _jogosConferidos.value = emptyList()
            resetarContadoresAcertos()
        }
    }

    /** Carrega todos os resultados disponíveis do repositório. */
    fun carregarTodosResultados() {
        viewModelScope.launch {
            // Supondo que o LiveData do repositório possa ser coletado ou convertido
            // Para simplificar, vamos buscar uma vez. O ideal seria observar um Flow do repo.
            // Esta é uma simplificação. O ideal seria o repositório expor um Flow.
            val resultados = resultadoRepository.todosResultados.value ?: emptyList()
            _todosResultados.value = resultados
        }
    }

    /**
     * Salva um novo [Resultado] no repositório.
     * Após salvar, atualiza a lista de todos os resultados e define o resultado recém-salvo como o atual.
     *
     * @param numeroConcurso O número do concurso.
     * @param numerosSorteados A lista de dezenas sorteadas.
     * @param dataSorteio A data do sorteio.
     */
    fun salvarResultado(numeroConcurso: Int, numerosSorteados: List<Int>, dataSorteio: Date) {
        viewModelScope.launch {
            val novoResultado =
                    Resultado(
                            concurso = numeroConcurso.toLong(),
                            dataSorteio = dataSorteio,
                            dezenas = numerosSorteados,
                            premiacao15Acertos = 0.0,
                            ganhadores15 = 0,
                            premiacao14Acertos = 0.0,
                            ganhadores14 = 0,
                            premiacao13Acertos = 0.0,
                            ganhadores13 = 0,
                            premiacao12Acertos = 0.0,
                            ganhadores12 = 0,
                            premiacao11Acertos = 0.0,
                            ganhadores11 = 0
                    )
            resultadoRepository.inserirResultado(novoResultado)
            carregarTodosResultados() // Atualiza a lista de resultados
            carregarUltimoResultado() // Alterado para carregar o último, que deve ser o que
            // acabamos de salvar
        }
    }

    /** Limpa o resultado atualmente selecionado, a lista de jogos conferidos e reseta as contagens de acertos. */
    fun limparResultadoAtual() {
        _resultadoAtual.value = null
        _jogosConferidos.value = emptyList()
        resetarContadoresAcertos()
    }

    /**
     * Inicia o processo de conferência de todos os jogos salvos contra o [resultadoAtual].
     * Atualiza o [statusConferencia], [jogosConferidos] e as estatísticas de acertos.
     * Define o status como [StatusConferencia.ERRO] se nenhum resultado estiver selecionado ou se ocorrer uma exceção.
     */
    fun conferirJogos() { // Modificado para não esperar lista de jogos como parâmetro por agora
        val resultado = _resultadoAtual.value
        if (resultado == null) {
            _statusConferencia.value =
                    StatusConferencia.ERRO // Ou algum status indicando "sem resultado"
            return
        }

        _statusConferencia.value = StatusConferencia.CONFERINDO
        viewModelScope.launch {
            try {
                val todosOsJogos =
                        jogoRepository.todosJogos.value
                                ?: emptyList() // Simplificação, idealmente observar Flow
                if (todosOsJogos.isEmpty()) {
                    _jogosConferidos.value = emptyList()
                    _statusConferencia.value =
                            StatusConferencia.CONCLUIDO // Ou um status "sem jogos para conferir"
                    return@launch
                }

                val jogosConferidosCalculados =
                        todosOsJogos.map { jogo ->
                            val acertos = contarAcertos(jogo.numeros, resultado.dezenas)
                            JogoConferido(jogo, acertos)
                        }
                _jogosConferidos.value = jogosConferidosCalculados
                contabilizarEstatisticas(jogosConferidosCalculados)
                _statusConferencia.value = StatusConferencia.CONCLUIDO
            } catch (e: Exception) {
                // Logar exceção e definir status de erro
                _statusConferencia.value = StatusConferencia.ERRO
            }
        }
    }

    /** Conta o número de acertos de um jogo */
    private fun contarAcertos(numerosJogo: List<Int>, numerosResultado: List<Int>): Int {
        return numerosJogo.count { it in numerosResultado }
    }

    /** Contabiliza estatísticas de acertos */
    private fun contabilizarEstatisticas(jogosConferidos: List<JogoConferido>) {
        resetarContadoresAcertos()

        jogosConferidos.forEach { jogoConferido ->
            when (jogoConferido.acertos) {
                15 -> _acertos15.value++
                14 -> _acertos14.value++
                13 -> _acertos13.value++
                12 -> _acertos12.value++
                11 -> _acertos11.value++
                else -> _acertosMenor11.value++
            }
        }
    }

    /** Reseta todos os contadores de acertos */
    private fun resetarContadoresAcertos() {
        _acertos15.value = 0
        _acertos14.value = 0
        _acertos13.value = 0
        _acertos12.value = 0
        _acertos11.value = 0
        _acertosMenor11.value = 0
    }

    // Funções para Preview no Jetpack Compose
    fun clearResultadoAtualForPreview() {
        _resultadoAtual.value = null
    }

    fun setResultadoAtualForPreview(resultado: Resultado) {
        _resultadoAtual.value = resultado
    }

    fun setTodosResultadosForPreview(resultados: List<Resultado>) {
        _todosResultados.value = resultados
    }

    init {
        // ... existing code ...
    }
}
