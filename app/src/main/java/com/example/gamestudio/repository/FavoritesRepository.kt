package com.example.gamestudio.repository

import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.model.FavoriteGame
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Colección: favoritos/{userId}/games/{gameId}
    private fun favoritesCollection(userId: String) =
        firestore.collection("favoritos").document(userId).collection("games")

    fun getFavoritesFlow(): Flow<ResponseService<List<FavoriteGame>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(ResponseService.Error("No hay sesión activa"))
            close()
            return@callbackFlow
        }
        trySend(ResponseService.Loading)
        val listener = favoritesCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResponseService.Error("Error: ${error.message}"))
                    return@addSnapshotListener
                }
                val games = snapshot?.documents?.mapNotNull {
                    it.toObject(FavoriteGame::class.java)
                } ?: emptyList()
                trySend(ResponseService.Success(games))
            }
        awaitClose { listener.remove() }
    }

    suspend fun addFavorite(game: FavoriteGame): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid
                    ?: return@withContext ResponseService.Error("No hay sesión activa")
                favoritesCollection(userId)
                    .document(game.id.toString())
                    .set(game)
                    .await()
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("Error al guardar: ${e.message}")
            }
        }

    suspend fun removeFavorite(gameId: Int): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid
                    ?: return@withContext ResponseService.Error("No hay sesión activa")
                favoritesCollection(userId)
                    .document(gameId.toString())
                    .delete()
                    .await()
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("Error al eliminar: ${e.message}")
            }
        }

    suspend fun isFavorite(gameId: Int): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val doc = favoritesCollection(userId).document(gameId.toString()).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}