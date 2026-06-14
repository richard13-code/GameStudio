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

    private var currentPage = 1
    private var currentSearch: String? = null
    private var searchJob: Job? = null

    fun loadGames() {
        viewModelScope.launch {
            _gameState.value = ResponseService.Loading
            _gameState.value = service.getGames(
                page = currentPage,
                pageSize = 20,
                search = currentSearch
            )
        }
    }

    fun searchGames(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            currentSearch = null
            currentPage = 1
            loadGames()
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            currentSearch = query
            currentPage = 1
            _gameState.value = ResponseService.Loading
            _gameState.value = service.getGames(page = 1, pageSize = 20, search = query)
        }
    }

    fun nextPage() {
        currentPage++
        loadGames()
    }

    fun previousPage() {
        if (currentPage > 1) {
            currentPage--
            loadGames()
        }
    }

    fun getCurrentPage(): Int = currentPage
}