package com.example.gamestudio.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gamestudio.R
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentGameDetailBinding
import com.example.gamestudio.model.Game
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GameDetailFragment : Fragment() {

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GameDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameId = arguments?.getInt("gameId") ?: run {
            findNavController().navigateUp()
            return
        }

        setupListeners()
        observeViewModel()
        viewModel.loadGameDetail(gameId)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnFavorite.setOnClickListener {
            val currentGame = (viewModel.gameDetailState.value as? ResponseService.Success)?.data
            if (currentGame != null) viewModel.toggleFavorite(currentGame)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.gameDetailState.collect { state ->
                        when (state) {
                            is ResponseService.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.contentGroup.visibility = View.GONE
                            }
                            is ResponseService.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.contentGroup.visibility = View.VISIBLE
                                bindGameDetail(state.data)
                            }
                            is ResponseService.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                            }
                            null -> {}
                        }
                    }
                }

                launch {
                    viewModel.isFavorite.collect { isFav ->
                        binding.btnFavorite.setImageResource(
                            if (isFav) R.drawable.ic_favorite_filled
                            else R.drawable.ic_favorite_border
                        )
                    }
                }

                launch {
                    viewModel.favoriteAction.collect { action ->
                        when (action) {
                            is ResponseService.Success -> {
                                val msg = if (viewModel.isFavorite.value)
                                    "✓ Juego guardado en favoritos"
                                else "Juego eliminado de favoritos"
                                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                            }
                            is ResponseService.Error ->
                                Snackbar.make(binding.root, action.error, Snackbar.LENGTH_LONG).show()
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun bindGameDetail(game: Game) {
        binding.tvGameTitle.text = game.name
        binding.tvReleased.text = "Lanzamiento: ${game.released ?: "Fecha desconocida"}"
        binding.tvRating.text = "⭐ ${game.rating}"
        binding.tvMetacritic.text =
            if (game.metacritic != null) "Metacritic: ${game.metacritic}" else ""
        binding.tvGenres.text = game.genres?.joinToString(", ") { it.name } ?: ""
        binding.tvDescription.text = game.descriptionRaw ?: "Sin descripción disponible"

        Glide.with(binding.ivCover)
            .load(game.backgroundImage)
            .placeholder(R.drawable.ic_launcher_background)
            .centerCrop()
            .into(binding.ivCover)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}