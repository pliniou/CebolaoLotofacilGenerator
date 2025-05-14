package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.AppDataStore
import com.example.cebolaolotofacilgenerator.databinding.FragmentConfiguracoesBinding
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConfiguracoesViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.ConfiguracoesViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfiguracoesFragment : Fragment() {

    private var _binding: FragmentConfiguracoesBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ConfiguracoesViewModel by viewModels {
        ConfiguracoesViewModelFactory(AppDataStore(requireContext().applicationContext))
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.temaAtual.observe(viewLifecycleOwner) { tema -> atualizarDescricaoTema(tema) }
    }

    private fun setupListeners() {
        binding.cardViewTema.setOnClickListener { mostrarDialogoSelecionarTema() }

        binding.cardViewSobre.setOnClickListener { mostrarDialogoSobre() }
    }

    private fun atualizarDescricaoTema(tema: ConfiguracoesViewModel.TemaAplicativo) {
        val textoTema =
                when (tema) {
                    ConfiguracoesViewModel.TemaAplicativo.CLARO -> getString(R.string.tema_claro)
                    ConfiguracoesViewModel.TemaAplicativo.ESCURO -> getString(R.string.tema_escuro)
                    ConfiguracoesViewModel.TemaAplicativo.SISTEMA ->
                            getString(R.string.tema_sistema)
                }

        binding.textViewTemaAtual.text = textoTema
    }

    private fun mostrarDialogoSelecionarTema() {
        val temas = ConfiguracoesViewModel.TemaAplicativo.values()
        val nomeTemas =
                temas
                        .map {
                            when (it) {
                                ConfiguracoesViewModel.TemaAplicativo.CLARO ->
                                        getString(R.string.tema_claro)
                                ConfiguracoesViewModel.TemaAplicativo.ESCURO ->
                                        getString(R.string.tema_escuro)
                                ConfiguracoesViewModel.TemaAplicativo.SISTEMA ->
                                        getString(R.string.tema_sistema)
                            }
                        }
                        .toTypedArray()

        val temaAtual = viewModel.temaAtual.value ?: ConfiguracoesViewModel.TemaAplicativo.SISTEMA
        val posicaoSelecionada = temas.indexOf(temaAtual)

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.selecionar_tema)
                .setSingleChoiceItems(nomeTemas, posicaoSelecionada) { dialog, posicao ->
                    val novoTema = temas[posicao]
                    viewModel.alterarTema(novoTema)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancelar, null)
                .show()
    }

    private fun mostrarDialogoSobre() {
        val mensagem =
                StringBuilder()
                        .append(getString(R.string.versao_app, getString(R.string.app_version)))
                        .append("\n\n")
                        .append(getString(R.string.descricao_app))
                        .append("\n\n")
                        .append(getString(R.string.desenvolvido_por))

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sobre_o_app)
                .setMessage(mensagem)
                .setPositiveButton(R.string.fechar, null)
                .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
