package com.example.foro_cinev1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.repository.UserRepository
import com.example.foro_cinev1.domain.models.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = UserRepository()

    fun registrarUsuario(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exito = repository.registerUser(user)
            onResult(exito)
        }
    }

    fun iniciarSesion(correo: String, contrasena: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(correo, contrasena)
            onResult(user)
        }
    }
}
