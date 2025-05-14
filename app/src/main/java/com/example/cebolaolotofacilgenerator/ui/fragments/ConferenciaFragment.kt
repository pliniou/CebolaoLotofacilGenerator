package com.example.cebolaolotofacilgenerator.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.databinding.FragmentConferenciaBinding
import com.example.cebolaolotofacilgenerator.ui.adapters.JogosConferidosAdapter
import com.example.cebolaolotofacilgenerator.ui.adapters.ResultadosAdapter
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConferenciaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

class ConferenciaFragment : Fragment() {

    private var _binding: FragmentConferenciaBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ConferenciaViewModel by viewModels()
    private lateinit var jogosConferidosAdapter: JogosConferidosAdapter
    private lateinit var resultadosAdapter: ResultadosAdapter
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConferenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()
        setupListeners()

        // Carrega o último resultado salvo, se houver
        viewModel.carregarUltimoResultado()
    }

    private fun setupRecyclerViews() {
        // Adapter para jogos conferidos
        jogosConferidosAdapter = JogosConferidosAdapter()

        binding.recyclerViewJogosConferidos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jogosConferidosAdapter
        }

        // Adapter para resultados anteriores
        resultadosAdapter =
                ResultadosAdapter(
                        onResultadoClick = { resultado ->
                            viewModel.carregarResultado(resultado.concurso)
                        }
                )

        binding.recyclerViewResultadosAnteriores.apply {
            layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = resultadosAdapter
        }
    }

    private fun setupObservers() {
        // Observa os resultados anteriores
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todosResultados.collect { resultados ->
                    resultadosAdapter.submitList(resultados)
                    atualizarVisibilidadeResultadosAnteriores(resultados.isEmpty())
                }
            }
        }

        // Observa o resultado atual carregado
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultadoAtual.collect { resultado ->
                    if (resultado != null) {
                        // TODO: Adicionar string 'concurso_numero' em strings.xml (ex: <string
                        // name="concurso_numero">Concurso N° %d</string>)
                        binding.textViewConcursoNumero.text =
                                getString(R.string.concurso_numero, resultado.concurso)
                        binding.textViewDataConcurso.text =
                                dateFormatter.format(resultado.dataSorteio)

                        // Preenche os números do resultado
                        val numerosSorteados = resultado.numeros
                        binding.textViewNumerosSorteados.text = numerosSorteados.joinToString(" - ")

                        // Habilita o botão de conferência
                        binding.buttonConferirJogos.isEnabled = true
                    } else {
                        limparCamposResultado()
                    }
                }
            }
        }

        // Observa os jogos conferidos
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.jogosConferidos.collect { jogosConferidos ->
                    jogosConferidosAdapter.submitList(jogosConferidos)
                    atualizarVisibilidadeJogosConferidos(jogosConferidos.isEmpty())
                }
            }
        }

        // Observa o status da conferência
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statusConferencia.collect { status ->
                    when (status) {
                        ConferenciaViewModel.StatusConferencia.CONFERINDO -> {
                            binding.progressBarConferencia.visibility = View.VISIBLE
                            binding.buttonConferirJogos.isEnabled = false
                        }
                        ConferenciaViewModel.StatusConferencia.CONCLUIDO -> {
                            binding.progressBarConferencia.visibility = View.GONE
                            binding.buttonConferirJogos.isEnabled = true
                        }
                        ConferenciaViewModel.StatusConferencia.ERRO -> {
                            binding.progressBarConferencia.visibility = View.GONE
                            binding.buttonConferirJogos.isEnabled = true
                            // TODO: Adicionar string 'erro_conferir_jogos' em strings.xml
                            Toast.makeText(
                                            requireContext(),
                                            R.string.erro_conferir_jogos,
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                        ConferenciaViewModel.StatusConferencia.OCIOSO -> {
                            binding.progressBarConferencia.visibility = View.GONE
                            binding.buttonConferirJogos.isEnabled =
                                    true // Ou false dependendo da lógica inicial
                        }
                    // else -> {} // Não é mais necessário com when exaustivo sobre enum
                    }
                }
            }
        }
        // Carregar todos os resultados para a lista de seleção
        viewModel.carregarTodosResultados()
    }

    private fun setupListeners() {
        // Botão para salvar um novo resultado
        binding.buttonSalvarResultado.setOnClickListener {
            // Valida o número do concurso
            val numeroConcurso = binding.editTextNumeroConcurso.text.toString().toIntOrNull()
            if (numeroConcurso == null) {
                // TODO: Adicionar string 'erro_numero_concurso_invalido' em strings.xml
                Toast.makeText(
                                requireContext(),
                                R.string.erro_numero_concurso_invalido,
                                Toast.LENGTH_SHORT
                        )
                        .show()
                return@setOnClickListener
            }

            // Obtém os números digitados
            val numerosTexto = binding.editTextNumerosSorteados.text.toString().trim()
            val numerosList =
                    numerosTexto
                            .split(Regex("[,;\\s-]+"))
                            .mapNotNull { it.toIntOrNull() }
                            .filter { it in 1..25 } // Filtra apenas números válidos entre 1 e 25
                            .distinct() // Remove duplicatas
                            .sorted() // Ordena

            // Valida a quantidade de números
            if (numerosList.size != 15) {
                // TODO: Adicionar string 'erro_quantidade_numeros_invalida' em strings.xml
                Toast.makeText(
                                requireContext(),
                                R.string.erro_quantidade_numeros_invalida,
                                Toast.LENGTH_SHORT
                        )
                        .show()
                return@setOnClickListener
            }

            // Salva o resultado
            viewModel.salvarResultado(numeroConcurso, numerosList, calendar.time)
            // TODO: Adicionar string 'resultado_salvo_com_sucesso' em strings.xml
            Toast.makeText(
                            requireContext(),
                            R.string.resultado_salvo_com_sucesso,
                            Toast.LENGTH_SHORT
                    )
                    .show()

            // Limpa os campos de entrada
            binding.editTextNumeroConcurso.text?.clear()
            binding.editTextNumerosSorteados.text?.clear()
        }

        // Seletor de data
        binding.buttonSelecionarData.setOnClickListener { mostrarSeletorData() }

        // Botão para conferir jogos
        binding.buttonConferirJogos.setOnClickListener { viewModel.conferirJogos() }
    }

    private fun mostrarSeletorData() {
        val datePickerDialog =
                DatePickerDialog(
                        requireContext(),
                        { _, year, month, dayOfMonth ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                            binding.textViewDataSelecionada.text =
                                    dateFormatter.format(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                )

        datePickerDialog.show()
    }

    private fun limparCamposResultado() {
        // TODO: Adicionar string 'concurso_nao_selecionado' em strings.xml
        binding.textViewConcursoNumero.text = getString(R.string.concurso_nao_selecionado)
        binding.textViewDataConcurso.text = "-"
        binding.textViewNumerosSorteados.text = "-"
        binding.buttonConferirJogos.isEnabled = false
    }

    private fun atualizarVisibilidadeResultadosAnteriores(listaVazia: Boolean) {
        if (listaVazia) {
            binding.recyclerViewResultadosAnteriores.visibility = View.GONE
            binding.textViewSemResultados.visibility = View.VISIBLE
        } else {
            binding.recyclerViewResultadosAnteriores.visibility = View.VISIBLE
            binding.textViewSemResultados.visibility = View.GONE
        }
    }

    private fun atualizarVisibilidadeJogosConferidos(listaVazia: Boolean) {
        if (listaVazia) {
            binding.recyclerViewJogosConferidos.visibility = View.GONE
            binding.textViewSemJogosConferidos.visibility = View.VISIBLE
        } else {
            binding.recyclerViewJogosConferidos.visibility = View.VISIBLE
            binding.textViewSemJogosConferidos.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
