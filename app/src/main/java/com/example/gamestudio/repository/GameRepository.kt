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

    override suspend fun getGames(limit: Int, search: String?): ResponseService<List<Game>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getGames(
                    apiKey = BuildConfig.RAWG_API_KEY,
                    pageSize = limit,
                    search = search?.takeIf { it.isNotBlank() }
                )
                when {
                    response.isSuccessful -> {
                        val body = response.body()
                        if (body != null) ResponseService.Success(body.results)
                        else ResponseService.Error("La API devolvió una lista vacía")
                    }
                    response.code() == 401 ->
                        ResponseService.Error("API Key inválida (401). Revisa tu local.properties")
                    response.code() == 404 ->
                        ResponseService.Error("Recurso no encontrado (404)")
                    response.code() == 429 ->
                        ResponseService.Error("Límite de peticiones excedido (429). Espera un momento")
                    else ->
                        ResponseService.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: java.net.UnknownHostException) {
                ResponseService.Error("Sin conexión a internet. Verifica tu red")
            } catch (e: java.net.SocketTimeoutException) {
                ResponseService.Error("Tiempo de espera agotado. Intenta de nuevo")
            } catch (e: Exception) {
                ResponseService.Error("Error inesperado: ${e.localizedMessage}")
            }
        }

    override suspend fun getGameById(id: Int): ResponseService<Game> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getGameById(id = id, apiKey = BuildConfig.RAWG_API_KEY)
                when {
                    response.isSuccessful -> {
                        val game = response.body()
                        if (game != null) ResponseService.Success(game)
                        else ResponseService.Error("No se encontró información del juego")
                    }
                    response.code() == 404 ->
                        ResponseService.Error("Juego no encontrado (404)")
                    response.code() == 429 ->
                        ResponseService.Error("Límite de peticiones excedido. Espera un momento")
                    else ->
                        ResponseService.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: java.net.UnknownHostException) {
                ResponseService.Error("Sin conexión a internet")
            } catch (e: Exception) {
                ResponseService.Error("Error: ${e.localizedMessage}")
            }
        }
}