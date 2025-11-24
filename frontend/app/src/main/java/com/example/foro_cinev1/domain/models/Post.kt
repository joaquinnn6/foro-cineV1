package com.example.foro_cinev1.domain.models

data class Post(
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val autor: String,
    val fecha: String,
    val likes: Int = 0,
    val dislikes: Int = 0,
    var userVote: Int = 0
)
