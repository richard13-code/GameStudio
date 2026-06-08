package com.example.gamestudio.network

import com.example.gamestudio.BuildConfig
import com.example.gamestudio.model.GameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {

    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String = BuildConfig.RAWG_API_KEY,
        @Query("page_size") pageSize: Int? = null,
        @Query("search") search: String? = null
    ): Response<GameResponse>
}
