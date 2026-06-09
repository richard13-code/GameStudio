package com.example.gamestudio.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.AuthRepository
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.onboarding.personal.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _userProfileState = MutableStateFlow<ResponseService<UserProfile>?>(null)
    val userProfileState: StateFlow<ResponseService<UserProfile>?> =
        _userProfileState.asStateFlow()

    fun fetchUserProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _userProfileState.value = ResponseService.Error("No hay sesión activa")
            return
        }
        viewModelScope.launch {
            _userProfileState.value = ResponseService.Loading
            _userProfileState.value = authRepository.getUserProfile(uid)
        }
    }
}