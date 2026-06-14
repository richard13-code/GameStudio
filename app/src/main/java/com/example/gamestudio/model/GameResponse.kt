package com.example.gamestudio.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GameResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("results") val results: List<Game>
)

@Parcelize
data class Game(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("description_raw") val descriptionRaw: String? = null,
    @SerializedName("genres") val genres: List<Genre>? = null
) : Parcelable

@Parcelize
data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Parcelable

data class FavoriteGame(
    val id: Int = 0,
    val name: String = "",
    val backgroundImage: String? = null,
    val rating: Double = 0.0,
    val metacritic: Int? = null,
    val genres: String = ""
)