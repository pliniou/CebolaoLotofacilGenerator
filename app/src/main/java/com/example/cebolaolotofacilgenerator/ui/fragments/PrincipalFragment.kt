package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.ui.adapters.JogosAdapter

class PrincipalFragment : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var jogosAdapter: JogosAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)
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
                JogosAdapter(
                        onFavoritoClick = { jogo, favorito ->
                            jogosViewModel.marcarComoFavorito(jogo, favorito)
                        },
                        onJogoClick = { jogo ->
                            // Exibir detalhes do jogo
                        }
                )

        binding.recyclerViewJogos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jogosAdapter
        }
    }

    private fun setupObservers() {
        // Observa os jogos gerados
        jogosViewModel.jogosGerados.observe(viewLifecycleOwner) { jogos ->
            jogosAdapter.submitList(jogos)
            atualizarVisibilidadeListaVazia(jogos.isEmpty())
        }

        // Observa o status da geração
        jogosViewModel.statusGeracao.observe(viewLifecycleOwner) { status ->
            when (status) {
                JogosViewModel.StatusGeracao.GERANDO -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonGerarJogos.isEnabled = false
                }
                JogosViewModel.StatusGeracao.CONCLUIDO -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGerarJogos.isEnabled = true
                }
                JogosViewModel.StatusGeracao.ERRO -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGerarJogos.isEnabled = true
                    Toast.makeText(requireContext(), R.string.erro_gerar_jogos, Toast.LENGTH_SHORT)
                            .show()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.buttonGerarJogos.setOnClickListener {
            val quantidadeJogos = binding.editTextQuantidadeJogos.text.toString().toIntOrNull() ?: 1
        }

        binding.buttonSalvarJogos.setOnClickListener {
            val jogos = jogosViewModel.jogosGerados.value
            if (jogos.isNullOrEmpty()) {
                Toast.makeText(
                                requireContext(),
                                R.string.nenhum_jogo_para_salvar,
                                Toast.LENGTH_SHORT
                        )
                        .show()
                return@setOnClickListener
            }

            jogosViewModel.salvarJogos(jogos)
            Toast.makeText(requireContext(), R.string.jogos_salvos_com_sucesso, Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun atualizarVisibilidadeListaVazia(listaVazia: Boolean) {
        if (listaVazia) {
            binding.recyclerViewJogos.visibility = View.GONE
            binding.textViewListaVazia.visibility = View.VISIBLE
            binding.buttonSalvarJogos.isEnabled = false
        } else {
            binding.recyclerViewJogos.visibility = View.VISIBLE
            binding.textViewListaVazia.visibility = View.GONE
            binding.buttonSalvarJogos.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
