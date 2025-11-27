package com.example.foro_cinev1.domain.models

data class User(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val role: String = "USER",             // 👈 NUEVO: rol del backend ("USER", "ADMIN")
    val ubicacion: String? = null,
    val profileImageUrl: String? = null
)
