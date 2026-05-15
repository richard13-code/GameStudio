package com.example.gamestudio.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.AuthRepository
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.onboarding.personal.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _userProfile = MutableLiveData<ResponseService<UserProfile>>()
    val userProfile: LiveData<ResponseService<UserProfile>> = _userProfile

    private val _updateStatus = MutableLiveData<ResponseService<Unit>>()
    val updateStatus: LiveData<ResponseService<Unit>> = _updateStatus

    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _userProfile.value = ResponseService.Loading
            val result = repository.getUserProfile(userId)
            _userProfile.value = result
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isBlank()) return
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _updateStatus.value = ResponseService.Loading
            val result = repository.updateUsername(userId, newUsername)
            _updateStatus.value = result
        }
    }
}