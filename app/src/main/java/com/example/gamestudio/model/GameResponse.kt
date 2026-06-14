package com.example.gamestudio.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GameResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("results") val results: List<Game>
)

data class Game(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("description_raw") val descriptionRaw: String? = null,
    @SerializedName("genres") val genres: List<Genre>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(Genre.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(released)
        parcel.writeString(backgroundImage)
        parcel.writeDouble(rating)
        parcel.writeValue(metacritic)
        parcel.writeString(descriptionRaw)
        parcel.writeTypedList(genres)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game = Game(parcel)
        override fun newArray(size: Int): Array<Game?> = arrayOfNulls(size)
    }
}

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Genre> {
        override fun createFromParcel(parcel: Parcel): Genre = Genre(parcel)
        override fun newArray(size: Int): Array<Genre?> = arrayOfNulls(size)
    }
}

data class FavoriteGame(
    val id: Int = 0,
    val name: String = "",
    val backgroundImage: String? = null,
    val rating: Double = 0.0,
    val metacritic: Int? = null,
    val genres: String = ""
)