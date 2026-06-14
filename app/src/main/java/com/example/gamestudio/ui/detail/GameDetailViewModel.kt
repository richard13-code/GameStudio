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

    private val _detailState = MutableStateFlow<ResponseService<Game>?>(null)
    val detailState: StateFlow<ResponseService<Game>?> = _detailState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _favoriteMsg = MutableStateFlow<String?>(null)
    val favoriteMsg: StateFlow<String?> = _favoriteMsg.asStateFlow()

    fun loadDetail(gameId: Int) {
        viewModelScope.launch {
            _detailState.value = ResponseService.Loading
            _detailState.value = gameRepository.getGameById(gameId)
            _isFavorite.value = favoritesRepository.isFavorite(gameId)
        }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                val result = favoritesRepository.removeFavorite(game.id)
                if (result is ResponseService.Success) {
                    _isFavorite.value = false
                    _favoriteMsg.value = "Eliminado de favoritos"
                } else if (result is ResponseService.Error) {
                    _favoriteMsg.value = result.error
                }
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
                if (result is ResponseService.Success) {
                    _isFavorite.value = true
                    _favoriteMsg.value = "Guardado en favoritos ❤"
                } else if (result is ResponseService.Error) {
                    _favoriteMsg.value = result.error
                }
            }
        }
    }

    fun clearMsg() {
        _favoriteMsg.value = null
    }
}