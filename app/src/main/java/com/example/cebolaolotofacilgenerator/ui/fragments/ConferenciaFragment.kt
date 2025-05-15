package com.example.cebolaolotofacilgenerator.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.databinding.FragmentConferenciaBinding
import com.example.cebolaolotofacilgenerator.ui.adapters.ResultadosAdapter
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConferenciaViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

// Novas importações para Compose
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.cebolaolotofacilgenerator.ui.composables.ListaJogosConferidos
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme // Substitua pelo seu tema se o nome for diferente

class ConferenciaFragment : Fragment() {

    private var _binding: FragmentConferenciaBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ConferenciaViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
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

        binding.composeViewJogosConferidos.apply {
            // Dispose the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CebolaoLotofacilGeneratorTheme { // Use o tema do seu app
                    val jogosConferidos by viewModel.jogosConferidos.collectAsState()
                    val resultadoAtual by viewModel.resultadoAtual.collectAsState()

                    ListaJogosConferidos(
                        jogosConferidos = jogosConferidos,
                        resultadoSorteado = resultadoAtual,
                        mainViewModel = mainViewModel,
                        modifier = Modifier.fillMaxSize() // Opcional, ajuste conforme necessário
                    )
                }
            }
        }

        // Carrega o último resultado salvo, se houver
        viewModel.carregarUltimoResultado()
        viewModel.carregarTodosResultados() // Para popular a lista de resultados anteriores
    }

    private fun setupRecyclerViews() {
        // Adapter para resultados anteriores
        resultadosAdapter =
                ResultadosAdapter(
                        onResultadoClick = { resultado ->
                            viewModel.carregarResultado(resultado)
                            // Limpar a lista de jogos conferidos manualmente ao selecionar um novo resultado,
                            // pois o ViewModel já faz isso, mas para garantir que a UI reaja imediatamente
                            // se a composable não estiver observando algo que a force a recompor instantaneamente.
                            // Na verdade, o ViewModel já limpa `_jogosConferidos` em `carregarResultado`.
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
                                getString(R.string.concurso_numero_formatado, resultado.id)
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
                            mainViewModel.showSnackbar(getString(R.string.erro_conferir_jogos))
                        }
                        ConferenciaViewModel.StatusConferencia.OCIOSO -> {
                            binding.progressBarConferencia.visibility = View.GONE
                            binding.buttonConferirJogos.isEnabled =
                                    true // Ou false dependendo da lógica inicial
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        // Botão para salvar um novo resultado
        binding.buttonSalvarResultado.setOnClickListener {
            // Valida o número do concurso
            val numeroConcurso = binding.editTextNumeroConcurso.text.toString().toIntOrNull()
            if (numeroConcurso == null) {
                // TODO: Adicionar string 'erro_numero_concurso_invalido' em strings.xml
                mainViewModel.showSnackbar(getString(R.string.erro_numero_concurso_invalido))
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
                mainViewModel.showSnackbar(getString(R.string.erro_quantidade_numeros_invalida))
                return@setOnClickListener
            }

            // Salva o resultado
            viewModel.salvarResultado(numeroConcurso, numerosList, calendar.time)
            mainViewModel.showSnackbar(getString(R.string.resultado_salvo_com_sucesso))

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
