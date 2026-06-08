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

    override suspend fun getGames(limit: Int): ResponseService<List<Game>> =
        withContext(Dispatchers.IO) {
            try {
                // Pasamos la API KEY explícitamente desde BuildConfig
                val response = api.getGames(
                    apiKey = BuildConfig.RAWG_API_KEY,
                    pageSize = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        ResponseService.Success(body.results)
                    } else {
                        ResponseService.Error("La API devolvió una lista vacía")
                    }
                } else {
                    ResponseService.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                // Imprimimos el error en consola para debuggear mejor
                e.printStackTrace()
                ResponseService.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
}
