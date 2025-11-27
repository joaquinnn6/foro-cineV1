package com.example.foro_cinev1.viewmodel

// Modelo de datos para una noticia
data class NewsItem(
    val id: Int,
    val title: String,
    val summary: String,
    val date: String,
    val category: String,
    val isFavorite: Boolean = false
)