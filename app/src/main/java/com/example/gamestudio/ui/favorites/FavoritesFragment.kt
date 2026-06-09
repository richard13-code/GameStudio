package com.example.gamestudio.ui.favorites

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestudio.R
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentFavoritesBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FavoritesViewModel>()

    private val adapter = FavoritesAdapter { favorite ->
        val bundle = bundleOf("gameId" to favorite.id)
        findNavController().navigate(R.id.action_favoritesFragment_to_gameDetailFragment, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
        observeState()
        viewModel.loadFavorites()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvFavorites.visibility = View.GONE
                            binding.emptyStateLayout.visibility = View.GONE
                        }
                        is ResponseService.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (state.data.isEmpty()) {
                                binding.rvFavorites.visibility = View.GONE
                                binding.emptyStateLayout.visibility = View.VISIBLE
                            } else {
                                binding.rvFavorites.visibility = View.VISIBLE
                                binding.emptyStateLayout.visibility = View.GONE
                                adapter.submitList(state.data)
                            }
                        }
                        is ResponseService.Error -> {
                            binding.progressBar.visibility = View.GONE
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