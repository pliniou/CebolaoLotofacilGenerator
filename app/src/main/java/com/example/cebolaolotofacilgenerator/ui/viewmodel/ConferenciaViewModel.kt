package com.example.cebolaolotofacilgenerator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.data.repository.JogoRepository
import com.example.cebolaolotofacilgenerator.data.repository.ResultadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de Conferência de Jogos
 */
class ConferenciaViewModel(
    private val jogoRepository: JogoRepository,
    private val resultadoRepository: ResultadoRepository
) : ViewModel() {

    // Estado para jogos conferidos
    private val _jogosConferidos = MutableStateFlow<List<JogoConferido>>(emptyList())
    val jogosConferidos: StateFlow<List<JogoConferido>> = _jogosConferidos

    // Estado do resultado selecionado
    private val _resultadoSelecionado = MutableStateFlow<Resultado?>(null)
    val resultadoSelecionado: StateFlow<Resultado?> = _resultadoSelecionado

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

    /**
     * Classe que representa um jogo conferido com seu número de acertos
     */
    data class JogoConferido(
        val jogo: Jogo,
        val acertos: Int
    )

    /**
     * Seleciona um resultado para conferência
     */
    fun selecionarResultado(resultado: Resultado) {
        _resultadoSelecionado.value = resultado
    }

    /**
     * Limpa o resultado selecionado
     */
    fun limparResultadoSelecionado() {
        _resultadoSelecionado.value = null
        _jogosConferidos.value = emptyList()
        resetarContadoresAcertos()
    }

    /**
     * Confere jogos contra o resultado selecionado
     */
    fun conferirJogos(jogos: List<Jogo>) {
        val resultado = _resultadoSelecionado.value ?: return
        
        viewModelScope.launch {
            val jogosConferidos = jogos.map { jogo ->
                val acertos = contarAcertos(jogo.numeros, resultado.numeros)
                JogoConferido(jogo, acertos)
            }
            
            _jogosConferidos.value = jogosConferidos
            contabilizarEstatisticas(jogosConferidos)
        }
    }

    /**
     * Conta o número de acertos de um jogo
     */
    private fun contarAcertos(numerosJogo: List<Int>, numerosResultado: List<Int>): Int {
        return numerosJogo.count { it in numerosResultado }
    }

    /**
     * Contabiliza estatísticas de acertos
     */
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

    /**
     * Reseta todos os contadores de acertos
     */
    private fun resetarContadoresAcertos() {
        _acertos15.value = 0
        _acertos14.value = 0
        _acertos13.value = 0
        _acertos12.value = 0
        _acertos11.value = 0
        _acertosMenor11.value = 0
    }
}
