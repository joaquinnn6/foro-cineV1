package com.example.foro_cinev1.domain.models

// Modelo de datos del foro (cada post es una publicacion craeda por un usuario)
// Representa una publicación dentro del foro de cine
data class Post(
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val autor: String,
    val fecha: String
)

