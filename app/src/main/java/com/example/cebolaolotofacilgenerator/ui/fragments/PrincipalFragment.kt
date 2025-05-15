package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
import com.example.cebolaolotofacilgenerator.databinding.FragmentPrincipalBinding
import com.example.cebolaolotofacilgenerator.ui.adapters.JogosAdapter
import com.example.cebolaolotofacilgenerator.viewmodel.GeradorViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.JogoViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

class PrincipalFragment : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding
        get() = _binding!!

    private val geradorViewModel: GeradorViewModel by viewModels()
    private val jogoViewModel: JogoViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
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
        geradorViewModel.operacaoStatus.value?.let {
            atualizarUIComBaseNoStatus(
                    it as com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
            )
        }
        geradorViewModel.jogosGerados.value?.let { atualizarVisibilidadeListaVazia(it.isEmpty()) }
    }

    private fun setupRecyclerView() {
        jogosAdapter =
                JogosAdapter(
                        onFavoritoClick = { jogo: Jogo, favorito: Boolean ->
                            jogoViewModel.marcarComoFavorito(jogo, favorito)
                        },
                        onJogoClick = { jogo: Jogo ->
                            mainViewModel.showSnackbar("Jogo clicado: ${jogo.numeros.joinToString()}")
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

        geradorViewModel.operacaoStatus.observe(viewLifecycleOwner) {
                status: com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus? ->
            status?.let { atualizarUIComBaseNoStatus(it) }
        }

        geradorViewModel.mensagem.observe(viewLifecycleOwner) { mensagem: String? ->
            mensagem?.let {
                if (geradorViewModel.operacaoStatus.value ==
                                com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.ERRO
                ) {
                    mainViewModel.showSnackbar(it)
                    geradorViewModel.limparMensagemUnica()
                }
            }
        }

        jogoViewModel.operacaoStatus.observe(viewLifecycleOwner) {
                status: com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus? ->
            when (status) {
                com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.SUCESSO -> {
                    jogoViewModel.resetarStatus()
                }
                com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.ERRO -> {
                    mainViewModel.showSnackbar(getString(R.string.erro_salvar_jogos))
                    jogoViewModel.resetarStatus()
                }
                com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.CARREGANDO,
                com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.OCIOSO,
                null -> {
                    /* Não faz nada para CARREGANDO, OCIOSO ou null neste observer específico */
                }
            }
        }
    }

    private fun atualizarUIComBaseNoStatus(
            status: com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus
    ) {
        when (status) {
            com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.CARREGANDO -> {
                binding.progressGerador.visibility = View.VISIBLE
                binding.btnGerarJogos.isEnabled = false
            }
            com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.SUCESSO,
            com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.OCIOSO -> {
                binding.progressGerador.visibility = View.GONE
                binding.btnGerarJogos.isEnabled = true
            }
            com.example.cebolaolotofacilgenerator.data.model.OperacaoStatus.ERRO -> {
                binding.progressGerador.visibility = View.GONE
                binding.btnGerarJogos.isEnabled = true
            }
        // else não é necessário se o when for exaustivo com um tipo não anulável
        }
    }

    private fun setupListeners() {
        binding.btnGerarJogos.setOnClickListener { geradorViewModel.gerarJogos() }

        binding.btnSalvarJogos.setOnClickListener {
            val jogos = geradorViewModel.jogosGerados.value
            if (jogos.isNullOrEmpty()) {
                mainViewModel.showSnackbar(getString(R.string.nenhum_jogo_para_salvar))
                return@setOnClickListener
            }
            jogoViewModel.inserirJogos(jogos)
            mainViewModel.showSnackbar(getString(R.string.jogos_salvos_com_sucesso))
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
