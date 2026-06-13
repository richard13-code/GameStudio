package com.example.gamestudio.network

import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game

interface GameService {

    // Cambiamos 'limit' por los parámetros reales de paginación de RAWG
    suspend fun getGames(
        page: Int = 1,
        pageSize: Int = 20
    ): ResponseService<List<Game>>
}