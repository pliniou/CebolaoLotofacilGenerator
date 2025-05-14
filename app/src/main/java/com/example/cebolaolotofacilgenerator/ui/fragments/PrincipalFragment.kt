package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.databinding.FragmentPrincipalBinding
import com.example.cebolaolotofacilgenerator.model.common.OperacaoStatus
import com.example.cebolaolotofacilgenerator.ui.adapters.JogosAdapter
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.JogoViewModel

class PrincipalFragment : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding
        get() = _binding!!

    private val geradorViewModel: GeradorViewModel by viewModels()
    private val jogoViewModel: JogoViewModel by viewModels()
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
        geradorViewModel.operacaoStatus.value?.let { atualizarUIComBaseNoStatus(it) }
        geradorViewModel.jogosGerados.value?.let { atualizarVisibilidadeListaVazia(it.isEmpty()) }
    }

    private fun setupRecyclerView() {
        jogosAdapter =
                JogosAdapter(
                        onFavoritoClick = { jogo: Jogo, favorito: Boolean ->
                            jogoViewModel.marcarComoFavorito(jogo, favorito)
                        },
                        onJogoClick = { jogo: Jogo ->
                            Toast.makeText(
                                            requireContext(),
                                            "Jogo clicado: ${jogo.numeros.joinToString()}",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                )

        binding.rvJogosGerados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jogosAdapter
        }
    }

    private fun setupObservers() {
        geradorViewModel.jogosGerados.observe(viewLifecycleOwner) { jogos: List<Jogo>? ->
            jogos?.let {
                jogosAdapter.submitList(it)
                atualizarVisibilidadeListaVazia(it.isEmpty())
            }
        }

        geradorViewModel.operacaoStatus.observe(viewLifecycleOwner) { status: OperacaoStatus? ->
            status?.let { atualizarUIComBaseNoStatus(it) }
        }

        geradorViewModel.mensagem.observe(viewLifecycleOwner) { mensagem: String? ->
            mensagem?.let {
                if (geradorViewModel.operacaoStatus.value == OperacaoStatus.ERRO) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    geradorViewModel.limparMensagemUnica()
                }
            }
        }

        jogoViewModel.operacaoStatus.observe(viewLifecycleOwner) { status: OperacaoStatus? ->
            when (status) {
                OperacaoStatus.SUCESSO -> {
                    jogoViewModel.resetarStatus()
                }
                OperacaoStatus.ERRO -> {
                    Toast.makeText(requireContext(), "Erro ao salvar jogos.", Toast.LENGTH_SHORT)
                            .show()
                    jogoViewModel.resetarStatus()
                }
                else -> {
                    /* Não faz nada para CARREGANDO ou OCIOSO neste observer específico */
                }
            }
        }
    }

    private fun atualizarUIComBaseNoStatus(status: OperacaoStatus) {
        when (status) {
            OperacaoStatus.CARREGANDO -> {
                binding.progressGerador.visibility = View.VISIBLE
                binding.btnGerarJogos.isEnabled = false
            }
            OperacaoStatus.SUCESSO, OperacaoStatus.OCIOSO -> {
                binding.progressGerador.visibility = View.GONE
                binding.btnGerarJogos.isEnabled = true
            }
            OperacaoStatus.ERRO -> {
                binding.progressGerador.visibility = View.GONE
                binding.btnGerarJogos.isEnabled = true
            }
            else -> {
                binding.progressGerador.visibility = View.GONE
                binding.btnGerarJogos.isEnabled = true
            }
        }
    }

    private fun setupListeners() {
        binding.btnGerarJogos.setOnClickListener { geradorViewModel.gerarJogos() }

        binding.btnSalvarJogos.setOnClickListener {
            val jogos = geradorViewModel.jogosGerados.value
            if (jogos.isNullOrEmpty()) {
                Toast.makeText(
                                requireContext(),
                                R.string.nenhum_jogo_para_salvar,
                                Toast.LENGTH_SHORT
                        )
                        .show()
                return@setOnClickListener
            }
            jogoViewModel.inserirJogos(jogos)
            Toast.makeText(requireContext(), R.string.jogos_salvos_com_sucesso, Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun atualizarVisibilidadeListaVazia(listaVazia: Boolean) {
        if (listaVazia) {
            binding.rvJogosGerados.visibility = View.GONE
            binding.tvInfoJogosGerados.visibility = View.VISIBLE
            binding.btnSalvarJogos.isEnabled = false
        } else {
            binding.rvJogosGerados.visibility = View.VISIBLE
            binding.tvInfoJogosGerados.visibility = View.GONE
            binding.btnSalvarJogos.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
