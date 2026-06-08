package com.example.gamestudio.repository

import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.Game
import com.example.gamestudio.network.ApiClient
import com.example.gamestudio.network.GameService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository : GameService {

    // CORRECCIÓN: Usar gameApi (en minúscula) para coincidir con el cambio en ApiClient
    private val api = ApiClient.gameApi

    override suspend fun getGames(limit: Int): ResponseService<List<Game>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getGames(
                    pageSize = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        ResponseService.Success(body.results)
                    } else {
                        ResponseService.Error("Respuesta vacía del servidor")
                    }
                } else {
                    ResponseService.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ResponseService.Error(
                    "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
}
