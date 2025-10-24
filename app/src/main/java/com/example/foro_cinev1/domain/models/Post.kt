package com.example.foro_cinev1.domain.models

data class Post(
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val autor: String,
    val fecha: String
)