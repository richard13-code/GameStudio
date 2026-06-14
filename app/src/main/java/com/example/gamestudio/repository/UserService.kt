package com.example.gamestudio.repository

import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.onboarding.personal.model.UserProfile

interface UserService {
    suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit>
    suspend fun getUserInfo(uid: String): ResponseService<UserProfile>
}