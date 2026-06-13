package com.example.gamestudio.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gamestudio.core.FragmentCommunicator
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var communicator: FragmentCommunicator

    private val adapter = GameAdapter { game ->
        // Handle game click
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGames.adapter = adapter

        // Configuramos las acciones de los nuevos botones de paginación
        setupPaginationButtons()

        observeState()
        viewModel.loadGames()
        return binding.root
    }

    // NUEVA FUNCIÓN: Conecta tus botones con las funciones de tu HomeViewModel
    private fun setupPaginationButtons() {
        binding.btnSiguiente.setOnClickListener {
            viewModel.nextPage()
        }

        binding.btnAnterior.setOnClickListener {
            viewModel.previousPage()
        }
    }

    fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            // Deshabilitamos los botones durante la carga para evitar clics dobles ruidosos
                            binding.btnSiguiente.isEnabled = false
                            binding.btnAnterior.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)

                            // Reactivamos los botones al terminar la carga
                            binding.btnSiguiente.isEnabled = true
                            // El botón anterior solo se activa si la página actual es mayor a 1
                            binding.btnAnterior.isEnabled = viewModel.getCurrentPage() > 1

                            // Se muestra la lista completa de la API para que el scroll funcione
                            adapter.submitList(state.data)

                            // Regresa el scroll al inicio de la cuadrícula para ver los nuevos juegos desde arriba
                            binding.rvGames.post {
                                // Buscamos el NestedScrollView que envuelve a la lista y lo subimos al inicio (0,0)
                                val nestedScroll = binding.rvGames.parent.parent as? androidx.core.widget.NestedScrollView
                                nestedScroll?.scrollTo(0, 0)
                            }
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)

                            // En caso de error, reactivamos según la página en la que nos quedamos
                            binding.btnSiguiente.isEnabled = true
                            binding.btnAnterior.isEnabled = viewModel.getCurrentPage() > 1

                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}