package com.example.foro_cinev1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.repository.UserRepository
import com.example.foro_cinev1.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(context: Context) : ViewModel() {
    private val repository = UserRepository(context)

    private val _usuarioActual = MutableStateFlow<User?>(null)
    val usuarioActual: StateFlow<User?> = _usuarioActual

    fun registrarUsuario(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exito = repository.registerUser(user)
            onResult(exito)
        }
    }

    fun iniciarSesion(correo: String, contraseña: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(correo, contraseña)
            _usuarioActual.value = user
            onResult(user)
        }
    }
}
