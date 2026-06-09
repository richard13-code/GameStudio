package com.example.gamestudio.network

import com.example.gamestudio.BuildConfig
import com.example.gamestudio.model.Game
import com.example.gamestudio.model.GameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String = BuildConfig.RAWG_API_KEY,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null
    ): Response<GameResponse>

    @GET("games/{id}")
    suspend fun getGameById(
        @Path("id") id: Int,
        @Query("key") apiKey: String = BuildConfig.RAWG_API_KEY
    ): Response<Game>
}
