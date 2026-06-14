package com.example.gamestudio.repository

import com.example.gamestudio.BuildConfig
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game
import com.example.gamestudio.network.ApiClient
import com.example.gamestudio.network.GameService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository : GameService {

    private val api = ApiClient.gameApi

    override suspend fun getGames(
        page: Int,
        pageSize: Int,
        search: String?
    ): ResponseService<List<Game>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGames(
                apiKey = BuildConfig.RAWG_API_KEY,
                page = page,
                pageSize = pageSize,
                search = search?.takeIf { it.isNotBlank() }
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ResponseService.Success(body.results)
                else ResponseService.Error("La API devolvió una lista vacía")
            } else {
                ResponseService.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseService.Error("Error de conexión: ${e.localizedMessage}")
        }
    }

    override suspend fun getGameById(id: Int): ResponseService<Game> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getGameById(id = id, apiKey = BuildConfig.RAWG_API_KEY)
                if (response.isSuccessful) {
                    val game = response.body()
                    if (game != null) ResponseService.Success(game)
                    else ResponseService.Error("Juego no encontrado")
                } else {
                    ResponseService.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                ResponseService.Error("Error: ${e.localizedMessage}")
            }
        }
}