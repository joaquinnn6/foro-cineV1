package com.example.foro_cinev1.domain.models

data class News(
    val id: Int = 0,
    val titulo: String,
    val resumen: String,
    val contenido: String,
    val autor: String,
    val fecha: String
)
