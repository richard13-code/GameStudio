package com.example.gamestudio.network

import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game

interface GameService {
    suspend fun getGames(page: Int, pageSize: Int, search: String? = null): ResponseService<List<Game>>
    suspend fun getGameById(id: Int): ResponseService<Game>
}