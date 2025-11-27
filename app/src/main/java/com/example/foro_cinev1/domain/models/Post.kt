package com.example.foro_cinev1.domain.models

data class Post(
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val autor: String,
    val userId: Int = 0,   // 👈 NUEVO: id real del creador
    val fecha: String,
    val likes: Int = 0,
    val dislikes: Int = 0
)
