package com.example.gamestudio.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.FavoriteGame
import com.example.gamestudio.model.Game
import com.example.gamestudio.repository.FavoritesRepository
import com.example.gamestudio.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailViewModel : ViewModel() {

    private val gameRepository = GameRepository()
    private val favoritesRepository = FavoritesRepository()

    private val _gameDetailState = MutableStateFlow<ResponseService<Game>?>(null)
    val gameDetailState: StateFlow<ResponseService<Game>?> = _gameDetailState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _favoriteAction = MutableStateFlow<ResponseService<Unit>?>(null)
    val favoriteAction: StateFlow<ResponseService<Unit>?> = _favoriteAction.asStateFlow()

    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _gameDetailState.value = ResponseService.Loading
            _gameDetailState.value = gameRepository.getGameById(gameId)
            _isFavorite.value = favoritesRepository.isFavorite(gameId)
        }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                val result = favoritesRepository.removeFavorite(game.id)
                if (result is ResponseService.Success) _isFavorite.value = false
                _favoriteAction.value = result
            } else {
                val favorite = FavoriteGame(
                    id = game.id,
                    name = game.name,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    metacritic = game.metacritic,
                    genres = game.genres?.joinToString(", ") { it.name } ?: ""
                )
                val result = favoritesRepository.addFavorite(favorite)
                if (result is ResponseService.Success) _isFavorite.value = true
                _favoriteAction.value = result
            }
        }
    }
}