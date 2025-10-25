package com.example.foro_cinev1.domain.models

data class Comment(
    val id: Int = 0,
    val postId: Int,
    val autor: String,
    val contenido: String,
    val fecha: String
)
