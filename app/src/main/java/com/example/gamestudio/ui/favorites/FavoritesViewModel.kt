package com.example.gamestudio.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.FavoriteGame
import com.example.gamestudio.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val repository = FavoritesRepository()

    private val _favoritesState = MutableStateFlow<ResponseService<List<FavoriteGame>>?>(null)
    val favoritesState: StateFlow<ResponseService<List<FavoriteGame>>?> =
        _favoritesState.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoritesFlow().collect { state ->
                _favoritesState.value = state
            }
        }
    }
}