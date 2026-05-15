package com.example.gamestudio.core

import com.example.gamestudio.onboarding.personal.model.UserProfile
import com.google.firebase.auth.FirebaseUser

interface Authentication {
    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun requestSignIn(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun saveUserProfile(profile: UserProfile): ResponseService<Unit>
}