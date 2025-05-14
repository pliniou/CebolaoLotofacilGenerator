package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.databinding.FragmentGerenciamentoJogosBinding
import com.example.cebolaolotofacilgenerator.ui.adapters.JogosGerenciamentoAdapter
import com.example.cebolaolotofacilgenerator.viewmodel.JogosViewModel
import com.google.android.material.tabs.TabLayout

class GerenciamentoJogosFragment : Fragment() {

    private var _binding: FragmentGerenciamentoJogosBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: JogosViewModel by viewModels()
    private lateinit var jogosAdapter: JogosGerenciamentoAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGerenciamentoJogosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        jogosAdapter =
                JogosGerenciamentoAdapter(
                        onFavoritoClick = { jogo: Jogo, favorito: Boolean ->
                            viewModel.marcarComoFavorito(jogo, favorito)
                        },
                        onExcluirClick = { jogo: Jogo -> confirmarExclusao(jogo) },
                        onJogoClick = { jogo: Jogo -> exibirDetalhesJogo(jogo) }
                )

        binding.recyclerViewJogos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jogosAdapter
        }
    }

    private fun setupObservers() {
        // Observa todos os jogos por padrão
        viewModel.todosJogos.observe(viewLifecycleOwner) { jogos: List<Jogo>? ->
            jogos?.let {
                jogosAdapter.submitList(it)
                atualizarVisibilidadeListaVazia(it.isEmpty())
            }
        }
    }

    private fun setupListeners() {
        // Configura as abas (todos, favoritos, conferidos)
        binding.tabLayout.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            0 -> { // Todos os jogos
                                viewModel.todosJogos.observe(viewLifecycleOwner) {
                                        jogos: List<Jogo>? ->
                                    jogos?.let {
                                        jogosAdapter.submitList(it)
                                        atualizarVisibilidadeListaVazia(it.isEmpty())
                                    }
                                }
                            }
                            1 -> { // Favoritos
                                viewModel.jogosFavoritos.observe(viewLifecycleOwner) {
                                        jogos: List<Jogo>? ->
                                    jogos?.let {
                                        jogosAdapter.submitList(it)
                                        atualizarVisibilidadeListaVazia(it.isEmpty())
                                    }
                                }
                            }
                            2 -> { // Conferidos
                                viewModel.jogosConferidos.observe(viewLifecycleOwner) {
                                        jogos: List<Jogo>? ->
                                    jogos?.let {
                                        jogosAdapter.submitList(it)
                                        atualizarVisibilidadeListaVazia(it.isEmpty())
                                    }
                                }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
        )

        // Botão para excluir todos os jogos
        binding.buttonLimparTodos.setOnClickListener { _ -> confirmarLimparTodos() }
    }

    private fun atualizarVisibilidadeListaVazia(listaVazia: Boolean) {
        if (listaVazia) {
            binding.recyclerViewJogos.visibility = View.GONE
            binding.textViewListaVazia.visibility = View.VISIBLE
        } else {
            binding.recyclerViewJogos.visibility = View.VISIBLE
            binding.textViewListaVazia.visibility = View.GONE
        }
    }

    private fun confirmarExclusao(jogo: Jogo) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirmar_exclusao_titulo)
                .setMessage(
                        getString(
                                R.string.mensagem_confirmar_exclusao_jogo,
                                jogo.numeros.joinToString(" - ")
                        )
                )
                .setPositiveButton(R.string.excluir_jogo) { _, _ ->
                    viewModel.deletarJogo(jogo)
                    Toast.makeText(requireContext(), R.string.jogo_excluido, Toast.LENGTH_SHORT)
                            .show()
                }
                .setNegativeButton(R.string.cancelar, null)
                .show()
    }

    private fun confirmarLimparTodos() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirmar_limpar_todos)
                .setMessage(R.string.mensagem_confirmar_limpar_todos)
                .setPositiveButton(R.string.sim) { _, _ ->
                    viewModel.limparTodosJogos()
                    Toast.makeText(
                                    requireContext(),
                                    R.string.todos_jogos_excluidos,
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                }
                .setNegativeButton(R.string.nao, null)
                .show()
    }

    private fun exibirDetalhesJogo(jogo: Jogo) {
        val mensagem =
                StringBuilder()
                        .append(
                                getString(
                                        R.string.numeros_jogados,
                                        jogo.numeros.joinToString(" - ")
                                )
                        )
                        .append("\n\n")
                        .append(
                                getString(
                                        R.string.data_geracao,
                                        android.text.format.DateFormat.format(
                                                "dd/MM/yyyy HH:mm",
                                                jogo.dataCriacao
                                        )
                                )
                        )
                        .append("\n\n")
                        .append(getString(R.string.caracteristicas_jogo))
                        .append("\n")
                        .append(
                                getString(
                                        R.string.pares_impares,
                                        jogo.quantidadePares,
                                        jogo.quantidadeImpares
                                )
                        )
                        .append("\n")
                        .append(getString(R.string.soma_total, jogo.soma))
                        .append("\n")
                        .append(getString(R.string.numeros_primos, jogo.quantidadePrimos))
                        .append("\n")
                        .append(getString(R.string.numeros_fibonacci, jogo.quantidadeFibonacci))
                        .append("\n")
                        .append(
                                getString(
                                        R.string.miolo_moldura,
                                        jogo.quantidadeMiolo,
                                        jogo.quantidadeMoldura
                                )
                        )
                        .append("\n")
                        .append(getString(R.string.multiplos_tres, jogo.quantidadeMultiplosDeTres))

        if (jogo.acertos != null) {
            mensagem.append("\n\n").append(getString(R.string.resultado_conferencia, jogo.acertos))
        }

        AlertDialog.Builder(requireContext())
                .setTitle(R.string.detalhes_do_jogo)
                .setMessage(mensagem.toString())
                .setPositiveButton(R.string.fechar, null)
                .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
