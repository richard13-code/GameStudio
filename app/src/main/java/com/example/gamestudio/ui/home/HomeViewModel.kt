package com.example.gamestudio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game
import com.example.gamestudio.network.GameService
import com.example.gamestudio.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val service: GameService = GameRepository()
): ViewModel() {


    private val _gameState = MutableStateFlow<ResponseService<List<Game>>?>(null)
    val gameState: StateFlow<ResponseService<List<Game>>?> = _gameState.asStateFlow()

    fun loadGames(limit: Int = 20) {
        viewModelScope.launch {
            _gameState.value = ResponseService.Loading
            val result = service.getGames(limit)
            _gameState.value = result
        }
    }
}
