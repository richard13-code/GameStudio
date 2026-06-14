package com.example.gamestudio.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gamestudio.R
import com.example.gamestudio.core.FragmentCommunicator
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentHomeBinding
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private val adapter = GameAdapter { game ->
        val bundle = bundleOf("gameId" to game.id)
        findNavController().navigate(R.id.action_homeFragment_to_gameDetailFragment, bundle)
    }

    private val searchAdapter = GameAdapter { game ->
        val bundle = bundleOf("gameId" to game.id)
        findNavController().navigate(R.id.action_homeFragment_to_gameDetailFragment, bundle)
        binding.searchView.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGames.adapter = adapter

        binding.rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSearchResults.adapter = searchAdapter

        setupSearch()
        setupPaginationButtons()
        observeState()
        viewModel.loadGames()
        return binding.root
    }

    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                viewModel.searchGames("")
            }
        }

        binding.searchView.editText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.searchGames(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })
    }

    private fun setupPaginationButtons() {
        binding.btnSiguiente.setOnClickListener { viewModel.nextPage() }
        binding.btnAnterior.setOnClickListener { viewModel.previousPage() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnSiguiente.isEnabled = false
                            binding.btnAnterior.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            binding.btnSiguiente.isEnabled = true
                            binding.btnAnterior.isEnabled = viewModel.getCurrentPage() > 1
                            adapter.submitList(state.data)
                            searchAdapter.submitList(state.data)
                            binding.rvGames.post {
                                val nestedScroll = binding.rvGames.parent.parent
                                        as? androidx.core.widget.NestedScrollView
                                nestedScroll?.scrollTo(0, 0)
                            }
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
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