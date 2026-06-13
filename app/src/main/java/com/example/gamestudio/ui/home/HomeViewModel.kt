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

    // 1. Guardamos la página actual en el ViewModel (empieza en la 1)
    private var currentPage = 1

    // 2. Modificamos la función para que use la página actual
    fun loadGames() {
        viewModelScope.launch {
            _gameState.value = ResponseService.Loading

            // Aquí le pasas la página actual a tu servicio en lugar del límite
            // (Asegúrate de que tu GameRepository acepte el parámetro 'page')
            val result = service.getGames(page = currentPage, pageSize = 20)
            _gameState.value = result
        }
    }

    // 3. Función para el botón "Siguiente"
    fun nextPage() {
        currentPage++
        loadGames() // Vuelve a pedir los datos a la API pero con la nueva página
    }

    // 4. Función para el botón "Anterior"
    fun previousPage() {
        if (currentPage > 1) {
            currentPage--
            loadGames()
        }
    }

    // Opcional por si necesitas saber en qué página vas en la interfaz
    fun getCurrentPage(): Int = currentPage
}
