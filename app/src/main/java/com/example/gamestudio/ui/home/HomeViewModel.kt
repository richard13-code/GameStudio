package com.example.gamestudio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game
import com.example.gamestudio.network.GameService
import com.example.gamestudio.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val service: GameService = GameRepository()
) : ViewModel() {

    private val _gameState = MutableStateFlow<ResponseService<List<Game>>?>(null)
    val gameState: StateFlow<ResponseService<List<Game>>?> = _gameState.asStateFlow()

    private var searchJob: Job? = null

    fun loadGames(limit: Int = 20) {
        viewModelScope.launch {
            _gameState.value = ResponseService.Loading
            _gameState.value = service.getGames(limit = limit, search = null)
        }
    }

    fun searchGames(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            loadGames()
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _gameState.value = ResponseService.Loading
            _gameState.value = service.getGames(limit = 20, search = query)
        }
    }
}