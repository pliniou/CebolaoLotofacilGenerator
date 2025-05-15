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
import kotlinx.coroutines.launch

/** ViewModel para a tela de Conferência de Jogos */
class ConferenciaViewModel(
        private val jogoRepository: JogoRepository,
        private val resultadoRepository: ResultadoRepository
) : ViewModel() {

    enum class StatusConferencia {
        OCIOSO,
        CONFERINDO,
        CONCLUIDO,
        ERRO
    }

    // Estado para jogos conferidos
    private val _jogosConferidos = MutableStateFlow<List<JogoConferido>>(emptyList())
    val jogosConferidos: StateFlow<List<JogoConferido>> = _jogosConferidos

    // Estado do resultado atual/selecionado para conferência
    private val _resultadoAtual = MutableStateFlow<Resultado?>(null)
    val resultadoAtual: StateFlow<Resultado?> = _resultadoAtual

    // Estado para todos os resultados (para lista de seleção, por exemplo)
    private val _todosResultados = MutableStateFlow<List<Resultado>>(emptyList())
    val todosResultados: StateFlow<List<Resultado>> = _todosResultados

    // Estado do status da conferência
    private val _statusConferencia = MutableStateFlow(StatusConferencia.OCIOSO)
    val statusConferencia: StateFlow<StatusConferencia> = _statusConferencia

    // Estados para contagem de acertos
    private val _acertos15 = MutableStateFlow(0)
    val acertos_15: StateFlow<Int> = _acertos15

    private val _acertos14 = MutableStateFlow(0)
    val acertos_14: StateFlow<Int> = _acertos14

    private val _acertos13 = MutableStateFlow(0)
    val acertos_13: StateFlow<Int> = _acertos13

    private val _acertos12 = MutableStateFlow(0)
    val acertos_12: StateFlow<Int> = _acertos12

    private val _acertos11 = MutableStateFlow(0)
    val acertos_11: StateFlow<Int> = _acertos11

    private val _acertosMenor11 = MutableStateFlow(0)
    val acertos_menor_11: StateFlow<Int> = _acertosMenor11

    /** Classe que representa um jogo conferido com seu número de acertos */
    data class JogoConferido(val jogo: Jogo, val acertos: Int)

    /**
     * Seleciona um resultado para conferência (renomeado para carregarResultado por consistência
     * com Fragment)
     */
    fun carregarResultado(resultado: Resultado) {
        viewModelScope.launch {
            _resultadoAtual.value = resultado
            // Limpar jogos conferidos ao carregar novo resultado para conferência
            _jogosConferidos.value = emptyList()
            resetarContadoresAcertos()
        }
    }

    fun carregarUltimoResultado() {
        viewModelScope.launch {
            _resultadoAtual.value = resultadoRepository.obterUltimoResultado()
            _jogosConferidos.value = emptyList()
            resetarContadoresAcertos()
        }
    }

    fun carregarTodosResultados() {
        viewModelScope.launch {
            // Supondo que o LiveData do repositório possa ser coletado ou convertido
            // Para simplificar, vamos buscar uma vez. O ideal seria observar um Flow do repo.
            // Esta é uma simplificação. O ideal seria o repositório expor um Flow.
            val resultados = resultadoRepository.todosResultados.value ?: emptyList()
            _todosResultados.value = resultados
        }
    }

    fun salvarResultado(numeroConcurso: Int, numerosSorteados: List<Int>, dataSorteio: Date) {
        viewModelScope.launch {
            val novoResultado =
                    Resultado(
                            dataSorteio = dataSorteio,
                            numeros = numerosSorteados,
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

    /** Limpa o resultado selecionado (renomeado para limparResultadoAtual) */
    fun limparResultadoAtual() {
        _resultadoAtual.value = null
        _jogosConferidos.value = emptyList()
        resetarContadoresAcertos()
    }

    /** Confere jogos contra o resultado selecionado */
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
                            val acertos = contarAcertos(jogo.numeros, resultado.numeros)
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
}
