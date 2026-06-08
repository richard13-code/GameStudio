package com.example.gamestudio.network

import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game

interface GameService {

    suspend fun getGames (limit: Int= 20): ResponseService<List<Game>>
}