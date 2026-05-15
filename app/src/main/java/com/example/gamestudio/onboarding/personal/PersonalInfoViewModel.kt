package com.example.gamestudio.onboarding.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestudio.core.AuthRepository
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.onboarding.personal.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _saveStatus = MutableLiveData<ResponseService<Unit>>()
    val saveStatus: LiveData<ResponseService<Unit>> = _saveStatus

    fun saveProfile(
        firstName: String,
        lastName: String,
        userName: String,
        phone: String,
        birthDate: String
    ) {
        if (firstName.isBlank() || lastName.isBlank() || userName.isBlank() || phone.isBlank() || birthDate.isBlank()) {
            _saveStatus.value = ResponseService.Error("Todos los campos son obligatorios")
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            _saveStatus.value = ResponseService.Error("No se encontró sesión de usuario")
            return
        }

        val profile = UserProfile(
            id = userId,
            firstName = firstName,
            lastName = lastName,
            userName = userName,
            phone = phone,
            birthDate = birthDate
        )

        viewModelScope.launch {
            _saveStatus.value = ResponseService.Loading
            val result = repository.saveUserProfile(profile)
            _saveStatus.value = result
        }
    }
}