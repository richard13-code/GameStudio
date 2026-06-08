package com.example.gamestudio.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.AuthRepository
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.onboarding.personal.model.UserProfile
import com.example.gamestudio.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _userProfileState = MutableStateFlow<ResponseService<UserProfile>?>(null)
    val userProfileState: StateFlow<ResponseService<UserProfile>?> = _userProfileState.asStateFlow()

    fun fetchUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                _userProfileState.value = ResponseService.Loading
                val result = userRepository.getUserInfo(currentUser.uid)
                _userProfileState.value = result
            }
        } else {
            _userProfileState.value = ResponseService.Error("No hay sesión activa")
        }
    }
}