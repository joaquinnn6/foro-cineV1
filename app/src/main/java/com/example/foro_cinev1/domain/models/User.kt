package com.example.foro_cinev1.domain.models

data class User(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contraseña: String,
    val ubicacion: String? = null
)
